
package knl.tools;

import java.math.BigInteger;
import java.util.ArrayList;
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

import lombok.AllArgsConstructor;
import lombok.Data;

@EnableAutoConfiguration
public class Application implements CommandLineRunner {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		List<String> tables = this.jdbcTemplate.queryForList("show tables;", new HashMap<>(), String.class);
		
		List<Entity> entities = new ArrayList<>();
		
		for (String table : tables) {
			
			String sql = String.format("show full columns from %s", table);
			List<Map<String, Object>> columns = this.jdbcTemplate.queryForList(sql, new HashMap<>());
			
			entities.add(this.getEntity(columns));
		}
		
		System.out.println(entities);
	}
	
	private Entity getEntity(List<Map<String, Object>> columns) {
		
		List<Field> idFields = new ArrayList<>();
		List<Field> fields = new ArrayList<>();
		
		for (Map<String, Object> column : columns) {
			
			if (column.get("Key").equals("PRI")) {
				
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
				column.get("Field").toString(),
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
		
		public Boolean getEmbeddedId() {
			
			return fields.size() != 1;
		}
	}
	
	@Data
	@AllArgsConstructor
	public static class Field {
		
		private String name;
		
		private Class<?> className;
		
		private String comment;
		
		private Boolean autoIncrement;
	}
}
