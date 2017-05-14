<table class="table table-striped table-hover">
	<thead>
		<tr>
<#list entity.id.fields as field>
			<th>
				${field.comment}
			</th>
</#list>
<#list entity.fields as field>
			<th>
				${field.comment}
			</th>
</#list>
			<th>
				Operation
			</th>
		</tr>
	</thead>
	<tbody>
		<#noparse><#list entities.content as entity></#noparse>
		<tr class="tr-event">
<#list entity.id.fields as field>
			${field.htmlTableTag}
</#list>
<#list entity.fields as field>
			${field.htmlTableTag}
</#list>
			<td><a href="${'${' + 'springMacroRequestContext.requestUri?html' + '}'}${entity.idPathFtlExpression}" class="btn btn-primary btn-xs">Edit</a></td>
		</tr>
		<#noparse></#list></#noparse>
	</tbody>
</table>