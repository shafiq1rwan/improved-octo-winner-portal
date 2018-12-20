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
								<h1 class="main-title float-left">Combo</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item">Home</li>
										<li class="breadcrumb-item">Item</li>
									<li class="breadcrumb-item active">Combo</li>
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
									<h3>Combo List</h3>
																		
									<button type="button"
										class="btn btn-social pull-right btn-primary bg-aqua"
										data-toggle="modal" data-target="#comboItemSelectionModal" 
										ng-click="changeComboItemType('MI')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Assign Item/Item Group
									</button>
	
								</div>
								<div class="card-body">
											
								  <ul class="nav nav-tabs" role="tablist">
								    <li class="nav-item" ng-repeat="combo_detail in combo_details">
								      <a class="nav-link" ng-class="{'active': $first}" data-toggle="tab" ng-click="changeTableData(combo_detail.id)">{{combo_detail.name}}</a>
								    </li>
								  </ul>

									<div class="table-responsive">
										<table id="combo_dtable"
											class="table table-bordered table-hover display"
											style="width: 100%">
											<thead>
												<tr>
													<th></th>
													<th>ID</th>
													<th>Name</th>
													<th>Type</th>
													<th>Price</th>
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
		
		
		
			<!-- Combo Item Selection Modal -->
		<div class="modal fade" id="comboItemSelectionModal" role="dialog"
			aria-labelledby="comboItemSelectionModal" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="comboItemSelectionForm" method="POST" accept-charset="UTF-8"
						role="form" class="form-signin">
						<div class="modal-header">
							<h5 class="modal-title">Item Selection</h5>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>

						<div class="modal-body">
							<div class="form-section">

							  <ul class="nav nav-pills" role="tablist">
							    <li class="nav-item">
							      <a class="nav-link active" data-toggle="pill" href="" ng-click="changeComboItemType('MI')">Menu Item</a>
							    </li>
							    <li class="nav-item">
							      <a class="nav-link" data-toggle="pill" href="" ng-click="changeComboItemType('G')">Item Group</a>
							    </li>
							  </ul>
		
								<div class="row">
									<div class="col-12 col-md-12 col-sm-12 col-xs-12">
										<div class="">
											<table class="table table-bordered table-hover display nowrap" 
											 id="comboItem_dtable" style="width: 100%">
												<thead>
													<tr>
														<th></th>
														<th>Name</th>
														<th>Price</th>
													</tr>
												</thead>
												<tbody>
												</tbody>	
											</table>
										</div>
									</div>
								</div>							

								
							</div>
						</div>

						<div class="modal-footer">
							<button class="btn btn-primary" type="submit" ng-click="">Add</button>			
						</div>
					</form>

				</div>
			</div>
		</div>
		<!-- END Modal -->
		
	</div>
</body>
</html>