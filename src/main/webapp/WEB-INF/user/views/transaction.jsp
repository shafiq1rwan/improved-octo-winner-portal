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
	                                    <h1 class="main-title float-left"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store"><i class="fa fa-chevron-left"></i></a> &nbsp;Transactions</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store">Store</a></li>
											<li class="breadcrumb-item active">Transactions</li>
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
								<h3>Transaction List</h3>								
								<!-- <button type="button" class="btn btn-social pull-right btn-primary bg-aqua" ng-click="addDevice()">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Add BYOD
								</button> -->									
							</div>						
							<div class="card-body">							
								<div class="table-responsive">
									<table id="datatable_transactions" class="table table-bordered table-hover display nowrap" style="width:100%">
										<thead>
											<tr>
												<th>Check No</th>
												<th>By</th>
												<th>Transaction Type</th>
												<th>Payment Method</th>
												<th>Payment Type</th>
												<th>Amount</th>
												<th>Status</th>
												<th>Date</th>
												<th></th> 
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>								
							</div>	
						</div>																			
					</div>														
				</div><!-- end card-->					
            </div>	
            <!-- END container-fluid -->			
		</div>
		<!-- END content -->	
    </div>
	<!-- END content-page -->
	
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