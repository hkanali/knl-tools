
package knl.tools.type;

import java.math.BigInteger;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TypeMap {
	
	// @formatter:off
	LONG(Long.class,				"int.*",			"number",	"%s"),
	INTEGER(Integer.class,			"int.*",			"number",	"%s"),
	BIGINTEGER(BigInteger.class,	"bitint.*",			"number",	"%s"),
	DOUBLE(Double.class,			"double.*",			"number",	"%s"),
	STRING(String.class,			"text$|varchar.*",	"text",		"%s"),
	DATETIME(DateTime.class,		"datetime$",		"text",		"%s.toString('yyyy/MM/dd HH:mm')"),
	LOCALDATE(LocalDate.class,		"date$",			"text",		"%s.toString('yyyy/MM/dd')"),
	BOOLEAN(boolean.class,			"tinyint.*",		"number",	"%s?c");
	// @formatter:on
	
	@Getter
	private Class<?> clazz;
	
	@Getter
	private String databaseColumnTypePattern;
	
	@Getter
	private String htmlInputType;
	
	@Getter
	private String renderingTemplate;
	
	public static TypeMap of(Class<?> clazz) {
		
		for (TypeMap type : TypeMap.values()) {
			
			if (type.getClazz().equals(clazz)) {
				
				return type;
			}
		}
		
		throw new RuntimeException();
	}
	
	public static TypeMap of(String columnType) {
		
		for (TypeMap type : TypeMap.values()) {
			
			if (Pattern.compile(type.getDatabaseColumnTypePattern()).matcher(columnType).matches()) {
				
				return type;
			}
		}
		
		throw new RuntimeException();
	}
}
