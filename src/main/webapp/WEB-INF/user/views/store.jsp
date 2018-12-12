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
	                                    <h1 class="main-title float-left">Store List</h1>
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
								<h3>Store List</h3>								
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#createStoreModal">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Create Store
								</button>								
							</div>						
							<div class="card-body">							
								<div class="table-responsive">
									<table id="store_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Backend ID</th>
												<th>Name</th>
												<th>Image</th>
												<th>Country</th>
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
	
	
	<!-- Modal -->
	<div class="modal fade" id="createStoreModal" tabindex="-1" role="dialog" aria-labelledby="createStoreModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title">Store Detail</h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">		       						
				  <form id="createStoreForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
					<div class="form-section">
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Backend ID</label>
									<input class="form-control" name="storeBackendID" placeholder="Backend ID" ng-model="store.backendID" type="text" required > 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" name="storeName" placeholder="Name" ng-model="store.name" type="text" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Currency</label>
									<input class="form-control" name="storeCurrency" placeholder="MYR" ng-model="store.currency" type="text" required> 
								</div>
							</div>
						</div>	
						<div class="row">
							<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
								<div class="form-group">
									<label class="login-label">Address</label>
									<input class="form-control" name="storeAddress" placeholder="Address" ng-model="store.address" type="text" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Table Count</label>
									<input class="form-control" name="storeTableCount" placeholder="50" ng-model="store.tableCount" type="number" required> 
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Country</label>
									<input class="form-control" name="storeCountry" placeholder="Country" ng-model="store.country" type="text" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Longitude</label>
									<input class="form-control" name="storeLongitude" placeholder="Longitude" ng-model="store.longitude" type="number" step=".000001" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Latitude</label>
									<input class="form-control" name="storeLatitude" placeholder="Latitude" ng-model="store.latitude" type="number" step=".000001" required> 
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<label class="login-label">Image</label>
									<input class="form-control" type="file" name="files[]" id="storeImage" multiple="multiple"> 
								</div>
							</div>
						</div>	
					</div>		
				  </form>									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-click="createStore()"> Create</button>
		      </div>
		    </div>
		</div>
	</div>
</div>
</body>
</html>