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
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item active">Users</li>
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
									<h3>User List</h3>
									<button type="button"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#userModal" 
										data-keyboard="false" data-backdrop="static"
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Create New User
									</button>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="users_dtable"
											class="table table-bordered table-hover display"
											style="width: 100%">
											<thead>
												<tr>
													<th></th>
													<th>Name</th>
													<th>Email</th>
													<th>Mobile Number</th>
													<th>Username</th>
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


	<!-- USER MODAL STARTED -->
	<div class="modal fade" id="userModal" tabindex="-1" role="dialog" aria-labelledby="userModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="userForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'">Create User</h5>
		        <h5 ng-show="action=='update'">Edit User</h5>		
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
							<div class="form-group">
								<label class="login-label">Name</label>
								<input class="form-control" name="name" placeholder="Name" ng-model="user.name" type="text" required> 
							</div>
						</div>
					</div>	
					<div class="row">				
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Email</label>
								<input class="form-control" name="email" placeholder="johndoe@email.com" ng-model="user.email" type="email" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Mobile Number</label>
								<input class="form-control" name="mobile-number" placeholder="60161234567" ng-model="user.mobileNumber" type="text" required> 
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
							<div class="form-group">
								<label class="login-label">Address</label>
								<input class="form-control" name="address" placeholder="Name" ng-model="user.address" type="text" required> 
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Username</label>
								<input class="form-control" name="username" placeholder="Username" ng-model="user.username" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Password</label>
								<input class="form-control" name="password" placeholder="Password" ng-model="user.password" type="password" required> 
							</div>
						</div>					
					</div>
					<div class="row">
						<div class="col-md-8">
							<div class="custom-control custom-checkbox">
								<input type="checkbox" name="enabled" ng-model="user.enabled" class="custom-control-input" id="enabled">
								<label class="custom-control-label" for="enabled">Enabled</label>							
							</div>
						</div>	
					</div>
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitUser()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitUser()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	<!-- USER MODAL ENDED -->
	
	
	</div>
</body>
</html>