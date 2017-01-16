package knl.tools.type;


import java.math.BigInteger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TypeMapping {
	
	INTEGER(Integer.class, "int", "number", null),
	LONG(Long.class, "int", "number", null),
	BIGINTEGER(BigInteger.class, "bitinteger", "number", null),
	DOUBLE(Double.class, "double", "number", null),
	STRING(String.class, "text", "text", null),
	DATETIME(DateTime.class, "datetime", "text", "%s.toString('yyyy/MM/dd HH:mm')"),
	LOCALDATE(LocalDate.class, "date", "text", "%s.toString('yyyy/MM/dd')"),
	BOOLEAN(Boolean.class, "tinyint", "number", "%s?c");
	
	@Getter
	private Class<?> clazz;

	@Getter
	private String databaseColumnType;

	@Getter
	private String htmlInputType;
	
	@Getter
	private String renderingTemplate;
}
