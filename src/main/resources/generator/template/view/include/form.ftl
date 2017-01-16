<#list entity.id.fields as field>


		<div class="form-group">
			<label>${(field.name)}</label>
<#if entity.id.embeddedId>
			<input name="id.${field.name}" class="form-control" type="text" value="${'${' + '(entity.id.' + field.name + ')!}'}" placeholder="${field.comment!}" required />
<#else>
			<input name="${field.name}" class="form-control" type="text" value="${'${' + '(entity.' + field.name + ')!}'}" placeholder="${field.comment!}" required />
</#if>
		</div>
</#list>
<#list entity.fields as field>


		<div class="form-group">
			<label>${(field.name)}</label>
			<input name="${field.name}" class="form-control" type="text" value="${'${' + '(entity.' + field.name + ')!}'}" placeholder="${field.comment!}" required />
		</div>
</#list>