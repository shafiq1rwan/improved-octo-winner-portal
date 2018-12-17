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
								<h1 class="main-title float-left">Modifier Group List</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item">Home</li>
									<li class="breadcrumb-item">Management</li>
									<li class="breadcrumb-item active">Modifier Group</li>
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
									<h3>Modifier Group List</h3>
									<button type="button"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#createModifierGroupModal" 
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Create Modifier Group
									</button>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="modifierGroup_dtable"
											class="table table-bordered table-hover display"
											style="width: 100%">
											<thead>
												<tr>
													<th>ID</th>
													<th>Backend ID</th>
													<th>Name</th>
													<th>Active Status</th>
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
		
			<!-- Modal -->
		<div class="modal fade" id="createModifierGroupModal" role="dialog"
			aria-labelledby="createModifierGroupModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="modifierGroupForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Group Category Detail</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">
							<div class="form-section">
								
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
			
										<div class="form-row">
											<div class="form-group col-md-8">
												<label class="login-label">Name</label> 
												<input
													class="form-control" name="modifierGroupName"
													placeholder="Name" type="text"
													ng-model="modifier_group.modifier_group_name" required>
											</div>
											
											<div class="form-group col-md-4">
												<label class="login-label">Is Active</label> 
												<input
													class="form-control" name="modifierGroupActiveStatus"
													type="checkbox"
													ng-model="modifier_group.is_active">
											</div>	
										</div>
										
										
									</div>
								</div>
								
					
		
							</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-show="action=='create'" ng-click="performModifierGroupOperations('create')">Submit</button>
							<button class="btn btn-primary" type="submit" ng-show="action=='update'" ng-click="performModifierGroupOperations('update')">Update</button>
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	</div>
</body>
</html>