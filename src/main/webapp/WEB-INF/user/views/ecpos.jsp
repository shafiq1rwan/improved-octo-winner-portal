<!DOCTYPE html>
<html lang="en">
<style>
.form-section {
   padding-left:15px;
   border-left:2px solid #FF851B;
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
	                                    <h1 class="main-title float-left"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store"><i class="fa fa-chevron-left"></i></a> &nbsp;ECPOS</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store">Store</a></li>
											<li class="breadcrumb-item active">ECPOS</li>
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
								<h3>ECPOS Info</h3>
								<div class="btn-group">								
									<button type="button" class="btn btn-social pull-right btn-primary bg-aqua ml-3" data-toggle="modal" data-target="#staffListModal">
										<span class="btn-label"><i class="fa fa-edit"></i></span> Manage Staff
									</button>
									<button type="button" class="btn btn-social pull-right btn-success bg-aqua ml-3" data-toggle="modal" data-target="#tableListModal">
										<span class="btn-label"><i class="fa fa-edit"></i></span> Manage Table
									</button>
								</div>									
							</div>						
							<div class="card-body">													
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<h6><label>Brand ID : {{store.brand_id}}</label></h6>
											<h6><label>Store ID : {{store.id}}</label></h6>
											<h6><label>Backend ID : {{store.backend_id}}</label></h6>
											<h6><label>Store Name : {{store.name}}</label></h6>
										</div>
									</div>									
								</div>								
										
								<hr>
								<div class="form-section" id="test">
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation ID</label>
												<input class="form-control" ng-model="ecpos.activation_id" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation Key</label>
												<input class="form-control" ng-model="ecpos.activation_key"  type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Date Created</label>
												<input class="form-control" ng-model="ecpos.created_date" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Status</label>
												<input class="form-control" ng-model="ecpos.status" type="text" disabled>
											</div>
										</div>											
									</div>
									
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">MAC Address</label>
												<input class="form-control" ng-model="ecpos.mac_address"  type="text" disabled>
											</div>
										</div>									
									</div>		
										
								</div>							
							</div>
							<div class="card-footer">
								<div class="btn-toolbar justify-content-end" role="toolbar" aria-label="Toolbar with button groups">
									<button type="button" ng-show="showActivation" ng-click="generateActivation()" class="btn btn-success">Generate Activation ID</button>															
			  						<button type="button" ng-click="resendActivationInfo()" class="btn btn-outline-secondary">Resend Activation ID</button>
			  						<button type="button" ng-click="reactivateDevice()" class="btn btn-outline-secondary">Reactivate ECPOS</button>
			  						<button type="button" ng-click="terminateDevice()" class="btn btn-outline-secondary">Terminate ECPOS</button>		  				
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
	
	<!-- Staff List Modal -->
	<div class="modal fade" id="staffListModal" role="dialog" aria-labelledby="staffListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Staff List</h5>	
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="staffList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Name</th>
												<th>Username</th>
												<th>Status</th>
												<th>Action</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#staffModal" ng-click="modalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Staff
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Staff QR Modal -->
	<div class="modal fade" id="staffQRModal" tabindex="-1" role="dialog" aria-labelledby="staffQRModal" aria-hidden="true">
		<div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5>Staff QR</h5>
		        <button type="button" class="close" ng-click="closeQRModel()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body text-center">
		        <img class="img-fluid" alt="Responsive image" data-ng-src="data:image/png;base64,{{qrImgData}}">				  				 									
		      </div>      
		    </div>
		</div>
	</div>
	
	<!-- Create Staff Modal -->
	<div class="modal fade" id="staffModal" tabindex="-1" role="dialog" aria-labelledby="staffModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'">Create Staff</h5>
		        <h5 ng-show="action=='update'">Edit Staff</h5>		
		        <button type="button" class="close" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Name</label>
								<input class="form-control" name="staffName" placeholder="Name" ng-model="staff.name" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Email</label>
								<input class="form-control" name="staffEmail" placeholder="johndoe@email.com" ng-model="staff.email" type="email" required> 
							</div>
						</div>
					</div>	
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Mobile Phone</label>
								<input class="form-control" name="staffMobilePhone" placeholder="60161234567" ng-model="staff.mobilePhone" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Role</label>
								<select class="form-control" ng-model="staff.role" ng-options="x.role_name for x in roleList track by x.id" required>
								<option value="" disabled>Please choose a role</option>
								</select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Username</label>
								<input class="form-control" name="staffUsername" placeholder="Username" ng-model="staff.username" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Password</label>
								<input class="form-control" name="staffPassword" placeholder="Password" ng-model="staff.password" type="password" required> 
							</div>
						</div>					
					</div>
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitStaff()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitStaff()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Table List Modal -->
	<div class="modal fade" id="tableListModal" role="dialog" aria-labelledby="tableListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="tableListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Table List</h5>	
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="tableList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Table Name</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#tableModal" ng-click="tableModalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Table
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Create Table Modal -->
	<div class="modal fade" id="tableModal" tabindex="-1" role="dialog" aria-labelledby="tableModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="actionTable == 'create'">Create Table</h5>
		        <h5 ng-show="actionTable == 'update'">Edit Table</h5>		
		        <button type="button" class="close" ng-click="resetTableModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Name</label>
								<input class="form-control" name="tableName" placeholder="Name" ng-model="table.name" type="text" required> 
							</div>
						</div>
					</div>	
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="actionTable == 'create'" type="submit" ng-click="submitTable()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="actionTable == 'update'" type="submit" ng-click="submitTable()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Loading Modal [START] -->
	<div class="modal fade" data-backdrop="static" id="loading_modal" role="dialog">
		<div class="modal-dialog h-100 d-flex flex-column justify-content-center my-0 modal-sm">
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