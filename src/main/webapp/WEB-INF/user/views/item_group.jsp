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
	                                    <h1 class="main-title float-left">Item Group List</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item active">Item Group</li>
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
								<h3>Item Group List</h3>								
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#itemGroupModal" ng-click="modalType('create')">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Add Item Group
								</button>									
							</div>						
							<div class="card-body">													
								<div class="table-responsive">
									<table id="itemGroupList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Backend ID</th>
												<th>Name</th>
												<th>Date Created</th>
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
	
	<!-- Item Group Modal -->
	<div class="modal fade" id="itemGroupModal" tabindex="-1" role="dialog" aria-labelledby="itemGroupModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'">Create Item Group</h5>
		        <h5 ng-show="action=='update'">Edit Item Group</h5>		
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
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
								<input class="form-control" name="staffEmail" placeholder="johndoe@email.com" ng-model="staff.email" type="text" required> 
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
								<select class="form-control" ng-model="staff.role" ng-options="x.role_name for x in roleList track by x.id">
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
	
	<!-- Item Modal -->
	<div class="modal fade" id="menuItemModal" tabindex="-1" role="dialog" aria-labelledby="menuItemModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Menu Item List</h5>
					<button type="button" class="close" data-dismiss="modal" ng-click="closeMenuItemModal()" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
							<ul class="list-group" id="sortableList">
								<li class="list-group-item pl-0 pr-0 pt-1 pb-1" ng-repeat="item in selectedItemList">
									<div class="d-flex flex-row">
										<div class="p-1">
											<img style="width:75px; heigh: 75px;" src="{pageContext.request.contextPath}/{{item.menu_item_image_path}}" class="rounded-circle"/>
										</div>
										<div class="pl-3 pt-2 pr-2 pb-2 flex-grow-1  border-left">
											<h5 class="card-title">
												{{item.menu_item_name}} <sub style="font-size:60%">{{item.menu_item_type_name}}</sub>
											</h5>
											<h6 class="text-info">{{item.backend_id}}</h6>
										</div>
										<div class="pr-1">
											<button class="btn btn-outline"></button>
										</div>
									</div>
								</li>
								<li class="list-group-item pl-0 pr-0 pt-1 pb-1" ng-if="selectedItemList.length===0">
									There is no Items assigned.
								</li>
							</ul>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn btn-danger" ng-show="assign_item_action =='Edit'" ng-click="unassignAll()" ng-disabled="selectedItemList.length === 0">Unassigned All Items</button>
					<button class="btn btn-default" ng-click="openAssignItemsModal()">Assign Items</button>
		      		<button class="btn btn-primary" ng-show="assign_item_action =='New'" ng-click="submitAssignedItems('New')" ng-disabled="selectedItemList.length === 0">Submit</button>
		      		<button class="btn btn-primary" ng-show="assign_item_action =='Edit'" ng-click="submitAssignedItems('Edit')">Update</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Items Assign & Unassign -->
	<div class="modal fade" id="assignItemsModal" role="dialog"
		aria-labelledby="assignItemsModal" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<form id="assignItemForm" method="POST" accept-charset="UTF-8"
					role="form" class="form-signin">
					<div class="modal-header">
						<h5 class="modal-title">Assign Item</h5>
						<button type="button" class="close" data-dismiss="modal"></button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
</body>
</html>