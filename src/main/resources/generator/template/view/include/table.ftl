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
		</tr>
		<#noparse></#list></#noparse>
	</tbody>
</table>