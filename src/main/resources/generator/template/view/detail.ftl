${'<#assign pageTitle="" />'}
${'<#include "../../shared/common.ftl">'}
${'<@layout.html pageTitle=pageTitle>'}

<div class="row">
	<div class="col-md-12">

		<form action="${'${' + 'springMacroRequestContext.requestUri?html' + '}'}" method="post">
			<input type="hidden" name="_method" value="put" />
				<#include './include/form.ftl' />
			<button type="submit" class="btn btn-info btn-xs">Update</button>
		</form>

		<form action="${'${' + 'springMacroRequestContext.requestUri?html' + '}'}" method="post">
			<input type="hidden" name="_method" value="delete" />
			<button type="submit" class="btn btn-danger btn-xs" onclick='return confirm("Confirm to delete");'>Delete</button>
		</form>

	</div>
</div>

${'</@layout.html>'}