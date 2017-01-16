package ${packageName}.entity;

import javax.persistence.Entity;

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
	 * {@link ID}
	 */
	@javax.persistence.EmbeddedId
	private Id id; 
<#else>
<#list entity.id.fields as field>
	/**
	 * ${field.comment!}
	 */
	@javax.persistence.Id
	${field.autoIncrement?then('@javax.persistence.GeneratedValue', '')}
	${field.javaFieldDef}
</#list>
</#if>
<#list entity.fields as field>
	
	/**
	 * ${field.comment!}
	 */
	${field.javaFieldDef}
</#list>
<#if entity.id.embeddedId>
	
	@SuppressWarnings("serial")
	@Data
	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@javax.persistence.Embeddable
	public static class Id implements Serializable {
<#list entity.id.fields as field>
		
		/**
		 * ${field.comment!}
		 */
		${field.javaFieldDef}
</#list>
	}
</#if>
}