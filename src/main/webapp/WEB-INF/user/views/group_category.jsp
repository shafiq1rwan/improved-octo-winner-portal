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
	                                    <h1 class="main-title float-left">Group Category List</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item">Home</li>
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
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#createGroupCategoryModal">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Create Group Category
								</button>								
							</div>						
							<div class="card-body">							
								<div class="table-responsive">
									<table id="groupCategory_dtable" class="table table-bordered table-hover display" style="width:100%">
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
	<div class="modal fade" id="createGroupCategoryModal" role="dialog" aria-labelledby="createGroupCategoryModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title">Group Category Detail</h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">		       						
				  <form id="createStoreForm" action="${pageContext.request.contextPath}/user/signin" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
					<div class="form-section">
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" name="storeName" placeholder="Name" type="text" required> 
								</div>
							</div>
						</div>	
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<label class="login-label">Address</label>
									<input class="form-control" name="storeAddress" placeholder="Address" type="text" required> 
								</div>
							</div>
						</div>
					</div>		
				  </form>									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" type="submit"> Create</button>
		      </div>
		    </div>
		</div>
	</div>
</div>
</body>
</html>