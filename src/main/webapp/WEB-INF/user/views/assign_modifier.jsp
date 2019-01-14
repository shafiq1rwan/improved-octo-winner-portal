<!DOCTYPE html>
<html lang="en">
<style>
.form-section {
	padding-left: 15px;
	border-left: 2px solid #FF851B;
}

</style>
<body class="adminbody">

	<div id="main">
		<div class="content-page">
			<!-- Start content -->
			<div class="content">
				<div class="container-fluid">
					<div class="row">
						<div class="col-xl-12">
							<div class="breadcrumb-holder">
								<h1 class="main-title float-left"><a ng-href="${pageContext.request.contextPath}/user/#!Router_menu_item"><i class="fa fa-chevron-left"></i></a> &nbsp;Combo Setting</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item"><a ng-href="${pageContext.request.contextPath}/user/#!Router_menu_item">Menu Item</a></li>
									<li class="breadcrumb-item active">Assign Modifier</li>
								</ol>
								<div class="clearfix"></div>
							</div>
						</div>
					</div>
					<!-- end row -->
					<div class="row">
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
							<div class="card mb-3">
								<div class="card-header d-flex flex-row justify-content-between">
									<h3>Assigned Modifier Group</h3>
																		
									<button type="button" ng-click="getUnassignedModifierGroup()"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#modifierGroupSelectionModal">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Assign Modifier Group
									</button>
	
								</div>
								<div class="card-body">

									<div class="table-responsive" style="margin-top:10px;">
										<table id="assignedModifierGroup_dtable"
											class="table table-bordered table-hover display"
											style="width: 100%">
											<thead>
												<tr>
													<th>Sequence</th>
													<th>Name</th>
													<th>Action</th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<!-- end card-->
						</div>
					</div>
					<!-- end card-->
				</div>
				<!-- END container-fluid -->
			</div>
			<!-- END content -->
		</div>
		<!-- END content-page -->
		
		
		
			<!-- Modifier Group Selection Modal -->
		<div class="modal fade" id="modifierGroupSelectionModal" role="dialog"
			aria-labelledby="modifierGroupSelectionModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="modifierGroupSelectionForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Modifier Group Selection</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">
							<div class="form-section">
								<div class="row">
									<div class="col-12 col-md-12 col-sm-12 col-xs-12">
										<div class="">
											<table class="table table-bordered table-hover display nowrap" 
											 id="modifierGroup_dtable" style="width: 100%">
												<thead>
													<tr>
														<th></th>
														<th>Id</th>
														<th>Name</th>
													</tr>
												</thead>
												<tbody>
												</tbody>	
											</table>
										</div>
									</div>
								</div>							

								
							</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-click="assignModifierGroups()">Add</button>			
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
	</div>
</body>
</html>