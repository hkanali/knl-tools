package ${packageName}.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Table
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ${tableUpperCamel} extends BaseEntity {
	
<#if entity.id.embeddedId>
	/**
	 * {@link Id}
	 */
	@javax.persistence.EmbeddedId
	private Id id; 
<#else>
<#list entity.id.fields as field>
	/**
	 * ${field.comment!}
	 */
	@javax.persistence.Id
<#if field.autoIncrement>
	@javax.persistence.GeneratedValue
</#if>
	${field.javaFieldDef}
</#list>
</#if>
<#list entity.fields as field>
	
	/**
	 * ${field.comment!}
	 */
	${field.javaFieldDef}
	<#if field.enumId>
	/**
	 * 
	 */
	public ${packageName}.type.${field.enumClassName} get${field.enumClassName}() {
	
		return ${packageName}.type.${field.enumClassName}.of(this.${field.name});
	}
	
	/**
	 *
	 */
	public void set${field.enumClassName}(${packageName}.type.${field.enumClassName} type) {
	
		this.${field.name} = type.getId();
	}
	</#if>
</#list>
<#if entity.id.embeddedId>
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@javax.persistence.Embeddable
	public static class Id implements java.io.Serializable {
<#list entity.id.fields as field>
		
		/**
		 * ${field.comment!}
		 */
		${field.javaFieldDef}
		<#if field.enumId>
		/**
		 * 
		 */
		public ${packageName}.type.${field.enumClassName} get${field.enumClassName}() {
		
			return ${packageName}.type.${field.enumClassName}.of(this.${field.name});
		}
		
		/**
		 *
		 */
		public void set${field.enumClassName}(${packageName}.type.${field.enumClassName} type) {
		
			this.${field.name} = type.getId();
		}
		</#if>
</#list>
	}
</#if>
}