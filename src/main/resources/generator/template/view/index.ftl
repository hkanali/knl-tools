${'<#assign pageTitle="" />'}
${'<#include "../../utils/common.html">'}
${'<@layout.html pageTitle=pageTitle>'}

<div class="row">

	<div class="col-md-12">

		<div>
			<a class="btn btn-primary" role="button" data-toggle="collapse"
				href="#collapseRegisterForm" aria-expanded="false" aria-controls="collapseRegisterForm">
				Create
			</a>
			<div class="collapse" id="collapseRegisterForm">
				<form action="${'${' + 'springMacroRequestContext.requestUri?html' + '}'}" method="post">

<#include "./include/form.ftl" />

					<button type="submit" class="btn btn-primary btn-xs">Save</button>
				</form>
			</div>
		</div>

	</div>

	<div class="col-md-12 table-responsive">

<#include "./include/table.ftl" />

	</div>

</div>

${'</@layout.html>'}