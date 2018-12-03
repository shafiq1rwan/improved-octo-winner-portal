<!DOCTYPE html>
<html lang="en">
<body class="adminbody">

<div id="main">
    <div class="content-page">
		<!-- Start content -->
        <div class="content">        
			<div class="container-fluid">				
				<div class="row">
						<div class="col-xl-12">
								<div class="breadcrumb-holder">
	                                    <h1 class="main-title float-left">Category</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item">Home</li>
											<li class="breadcrumb-item active">Category</li>
	                                    </ol>
	                                    <div class="clearfix"></div>
	                            </div>
						</div>
				</div>
          	 	<!-- end row -->
				<div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">						
						<div class="card mb-3">
							<div class="card-header">
								<h3> Add Category</h3>
							</div>						
							<div class="card-body">							
								<form action="${pageContext.request.contextPath}/user/signin" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
									<div class="col-lg-6">
										<div class="form-group">
											<label class="login-label">Backend ID</label>
											<input class="form-control" name="backendId" placeholder="Backend ID" type="text" required> 
										</div>	
										<div class="form-group">
											<label class="login-label">Name</label>
											<input class="form-control" name="categoryName" placeholder="Name" type="text" required> 
										</div>
										<div class="form-group">
											<label class="login-label">Description</label>
											<input class="form-control" name="categoryDescription" placeholder="Description" type="text" required> 
										</div>
										<div class="form-group">
											<label class="login-label">Image</label>
											<input class="form-control" type="file" name="files[]" id="filer_example1" multiple="multiple"> 
										</div>	
										<div class="form-group">
											<label class="login-label">Image</label>
											<input class="form-control" type="file" name="files[]" id="filer_example2" multiple="multiple"> 
										</div>								
										<div class="form-group text-right m-b-0">
                                            <button class="btn btn-primary" type="submit">
                                                Submit
                                            </button>
                                            <!-- <button type="reset" class="btn btn-secondary m-l-5">
                                                Cancel
                                            </button> -->
                                        </div>										
									</div>		
								</form>		
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