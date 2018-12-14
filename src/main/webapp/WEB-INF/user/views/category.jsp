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
	                                    <h1 class="main-title float-left"><a ng-href="${pageContext.request.contextPath}/user/#!Router_group_category"><i class="fa fa-chevron-left"></i></a> &nbsp;Category</h1>
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
							<div class="card-header d-flex flex-row justify-content-between">
								<h3>Category List</h3>								
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#createCategoryModal" ng-click="category.is_active = true; setModalType('create')">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Create Category
								</button>								
							</div>	
							<div class="card-body">							
								<div class="table-responsive">
									<table id="category_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Backend ID</th>
												<th>Name</th>
												<th>Image</th>
												<th>Active Status</th>
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
	<div class="modal fade" id="createCategoryModal" tabindex="-1" role="dialog" aria-labelledby="createCategoryModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="createCategoryForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 class="modal-title">Category Detail</h5>
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">		       									  
					<div class="form-section">
						<div class="row">
							<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" name="categoryName" placeholder="Name" ng-model="category.category_name" type="text" required> 
								</div>
							</div>
							
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Active</label>
									<input class="form-control" name="categoryStatus" ng-model="category.is_active" type="checkbox"> 
								</div>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<label class="login-label">Description</label>
									<textarea style="resize:none;" rows="6" class="form-control" name="categoryDescription" placeholder="Category Description" ng-model="category.category_description"></textarea>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<label class="login-label">Image</label>
									<input class="form-control" type="file" name="files[]" id="categoryImage" multiple="multiple"> 
								</div>
							</div>
						</div>	
					</div>				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="createCategory()">Submit</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="updateCategory()">Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
</div>
</body>
</html>