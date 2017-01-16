
package knl.tools;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Data;

@EnableAutoConfiguration
public class Application implements CommandLineRunner {
	
	private static final String TEMPLATE_LOADER_PATH = "generator/template";
	
	private static final String OUTPUT_DIR = "src/main/resources/generator/output";
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		List<String> tables = this.jdbcTemplate.queryForList("show tables;", new HashMap<>(), String.class);
		
		for (String table : tables) {
			
			Map<String, Object> model = new HashMap<>();
			
			String sql = String.format("show full columns from %s", table);
			List<Map<String, Object>> columns = this.jdbcTemplate.queryForList(sql, new HashMap<>());
			
			Entity entity = this.getEntity(columns);
			
			String tableUpperCamel = LOWER_UNDERSCORE.to(UPPER_CAMEL, table);
			String tableLowerCamel = LOWER_UNDERSCORE.to(LOWER_CAMEL, table);
			
			model.put("packageName", "knl");
			model.put("tableUpperCamel", tableUpperCamel);
			model.put("entity", entity);
			
			// @formatter:off
			// entity
			this.print(model, entity, "entity.ftl",		 OUTPUT_DIR + "/entity/" + tableUpperCamel + ".java");
			
			// repository
			this.print(model, entity, "repository.ftl",	 OUTPUT_DIR + "/repository/" + tableUpperCamel + "Repository.java");

			// repository
			this.print(model, entity, "form.ftl",		 OUTPUT_DIR + "/form/Admin" + tableUpperCamel + "Form.java");

			// admin controller
			this.print(model, entity, "controller.ftl",	 OUTPUT_DIR + "/controller/Admin" + tableUpperCamel + "Controller.java");

			// admin view index
			this.print(model, entity, "view/index.ftl",	 OUTPUT_DIR + "/view/" + tableLowerCamel + "/index.html");

			// admin view detail
			this.print(model, entity, "view/detail.ftl", OUTPUT_DIR + "/view/" + tableLowerCamel + "/detail.html");
			// @formatter:on
		}
	}
	
	private void print(Map<String, Object> model, Entity entity, String templateName, String outputFileName) {
		
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
		
		for (Map<String, Object> column : columns) {
			
			if (column.get("Key").toString().equalsIgnoreCase("PRI")) {
				
				idFields.add(this.getField(column));
			}
			else {
				
				fields.add(this.getField(column));
			}
		}
		
		return new Entity(new Id(idFields), fields);
	}
	
	private Field getField(Map<String, Object> column) {
		
		// @formatter:off
		return new Field(
				LOWER_UNDERSCORE.to(LOWER_CAMEL, column.get("Field").toString()),
				this.getClassName(column.get("Type").toString()),
				StringUtils.defaultString(column.get("Comment").toString(), null),
				String.valueOf(column.get("Extra")).contains("auto_increment")
			);
		// @formatter:on
	}
	
	private Class<?> getClassName(String columnType) {
		
		if (columnType.toLowerCase().startsWith("int")) {
			
			return Long.class;
		}
		else if (columnType.toLowerCase().startsWith("bigint")) {
			
			return BigInteger.class;
		}
		else if (columnType.toLowerCase().startsWith("varchar") || columnType.toLowerCase().startsWith("text")) {
			
			return String.class;
		}
		else if (columnType.toLowerCase().startsWith("double")) {
			
			return Double.class;
		}
		else if (columnType.toLowerCase().startsWith("tinyint")) {
			
			return boolean.class;
		}
		else if (columnType.toLowerCase().equals("date")) { // #equals
			
			return LocalDate.class;
		}
		else if (columnType.toLowerCase().equals("datetime")) { // #equals
			
			return DateTime.class;
		}
		
		throw new RuntimeException(String.format("unsupported type. %s", columnType));
	}
	
	@Data
	@AllArgsConstructor
	public static class Entity {
		
		private Id id;
		
		private List<Field> fields;
	}
	
	@Data
	@AllArgsConstructor
	public static class Id {
		
		private List<Field> fields;
		
		public boolean isEmbeddedId() {
			
			return fields.size() != 1;
		}
	}
	
	@Data
	@AllArgsConstructor
	public static class Field {
		
		private String name;
		
		private Class<?> className;
		
		private String comment;
		
		private boolean autoIncrement;
		
		public boolean isEnumId() {
			
			return Arrays.asList().contains(this.name);
		}
		
		public String getJavaFieldDef() {
			
			return String.format("private %s %s;", this.className.getName(), this.name);
		}
	}
	
	public interface DriverMode {
		
	}
}
