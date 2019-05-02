
package knl.tools;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import knl.tools.config.ProjectProperties;
import knl.tools.dto.Entity;
import knl.tools.dto.Entity.Field;
import knl.tools.dto.Entity.Id;
import knl.tools.type.TypeMap;

@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {
	
	private static final String TEMPLATE_LOADER_PATH = "generator/template";
	
	private static final String OUTPUT_DIR = "src/main/resources/generator/output";

	private static final List<String> SYSTEM_COLUMNS_FIELD_NAMES = Arrays.asList("create_date", "update_date");
	
	@Autowired
	private ProjectProperties projectProperties;
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		this.clean();
		
		List<String> tables = this.jdbcTemplate.queryForList("show tables;", new HashMap<>(), String.class);
		
		for (String table : tables) {
			
			Map<String, Object> model = new HashMap<>();
			
			String sql = String.format("show full columns from %s", table);
			List<Map<String, Object>> columns = this.jdbcTemplate.queryForList(sql, new HashMap<>());
			
			Entity entity = this.getEntity(columns);
			
			String tableUpperCamel = LOWER_UNDERSCORE.to(UPPER_CAMEL, table);
			String tableLowerCamel = LOWER_UNDERSCORE.to(LOWER_CAMEL, table);
			
			model.put("packageName", this.projectProperties.getPackageName());
			model.put("tableUpperCamel", tableUpperCamel);
			model.put("tableLowerCamel", tableLowerCamel);
			model.put("entity", entity);
			
			// @formatter:off
			// entity
			this.print(model, "entity.ftl",			OUTPUT_DIR + "/entity/" + tableUpperCamel + ".java");
			
			for (Field field : entity.getId().getFields()) {
				
				if (!field.isEnumId()) {
					
					continue;
				}
				
				model.put("enumClassName", field.getEnumClassName());
				
				// enum
				this.print(model, "enum.ftl",			OUTPUT_DIR + "/enum/" + field.getEnumClassName() + ".java");
			}
			
			for (Field field : entity.getFields()) {
				
				if (!field.isEnumId()) {
					
					continue;
				}
				
				model.put("enumClassName", field.getEnumClassName());
				
				// enum
				this.print(model, "enum.ftl",			OUTPUT_DIR + "/enum/" + field.getEnumClassName() + ".java");
			}
			
			// repository
			this.print(model, "repository.ftl",		OUTPUT_DIR + "/repository/" + tableUpperCamel + "Repository.java");

			// repository
			this.print(model, "form.ftl",			OUTPUT_DIR + "/form/Admin" + tableUpperCamel + "Form.java");

			// admin controller
			this.print(model, "controller.ftl",		OUTPUT_DIR + "/controller/Admin" + tableUpperCamel + "Controller.java");

			// admin view index
			this.print(model, "view/index.ftl",		OUTPUT_DIR + "/view/" + tableLowerCamel + "/index.ftl");

			// admin view detail
			this.print(model, "view/detail.ftl",	OUTPUT_DIR + "/view/" + tableLowerCamel + "/detail.ftl");
			// @formatter:on
		}
	}
	
	private void clean() {
		
		File file = new File(OUTPUT_DIR);
		file.delete();
	}
	
	private void print(Map<String, Object> model, String templateName, String outputFileName) {
		
		FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
		factory.setTemplateLoaderPath(TEMPLATE_LOADER_PATH);
		factory.setDefaultEncoding("UTF-8");
		
		File outputDir = new File(this.getOutputDir(outputFileName));
		outputDir.mkdirs();
		
		File output = new File(outputFileName);
		
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output)))) {
			
			Configuration configuration = factory.createConfiguration();
			Template template = configuration.getTemplate(templateName);
			template.process(model, pw);
		}
		catch (Exception e) {
			
			throw new IllegalStateException(e);
		}
	}
	
	private String getOutputDir(String outputFileName) {
		
		return outputFileName.substring(0, outputFileName.lastIndexOf("/"));
	}
	
	private Entity getEntity(List<Map<String, Object>> columns) {
		
		List<Field> idFields = new ArrayList<>();
		List<Field> fields = new ArrayList<>();
		
		int primaryCounter = 0;
		for (Map<String, Object> column : columns) {
			
			if (column.get("Key").toString().equalsIgnoreCase("PRI")) {
				
				primaryCounter++;
			}
		}
		
		boolean multiPrimary = primaryCounter >= 2;
		
		for (Map<String, Object> column : columns) {
			
			if (SYSTEM_COLUMNS_FIELD_NAMES.contains(column.get("Field").toString())) {
				
				continue;
			}
			
			if (column.get("Key").toString().equalsIgnoreCase("PRI")) {
				
				idFields.add(this.getField(column, true, multiPrimary));
			}
			else {
				
				fields.add(this.getField(column, false, multiPrimary));
			}
		}
		
		return new Entity(new Id(idFields), fields);
	}
	
	private Field getField(Map<String, Object> column, boolean primary, boolean multiPrimary) {
		
		// @formatter:off
		return new Field(
				LOWER_UNDERSCORE.to(LOWER_CAMEL, column.get("Field").toString()),
				this.getClazz(column.get("Type").toString()),
				StringUtils.defaultString(column.get("Comment").toString(), null),
				primary,
				String.valueOf(column.get("Extra")).contains("auto_increment"),
				multiPrimary
			);
		// @formatter:on
	}
	
	private Class<?> getClazz(String columnType) {
		
		return TypeMap.of(columnType).getClazz();
	}
	
	public interface DriverMode {
		
	}
}
