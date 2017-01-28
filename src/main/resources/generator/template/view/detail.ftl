
<div class="row">
	<div class="col-md-12">

		<form action="${'${' + 'currentUrl' + '}'}" method="post">
			<input type="hidden" name="_method" value="put" />
				<#include './include/form.ftl' />
			<button type="submit" class="btn btn-info btn-xs">更新</button>
		</form>

		<form action="${'${' + 'currentUrl' + '}'}" method="post">
			<input type="hidden" name="_method" value="delete" />
			<button type="submit" class="btn btn-danger btn-xs" onclick='return confirm("削除します。\nよろしいですか？");'>削除</button>
		</form>

	</div>
</div>