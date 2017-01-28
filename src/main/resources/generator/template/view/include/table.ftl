<table class="table table-striped table-hover">
	<thead>
<#list entity.id.fields as field>
		<tr>
			${field.comment}
		</tr>
</#list>
<#list entity.fields as field>
		<tr>
			${field.comment}
		</tr>
</#list>
		<tr>
			操作
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
			<td><a href="${'${' + 'currentUrl' + '}'}${entity.idPathFtlExpression}" class="btn btn-primary btn-xs">編集</a></td>
		</tr>
		<#noparse></#list></#noparse>
	</tbody>
</table>