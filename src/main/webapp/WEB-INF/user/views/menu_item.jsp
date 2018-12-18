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
									<li class="breadcrumb-item">Home</li>
									<li class="breadcrumb-item active">Item</li>
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
											class="table table-bordered table-hover display"
											style="width: 100%">
											<thead>
												<tr>
													<th>ID</th>
													<th>Backend ID</th>
													<th>Name</th>
													<th>Image Path</th>
													<th>Price</th>
													<th>Type</th>
													<th>Taxable</th>
													<th>Discountable</th>	
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
							<h5 class="modal-title">Menu Item Detail</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">
							<div class="form-section">
								
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<label for="menuItemName">Name</label>
    										<input type="text" class="form-control" id="menuItemName" placeholder="Name" ng-model = "menu_item.menu_item_name" required/>
										</div>
										
										<div class="form-group">
											<label for="menuItemType">Item Type</label>
											<select id="menuItemType" class="form-control" ng-model="menu_item.menu_item_type" 
											ng-change="disableInputs = (menu_item.menu_item_type == 2)" 
											ng-options="mit.menu_item_type_id as mit.menu_item_type_name for mit in menu_item_types"
											required>
												<option value="" disabled>-- SELECT --</option>
											</select>																											
										</div>
										
										<div class="form-group" ng-if="disableInputs">
											<label for="modifierGroup">Modifer Group</label>
											<select id="modifierGroup" class="form-control" ng-model="menu_item.modifier_group_id" ng-options="mg.id as mg.modifier_group_name for mg in modifier_groups">
												<option value="">-- SELECT --</option>
											</select>																											
										</div>
									
										<div class="form-group">
											<label for="menuItemDescription">Description</label>
											<textarea style="resize:none;" rows="6" class="form-control" id="menuItemDescription" placeholder="Description" ng-model = "menu_item.menu_item_description" required>
											</textarea>
										</div>
										
										<div class="form-group">
											<label for="menuItemBasePrice">Price</label>
    										<input type="number" class="form-control" id="menuItemBasePrice" ng-model = "menu_item.menu_item_base_price" min="0" required/>
										</div>
																				
										<div class="form-row" ng-if="!disableInputs">
																					 
											<div class="form-group col-md-4">
												<label for="taxable">Taxable</label>
												<input type="checkbox" id="taxable" ng-model = "menu_item.is_taxable" ng-disabled="disableInputs" />
											</div>
				
											<div class="form-group col-md-4">
												<label for="discountable">Discountable</label>
												<input type="checkbox" id="discountable" ng-model = "menu_item.is_discountable" ng-disabled ="disableInputs" />
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	</div>
</body>
</html>