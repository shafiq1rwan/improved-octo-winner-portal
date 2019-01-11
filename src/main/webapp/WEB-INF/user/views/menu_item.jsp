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
								<h1 class="main-title float-left">Menu Item List</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item active">Menu Item</li>
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
									<h3>Menu Item List</h3>
									<button type="button"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#createMenuItemModal" 
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Create Menu Item
									</button>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="menuItem_dtable"
											class="table table-bordered table-hover display nowrap"
											style="width: 100%">
											<thead>
												<tr>
													<th>ID</th>
													<th>Backend ID</th>
													<th>Name</th>
													<th>Price</th>
													<th>Type</th>
													<th>Status</th>	
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
		
		<!-- Modal -->
		<div class="modal fade" id="createMenuItemModal" role="dialog"
			aria-labelledby="createMenuItemModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="menuItemForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 ng-show="action=='create'" class="modal-title">Create Menu Item</h5>
		        			<h5 ng-show="action=='update'" class="modal-title">Edit Menu Item</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">
							<div class="form-section">
								
								<div class="row">
									<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
										<div class="form-group">
											<label for="menuItemName">Name</label>
    										<input type="text" class="form-control" id="menuItemName" placeholder="Name" ng-model = "menu_item.menu_item_name" required/>
										</div>
									</div>
									<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
										<div class="form-group">
											<label for="menuItemType">Item Type</label>
											<select id="menuItemType" class="form-control" ng-model="menu_item.menu_item_type" 
											ng-change="disableInputs = (menu_item.menu_item_type == 2)" 
											ng-options="mit.menu_item_type_id as mit.menu_item_type_name for mit in menu_item_types"
											required>
												<option value="" disabled>-- SELECT --</option>
											</select>																											
										</div>
									</div>
								</div>
								<div class="row">	
									<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">	
										<div class="form-group">
											<label for="menuItemBasePrice">Price</label>
    										<input type="number" class="form-control" id="menuItemBasePrice" ng-model = "menu_item.menu_item_base_price" min="0" required/>
										</div>			
										
										<div class="form-group">
											<label for="menuBackendId">Backend Id</label>
    										<input type="text" class="form-control" id="menuBackendId" ng-model = "menu_item.backend_id" required/>
										</div>	
		
										<div class="form-row">																			 
											<div class="form-group col-4 col-sm-4 col-md-4 col-lg-4 col-xl-6">
												<label for="taxable">Taxable</label>
												<input type="checkbox" id="taxable" ng-model = "menu_item.is_taxable" />
											</div>
											<div class="form-group col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6" ng-if="!disableInputs">
												<label for="discountable">Discountable</label>
												<input type="checkbox" id="discountable" ng-model = "menu_item.is_discountable" ng-disabled ="disableInputs"/>
											</div>

										</div>																		
									</div>
							<!-- 		<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">		
										<div class="form-group" ng-if="disableInputs">
											<label for="modifierGroup">Modifer Group</label>
											<select id="modifierGroup" class="form-control" ng-model="menu_item.modifier_group_id" ng-options="mg.id as mg.modifier_group_name for mg in modifier_groups">
												<option value="">-- SELECT --</option>
											</select>																											
										</div>
									</div> -->
								</div>
							
								<div class="row">	
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<label for="menuItemDescription">Description</label>
											<textarea style="resize:none;" rows="6" class="form-control" id="menuItemDescription" placeholder="Description" ng-model = "menu_item.menu_item_description">
											</textarea>
										</div>
									</div>
								</div>								
								<div class="row">
									 <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
									 	<div class="form-group">
									 		<img style="max-width:100%;max-height:100%;" ng-show="menu_item.image_path!=null"  ng-src="{{menu_item.image_path}}"/>
									 	</div>									
										<div class="form-group">		
								 			<p>{{menu_item.image_path}}</p>																		
											<label class="login-label">Image</label> 
											<input id="menuItemImage" type="file" accept="image/*"/> 
										</div>
									</div>
								</div>	
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">							
										<div class="form-group">																	
											<label for="modifierGroup">Modifier Groups</label> 
											<div class="pull-right">
												<button></button>
											</div>
											<div ng-if="">
												Group Sequence: {{}}
											</div>
											
											 	<select id="modifierGroup" multiple="multiple" class="form-control">
													<option></option>
													<option ng-repeat = "mg in modifier_groups" value ="{{mg.id}}">{{mg.modifier_group_name}}</option>
												</select>	 
												
												
										<!-- 	  	<select id="modifierGroup" multiple="multiple">
												   <option></option>
												  <option value="AL">Alabama</option>
												  <option value="WY">Wyoming</option>
												</select> -->
										</div>
									</div>
								</div>

							</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-show="action=='create'" ng-click="performMenuItemOperations('create')">Submit</button>
							<button class="btn btn-primary" type="submit" ng-show="action=='update'" ng-click="performMenuItemOperations('update')">Update</button>
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
		<!-- Combo Setting Modal -->
		<div class="modal fade" id="comboSettingModal" role="dialog"
			aria-labelledby="comboSettingModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="menuItemForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Combo Setting</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">	
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="pull-right" ng-if ="reordering_action === false">
													<button type="button"
														class="btn btn-info btn-primary bg-aqua"
														ng-click ="addNewTier()">
														<span class="btn-label"><i class="fa fa-plus"></i></span>
														Insert New Tier
													</button>	
										</div>
									</div>
								</div>		
						<div class="form-section" style="margin-top: 15px;">			
								<div class="row">	
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<ul class="list-group no-bullets" id="sortableList">
										  <li class="list-group-item" ng-repeat="x in tierItems">										  	
										  	<h5 ng-if="reordering_action"><i class="fa fa-reorder pull-right"></i></h5>							  	
										  	<div class="row">
											  	<div class="col-6 col-md-6">
													  	<h5>Name</h5>
													  	<p>{{x.name}}</p>
												</div>
												<div class="col-6 col-md-6">	
													  	<h5>Quantity</h5>
													  	<p>{{x.quantity}}</p>
												</div>
	
											</div> 
											<div class="row">
												<div class="col-6 col-md-6">
														<h5><i ng-click="openEditTier('update', x.id)" class="fa fa-edit pull-left" ng-if ="!reordering_action"></i></h5>
												</div>						 	
												<div class="col-6 col-md-6">
														 <h5><i ng-click="removeExistingTier(x.id)" class="fa fa-trash pull-right" ng-if ="!reordering_action"></i></h5>
												</div>
											</div>
										  </li>
										</ul>
										<div ng-if="tierItems.length === 0">
											<p>No Available Tier</p>
										</div>
									</div>
								</div>
								
							</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-if ="tierItems.length > 1" ng-hide="reordering_action" ng-click="reorderTierItems()">Reorder</button>
							<button class="btn btn-info" type="submit" ng-if ="reordering_action" ng-click="saveReordering()">Save</button>
							<button class="btn btn-danger" type="submit" ng-if ="reordering_action" ng-click="cancelReordering()">Cancel</button>				
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
		
		<!-- Tier Modal -->
		<div class="modal fade" id="tierModal" role="dialog"
			aria-labelledby="tierModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="menuItemForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">New Tier</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetNewTierModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">	
								<div class="form-section">
																			
									<div class="row">	
										<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
											<ul class="list-group no-bullets">
											  <li class="list-group-item">											  	
											  	<h5>Tier</h5>								  	
											  	<div class="form-row">
												  	<div class="form-group col-6 col-md-6">
														  	<label for="">Name</label>
														  	<input type="text" class="form-control" placeholder="Name" ng-model="tier_item.name" required/>
													</div>
													<div class="form-group col-6 col-md-6">	
														  	<label for="">Quantity</label>
														  	<input type="number" class="form-control" ng-model="tier_item.quantity" min=1 max=99 required/>
													</div>
												</div> 
											  </li>
											</ul>
										</div>
									</div>
									
								</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-hide= "tier_action === 'update'" ng-click="createNewTier()">Create</button>
							<button class="btn btn-info" type="submit" ng-show= "tier_action ==='update'" ng-click="editExistingTier(tier_item.id)">Update</button>
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
		
		
		
		
		
		
		
		
		
		
		
		
	</div>
</body>
</html>