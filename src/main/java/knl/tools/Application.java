
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import knl.tools.type.TypeMap;
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
		
		this.clean();
		
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
			model.put("tableLowerCamel", tableLowerCamel);
			model.put("entity", entity);
			
			// @formatter:off
			// entity
			this.print(model, entity, "entity.ftl",			OUTPUT_DIR + "/entity/" + tableUpperCamel + ".java");
			
			// repository
			this.print(model, entity, "repository.ftl",		OUTPUT_DIR + "/repository/" + tableUpperCamel + "Repository.java");

			// repository
			this.print(model, entity, "form.ftl",			OUTPUT_DIR + "/form/Admin" + tableUpperCamel + "Form.java");

			// admin controller
			this.print(model, entity, "controller.ftl",		OUTPUT_DIR + "/controller/Admin" + tableUpperCamel + "Controller.java");

			// admin view index
			this.print(model, entity, "view/index.ftl",		OUTPUT_DIR + "/view/" + tableLowerCamel + "/index.html");

			// admin view detail
			this.print(model, entity, "view/detail.ftl",	OUTPUT_DIR + "/view/" + tableLowerCamel + "/detail.html");
			// @formatter:on
		}
	}
	
	private void clean() {
		
		File file = new File(OUTPUT_DIR);
		file.delete();
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
		
		int primaryCounter = 0;
		for (Map<String, Object> column : columns) {
			
			if (column.get("Key").toString().equalsIgnoreCase("PRI")) {
				
				primaryCounter++;
			}
		}
		
		boolean multiPrimary = primaryCounter >= 2;
		
		for (Map<String, Object> column : columns) {
			
			// TODO system columns
			if (Arrays.asList("create_date", "update_date").contains(column.get("Field").toString())) {
				
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
	
	@Data
	@AllArgsConstructor
	public static class Entity {
		
		private Id id;
		
		private List<Field> fields;
		
		public String getIdPathExpression() {
			
			if (!id.isEmbeddedId()) {
				
				return "/{" + id.fields.get(0).name + "}";
			}
			
			String result = "";
			for (Field field : id.fields) {
				
				result += "/" + field.getName() + "/{" + field.getName() + "}";
			}
			
			return result;
		}
		
		public String getIdPathFtlExpression() {
			
			if (!id.isEmbeddedId()) {
				
				return "/${entity." + id.fields.get(0).name + "}";
			}
			
			String result = "";
			for (Field field : id.fields) {
				
				result += "/" + field.getName() + "/${entity.id." + field.getName() + "}";
			}
			
			return result;
		}
		
		public String getIdControllerParamExpression() {
			
			String result = "";
			for (Field field : id.fields) {
				
				result += "@PathVariable " + field.getClazz().getName() + " " + field.getName() + ", ";
			}
			
			return result;
		}
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
		
		private Class<?> clazz;
		
		private String comment;
		
		private boolean primary;
		
		private boolean autoIncrement;
		
		private boolean multiPrimary;
		
		public boolean isEnumId() {
			
			return Arrays.asList().contains(this.name);
		}
		
		public String getJavaFieldDef() {
			
			return String.format("private %s %s;", this.clazz.getName(), this.name);
		}
		
		public String getHtmlInputTag() {
			
			String attrName;
			if (this.primary && this.autoIncrement && !this.multiPrimary) {
				
				return "<input name=\"id\" type=\"hidden\" value=\"${(entity.id)!}\" />";
			}
			else if (this.primary && !this.autoIncrement && this.multiPrimary) {
				
				attrName = "id." + this.name;
			}
			else {
				
				attrName = this.name;
			}
			
			TypeMap typeMap = TypeMap.of(this.clazz);
			
			if (typeMap == TypeMap.BOOLEAN) {
				
				// @formatter:off
				return String.format(this.comment
					+ "<div class=\"checkbox\">"
						+ "<label>"
							+ "<input name=\"%s\" type=\"checkbox\" value=\"1\" ${((%s)!true)?then('checked=\"checked\"', '')} data-toggle=\"toggle\"/>"
						+ "</label>"
					+ "</div>", attrName, "entity." + attrName);
				// @formatter:on
			}
			
			return "<div class=\"form-group\">"
				
				+ "<label>" + this.comment + "</label>"
				
				+ String.format(
					// @formatter:off
						"<input"
						+ " name=\"%s\""
						+ " class=\"form-control\""
						+ " type=\"%s\""
						+ " value=\"${(%s)!}\""
						+ " placeholder=\"%s\" required />",
							attrName,
							typeMap.getHtmlInputType(),
							String.format(typeMap.getRenderingTemplate(), "entity." + attrName),
							this.comment)
				+ "</div>";
				// @formatter:on
		}
		
		public String getHtmlTableTag() {
			
			String attrName;
			if (this.primary && this.autoIncrement && !this.multiPrimary) {
				
				attrName = "id";
			}
			else if (this.primary && !this.autoIncrement && this.multiPrimary) {
				
				attrName = "id." + this.name;
			}
			else {
				
				attrName = this.name;
			}
			
			TypeMap typeMap = TypeMap.of(this.clazz);
			
			return String.format(
				// @formatter:off
				"<td>${(%s)!}</td>",
					String.format(typeMap.getRenderingTemplate(), "entity." + attrName));
				// @formatter:on
		}
	}
	
	public interface DriverMode {
		
	}
}
