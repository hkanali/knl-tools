<#list entity.id.fields as field>

			<#if !['createDatetime', 'updateDatetime', 'deleted']?seq_contains(field.name)>${field.htmlInputTag}</#if>
</#list>
<#list entity.fields as field>

			<#if !['createDatetime', 'updateDatetime', 'deleted']?seq_contains(field.name)>${field.htmlInputTag}</#if>
</#list>