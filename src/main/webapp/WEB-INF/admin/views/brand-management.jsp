<!DOCTYPE html>
<html>
<style>
.form-section {
	padding-left: 15px;
	border-left: 2px solid #FF851B;
}

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
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item">Brands</li>
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
								<h3>Brand List</h3>
								
									<button type="button" ng-show="role === 'ROLE_SUPER_ADMIN'"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#brandModal"
										data-keyboard="false" data-backdrop="static" 
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Create Brand
									</button>
													
							</div>	
							<div class="card-body">	
									<div class="table-responsive">
										<table id="brands_dtable" class="table table-bordered table-hover display" style="width:100%">
											<thead>
												<tr>
													<th>Brand Name</th>
													<th>Action</th>
												</tr>
											</thead>										
											<tbody>									
											</tbody>
										</table>
									</div>	

							</div>								
						</div><!-- end card-->						
					</div>														
				</div><!-- end card-->					
            </div>	
            <!-- END container-fluid -->			
		</div>
		<!-- END content -->
    </div>
	<!-- END content-page -->
	
	<!-- BRAND MODAL STARTED -->
	<div class="modal fade" id="brandModal" tabindex="-1" role="dialog" aria-labelledby="brandModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="brandForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'">Create Brand</h5>
		        <h5 ng-show="action=='update'">Edit Brand</h5>		
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
							<div class="form-group">
								<label class="login-label">Brand Name</label>
								<input class="form-control" name="name" placeholder="Name" ng-model="brand.name" type="text" required> 
							</div>
						</div>
					</div>
					
					<div class ="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Db Domain</label>
								<input class="form-control" name="name" placeholder="Name" ng-model="brand.dbDomain" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Db Name</label>
								<input class="form-control" name="name" placeholder="Name" ng-model="brand.dbName" type="text" required> 
							</div>
						</div>
					</div>
					
					<div class ="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Db Username</label>
								<input class="form-control" name="dbUsername" placeholder="username" ng-model="brand.dbUsername" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Db Password</label>
								<input class="form-control" name="dbPassword" placeholder="password" ng-model="brand.dbPassword" type="password" required> 
							</div>
						</div>
					</div>
					
					<div class ="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Db Port</label>
								<input class="form-control" name="name" placeholder="dbPort" ng-model="brand.dbPort" type="number" required> 
							</div>
						</div>
					</div>
					
					
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitBrand()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitBrand()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	<!-- BRAND MODAL ENDED -->
	
	<!-- BRAND USER MODAL STARTED -->
	<div class="modal fade" id="brandUserModal" tabindex="-1" role="dialog" aria-labelledby="brandUserModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="brandUserForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Assigned User(s)</h5>	
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="table-wrapper-scroll-y">
											<table class="table">
												<thead>
													<tr>
														<th>Username</th>
														<th>Email</th>
														<th>Selected</th>
													</tr>
												</thead>
			
												<tbody>
													<tr ng-repeat="user in users">
															<td>{{user.username}}</td>
															<td>{{user.email}}</td>
															<td >
																<input type="checkbox" ng-model="user.exist"
																ng-change="addIntoUserList(user)"/>				
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
		      	<button class="btn btn-primary" type="submit" ng-click="assignUser()">Assign</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	<!-- BRAND USER MODAL ENDED -->

	</div>
</body>
</html>