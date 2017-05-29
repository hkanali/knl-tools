package knl.tools.dto;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import knl.tools.type.TypeMap;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Entity {

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

		private static final Map<String, String> FIELD_ENUM_CLASS_NAME_MAP;

		static {

			FIELD_ENUM_CLASS_NAME_MAP = new HashMap<>();
			FIELD_ENUM_CLASS_NAME_MAP.put("genderId", "gender");
		}

		private String name;

		private Class<?> clazz;

		private String comment;

		private boolean primary;

		private boolean autoIncrement;

		private boolean multiPrimary;

		public boolean isEnumId() {

			if (this.name.endsWith("TypeId")) {

				return true;
			}

			return FIELD_ENUM_CLASS_NAME_MAP.keySet().contains(this.name);
		}

		public String getEnumClassName() {

			return LOWER_CAMEL.to(UPPER_CAMEL, this.getEnumInstanceName());
		}

		public String getEnumInstanceName() {

			if (this.name.endsWith("TypeId")) {

				return this.name.replaceAll("TypeId", "Type");
			}

			return FIELD_ENUM_CLASS_NAME_MAP.get(this.name);
		}

		public String getJavaFieldDef() {

			return String.format("private %s %s;", this.clazz.getName(), this.name);
		}

		public String getHtmlInputTag() {

			String attrName;
			if (this.primary && this.autoIncrement && !this.multiPrimary) {

				return "<input name=\"id\" type=\"hidden\" value=\"${(entity.id)!}\" />";
			} else if (this.primary && !this.autoIncrement && this.multiPrimary) {

				attrName = "id." + this.name;
			} else {

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
			} else if (this.isEnumId()) {

				// @formatter:off
				return String.format(""
					+ "<div class=\"form-group\">"
						+ "<label class=\"control-label\" for=\"%s\">%s</label>"
						+ "<select id=\"%s\" name=\"%s\" class=\"form-control\">"
							+ "<#list %ss as type>"
							+ "<option value=\"${type.id}\" ${((%s == type.id)!false)?then('selected=\"selected\"', '')}>${type}</option>"
							+ "</#list>"
						+ "</select>"
					+ "</div>",
						attrName,
						this.comment,
						attrName,
						attrName,
						this.getEnumInstanceName(),
						attrName);
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
			} else if (this.primary && !this.autoIncrement && this.multiPrimary) {

				attrName = "id." + this.name;
			} else {

				attrName = this.name;
			}

			if (this.name.endsWith("TypeId")) {

				attrName = attrName.replace("Id", "");
			} else if (FIELD_ENUM_CLASS_NAME_MAP.keySet().contains(this.name)) {

				attrName = attrName.replace(this.name, FIELD_ENUM_CLASS_NAME_MAP.get(this.name));
			}

			TypeMap typeMap = TypeMap.of(this.clazz);

			return String.format(
				// @formatter:off
				"<td>${(%s)!}</td>",
					String.format(typeMap.getRenderingTemplate(), "entity." + attrName));
				// @formatter:on
		}
	}
}
