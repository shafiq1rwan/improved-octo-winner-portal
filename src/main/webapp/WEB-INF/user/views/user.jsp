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
	                                    <h1 class="main-title float-left">User List</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item active">User Configuration</li>
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
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#storeModal" ng-click="modalType('create')">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Create User
								</button>									
							</div>						
							<div class="card-body">							
								<div class="table-responsive">
									<table id="user_dtable" class="table table-bordered table-hover display nowrap" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Username</th>
												<th>Name</th>
												<th>Enabled Status</th>
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
	
	
	<!-- Store Modal -->
	<div class="modal fade" id="storeModal" tabindex="-1" role="dialog" aria-labelledby="storeModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="storeForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'" class="modal-title">Create User</h5>
		        <h5 ng-show="action=='update'" class="modal-title">Edit User</h5>
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
									<input class="form-control" name="storeName" placeholder="John Doe" ng-model="store.name" type="text" required> 
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Email</label>
									<input class="form-control" name="storeCurrency" placeholder="john.doe@gmail.com" ng-model="store.currency" type="text" required> 
								</div>
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Mobile Number</label>
									<input class="form-control" name="storeCurrency" placeholder="60161234567" ng-model="store.currency" type="text" required> 
								</div>
							</div>
						</div>	
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<label class="login-label">Address</label>
									<input class="form-control" name="storeAddress" placeholder="USJ 21, Subang Jaya" ng-model="store.address" type="text" required> 
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Username</label>
									<input class="form-control" name="storeAddress" placeholder="Username" ng-model="store.address" type="text" required> 
								</div>
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Password</label>
									<input class="form-control" name="storeAddress" placeholder="Password" ng-model="store.address" type="text" required> 
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<label for="taxable">Enabled</label>
								<input type="checkbox" id="taxable" ng-model = "menu_item.is_taxable" />
							</div>
						</div>											
					</div>			 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitStore()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitStore()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
</div>
</body>
</html>