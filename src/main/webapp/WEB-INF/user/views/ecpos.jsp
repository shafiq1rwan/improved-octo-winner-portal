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
											<li class="breadcrumb-item">Home</li>
											<li class="breadcrumb-item active">Store</li>
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
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#storeModal" ng-click="modalType('create')">
									<span class="btn-label"><i class="fa fa-edit"></i></span> Manage Employee
								</button>									
							</div>						
							<div class="card-body">						
								<div class="form-section">
									<div class="row">
										<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
											<div class="form-group">
												<h6><label>Store ID : Test</label></h6>
												<h6><label>Backend ID : Test</label></h6>
												<h6><label>Store Name : Test</label></h6>
											</div>
										</div>									
									</div>								
											
									<hr>
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Backend ID</label>
												<input class="form-control" ng-model="store.name" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation ID</label>
												<input class="form-control" ng-model="store.name" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation Key</label>
												<input class="form-control" ng-model="store.name" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Date Created</label>
												<input class="form-control" ng-model="store.name" type="text" disabled>
											</div>
										</div>										
									</div>	
										
								</div>							
							</div>
							<div class="card-footer">
								<div class="btn-toolbar justify-content-end" role="toolbar" aria-label="Toolbar with button groups">
									<button type="button" class="btn btn-success">Generate Activation ID</button>															
			  						<button type="button" class="btn btn-outline-secondary">Resend Activation ID</button>
			  						<button type="button" class="btn btn-outline-secondary">Reactivate ECPOS</button>
			  						<button type="button" class="btn btn-outline-secondary">Terminate ECPOS</button>		  				
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
		        <h5 ng-show="action=='create'" class="modal-title">Create Store</h5>
		        <h5 ng-show="action=='update'" class="modal-title">Edit Store</h5>
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="store_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Backend ID</th>
												<th>Name</th>
												<th>Image</th>
												<th>Country</th>
												<th>Publish Status</th>
												<th>Action</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitStore()"> Submit</button>
		      	<button class="btn btn-success" ng-show="action=='create'" type="submit" ng-click="submitStore(1)"> Submit & Publish</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitStore()"> Update</button>
		      	<button class="btn btn-success" ng-show="action=='update'" type="submit" ng-click="submitStore(1)"> Update & Publish</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
</div>
</body>
</html>