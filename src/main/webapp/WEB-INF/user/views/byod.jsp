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
	                                    <h1 class="main-title float-left"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store"><i class="fa fa-chevron-left"></i></a> &nbsp;BYOD</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store">Store</a></li>
											<li class="breadcrumb-item active">BYOD</li>
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
								<h3>BYOD Info</h3>								
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" ng-click="addDevice()">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Add BYOD
								</button>									
							</div>						
							<div class="card-body">													
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<h6><label>Store ID : {{store.id}}</label></h6>
											<h6><label>Backend ID : {{store.backend_id}}</label></h6>
											<h6><label>Store Name : {{store.name}}</label></h6>
											<h6><label>Number of BYOD : {{store.byod_count}}</label></h6>
										</div>
									</div>									
								</div>
							</div>
						</div>								
						<div class="card mb-3 shadow" ng-repeat="item in store.byod">				
							<div class="card-body">		
								<div class="form-section" id="test">
									<h5>BYOD # {{store.byod.length - $index}}</h5>
									<br>
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation ID</label>
												<input class="form-control" ng-model="item.activation_id" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation Key</label>
												<input class="form-control" ng-model="item.activation_key"  type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Date Created</label>
												<input class="form-control" ng-model="item.created_date" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Status</label>
												<input class="form-control" ng-model="item.status" type="text" disabled>
											</div>
										</div>										
									</div>
									
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">MAC Address</label>
												<input class="form-control"  type="text" disabled>
											</div>
										</div>																			
									</div>		
										
								</div>							
							</div>
							<div class="card-footer">
								<div class="btn-toolbar justify-content-end" role="toolbar" aria-label="Toolbar with button groups">														
			  						<button type="button" class="btn btn-outline-secondary">Resend Activation ID</button>
			  						<button type="button" ng-click="reactivateDevice(item.activation_id)" class="btn btn-outline-secondary">Reactivate BYOD</button>
			  						<button type="button" ng-click="terminateDevice(item.activation_id)" class="btn btn-outline-secondary">Terminate BYOD</button>		  				
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
	
</div>
</body>
</html>