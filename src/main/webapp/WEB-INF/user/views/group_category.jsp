<!DOCTYPE html>
<html lang="en">
<style>
.table-wrapper-scroll-y {
display: block;
max-height: 200px;
overflow-y: auto;
-ms-overflow-style: -ms-autohiding-scrollbar;
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
								<h1 class="main-title float-left">Group Category List</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item active">Group Category</li>
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
									<h3>Group Category List</h3>
									<button type="button"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#createGroupCategoryModal" 
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Create Group Category
									</button>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="groupCategory_dtable"
											class="table table-bordered table-hover display nowrap"
											style="width: 100%">
											<thead>
												<tr>
													<th>ID</th>
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


		<!-- Modal -->
		<div class="modal fade" id="createGroupCategoryModal" role="dialog"
			aria-labelledby="createGroupCategoryModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="groupCategoryForm" method="POST" accept-charset="UTF-8"
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
										<div class="form-group">
											<label class="login-label">Name</label> 
											<input
												class="form-control" name="groupCategoryName"
												placeholder="Name" type="text"
												ng-model="group_category.group_category_name" required>
										</div>
									</div>
								</div>
								
							 	<div class="row" ng-if="stores.length > 0">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
									
									<div class="table-wrapper-scroll-y">
											<table class="table">
												<thead>
													<tr>
														<th>Id</th>
														<th>Store Name</th>
														<th>Selected</th>
													</tr>
												</thead>
			
												<tbody>
													<tr ng-repeat="store in stores">
															<td>{{store.id}}</td>
															<td>{{store.store_name}}</td>
															<td >
																	<input type="checkbox" ng-model="store.is_checked" ng-if="action =='create'"
																	ng-change="addStoreIntoGroupCategory(store)"/>
																	
																	<input type="checkbox" ng-model="store.is_checked" ng-if="action =='update'" ng-init = "store.is_checked = store.group_category_id === group_category.id"
																	ng-change="editStoreIntoGroupCategory(store)" ng-checked="store.group_category_id === group_category.id"/>						
															</td>
													</tr>
												</tbody>
											</table>
										</div>
										
									</div>
								</div>
		
							</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-show="action=='create'" ng-click="createGroupCategory()">Submit</button>
							<button class="btn btn-primary" type="submit" ng-show="action=='update'" ng-click="updateGroupCategory()">Update</button>
						</div>
					</form>

				</div>
			</div>
		</div>
		
		<!-- Tax List Modal -->
	<div class="modal fade" id="taxListModal" role="dialog" aria-labelledby="taxListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Tax List</h5>	
		        <button type="button" class="close" ng-click="closeTaxListModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
						<table id="taxList_dtable" class="table table-bordered table-hover display" style="width:100%">
							<thead>
								<tr>
									<th>ID</th>
									<th>Name</th>
									<th>Rate (Percentage %)</th>
									<th>Active Status</th>
								</tr>
							</thead>										
							<tbody>									
							</tbody>
						</table>
					</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" ng-click="openTaxDetailModal('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Tax
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Tax Detail Modal -->
		<div class="modal fade" id="taxDetailModal" role="dialog"
			aria-labelledby="taxModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="modifierGroupForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Tax Detail</h5>
							<button type="button" class="close" ng-click="closeTaxDetailModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body">
							<div class="form-section">							
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">			
										<div class="form-group">
											<label class="login-label">Name</label>
											<input class="form-control" name="taxName" type="text" ng-model="tax.name" required> 
										</div>																	
									</div>
								</div>
								<div class="row">
									<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">		
										<div class="form-group">
											<label class="login-label">Rate (Percentage %)</label>
											<input class="form-control" name="taxRate" type="number" min="1" max="100" step="1" ng-model="tax.rate" required> 
										</div>																	
									</div>
									<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
										<div class="form-group">
											<label class="login-label">Tax Type</label>
											<select class="form-control" ng-model="tax.type" ng-options="x.tax_name for x in taxType track by x.id" required>
												<option value="" disabled>Please choose a tax type</option>
											</select>
										</div>																	
									</div>
								</div>
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">			
										<div class="form-group">
											<div class="custom-control custom-checkbox">
												<input type="checkbox" name="taxStatus" ng-model="tax.is_active" class="custom-control-input" id="customCheck1">
												<label class="custom-control-label" for="customCheck1">Active</label>
											</div>
										</div>																	
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">						
							<button class="btn btn-primary" type="submit" ng-show="taxAction=='create'" ng-click="postTaxRequest()">Submit</button>
							<button class="btn btn-primary" type="submit" ng-show="taxAction=='update'" ng-click="postTaxRequest()">Update</button>
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
		<!-- Loading Modal [START] -->
		<div class="modal" id="loading_modal" tabindex="-1" role="dialog">
			<div class="modal-dialog modal-dialog-centered modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-body">
						<div class="text-center">
							<img style="width:75%" src="${pageContext.request.contextPath}/assets/images/byodadmin/gif/loading.gif"><br>
								<span>Loading...</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- Loading Modal [END] -->
	</div>
</body>
</html>