
<div class="row">

	<div class="col-md-12">

		<div>
			<a class="btn btn-primary" role="button" data-toggle="collapse"
				href="#collapseRegisterForm" aria-expanded="false" aria-controls="collapseRegisterForm">
				CREATE
			</a>
			<div class="collapse" id="collapseRegisterForm">
				<form action="${'${' + 'currentUrl' + '}'}" method="post">

<#include "./include/form.ftl" />

					<button type="submit" class="btn btn-primary btn-xs">CREATE</button>
				</form>
			</div>
		</div>

	</div>

	<div class="col-md-12 table-responsive">

<#include "./include/table.ftl" />

	</div>

</div>