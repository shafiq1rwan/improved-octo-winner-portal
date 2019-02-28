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
								<h1 class="main-title float-left">Modifier Group List</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item active">Modifier Group</li>
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
									<h3>Modifier Group List</h3>
									<button type="button"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#createModifierGroupModal" data-backdrop="static" data-keyboard="false"
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Create Modifier Group
									</button>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="modifierGroup_dtable" class="table table-bordered table-hover display" style="width: 100%">
											<thead>
												<tr>
													<th>ID</th>
													<th>Name</th>
													<th>Active Status</th>
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
		
		<!-- Modifier Group Modal -->
		<div class="modal fade" id="createModifierGroupModal" role="dialog"
			aria-labelledby="createModifierGroupModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="modifierGroupForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Modifier Group Detail</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body">
							<div class="form-section">							
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">			
										<div class="form-row">
											<div class="form-group col-md-8">
												<label class="login-label">Name</label> 
												<input class="form-control" name="modifierGroupName" placeholder="Name" type="text" ng-model="modifier_group.modifier_group_name" required>
											</div>																	
										</div>
										<div class="form-row">
											<div class="form-group col-md-8">
												<div class="custom-control custom-checkbox">
													<input type="checkbox" name="modifierGroupActiveStatus" ng-model="modifier_group.is_active" class="custom-control-input" id="customCheck1">
													<label class="custom-control-label" for="customCheck1">Active</label>
												</div>
											</div>
										</div>																	
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">						
							<button class="btn btn-primary" type="submit" ng-show="action=='create'" ng-click="performModifierGroupOperations('create')">Submit</button>
							<button class="btn btn-primary" type="submit" ng-show="action=='update'" ng-click="performModifierGroupOperations('update')">Update</button>
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
		<!-- Modal START -->
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
								  				<img  style="width:75px; height:75px;" src="{{item.menu_item_image_path}}" class="rounded-circle" />  				
								  			</div>
								  			<div class="pl-3 pt-2 pr-2 pb-2 flex-grow-1  border-left">				  		
							  					<h5 class="card-title">
							  						{{item.menu_item_name}} <sub style="font-size:60%">{{item.menu_item_type_name}}</sub>							  					
												</h5>
							  					<h6 class="text-info">{{item.backend_id}}</h6>				  				
											</div>
											<div class="pr-1">
												<button class="btn btn-outline-danger border-0" type="button" ng-click="unassignItem(item.id)"><i class="fa fa-window-close"></i></button>
												<i class="fas fa-arrows-alt pull-right"></i>
											</div>									
										</div>
									</li>
									<li class="list-group-item pl-0 pr-0 pt-1 pb-1" ng-if="selectedItemList.length===0">
									 	<div class="p-1">
									 		There is no item assigned.
									 	</div>
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
		<!-- Modal END -->
	
		<!-- Modal Assign Item STARTED -->
		<div class="modal fade" id="assignItemsModal" role="dialog" aria-labelledby="assignItemsModal" aria-hidden="true">
			<div class="modal-dialog modal-xl">
				<div class="modal-content">
					<form id="assignItemsForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Assign Items</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="clearAssignItemModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">	
						<div class="container">
							<div class="row">
								<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">						
									<input class="form-control" id="searchbox-input" type="text" placeholder="Search..">
								</div>
								<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
									<div class="form-group">
										<select class="form-control" id="filterList" ng-click="event = $event" ng-change="filterOnChange(event)"  ng-model="filterSelected" ng-options="x.name for x in filterList track by x.id">									
										</select>
									</div>
								</div>	
							</div>
							<div class="row">
								<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">	
									  	<div class="d-flex flex-row flex-wrap card-container">
									  		<div class="col-md-3 pt-3 pr-2" ng-repeat="item in itemList" ng-show="item.isAssigned == false">							  		
											    <div class="card custom-card" ng-click="selectCard(item)">
											        <div class="card-body p-1 ">
											            <div class="d-flex flex-row">
												  			<div class="w-25 d-flex flex-row justify-content-center" style="text-align:center">
																<input class="align-self-center" type="checkbox" id="checkBox{{item.id}}" ng-checked="item.check" aria-label="...">
															</div>
												  			<div class="p-1 w-75 border-left ">
												  				<span class="mb-1" style="font-size:75%;">{{item.menu_item_type_name}}</span>
												  				<h6 class="card-title">{{item.menu_item_name}}</h6>
												  				<h6 class="text-info">{{item.backend_id}}</h6>				  				
															</div>
														</div>
											        </div>
											    </div>					    
											</div>
										 	<div class="p-1" ng-if="emptyList">
										 		You have assigned all items or there is no item created.
										 	</div>
										</div>
									</div>										
								</div>
							</div>				
						</div>
						<div class="modal-footer">	
							<button class="btn btn-secondary" ng-click="closeAssignItemsModal()">Cancel</button>					
							<button class="btn btn-primary" type="submit" ng-click="assignItems()">Assign</button>
						</div>
					</form>
				</div>
			</div>
		</div>		
		<!-- Modal Assign Item ENDED -->		
	</div>
</body>
</html>