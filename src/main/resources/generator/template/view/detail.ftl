
<div class="row">
	<div class="col-md-12">

		<form action="${'${' + 'currentUrl' + '}'}" method="post">
			<input type="hidden" name="_method" value="put" />
				<#include './include/form.ftl' />
			<button type="submit" class="btn btn-primary btn-xs">UPDATE</button>
		</form>

		<form action="${'${' + 'currentUrl' + '}'}" method="post">
			<input type="hidden" name="_method" value="delete" />
			<button type="submit" class="btn btn-primary btn-xs" onclick='return confirm("Confirm to delete.");'>DELETE</button>
		</form>

	</div>
</div>