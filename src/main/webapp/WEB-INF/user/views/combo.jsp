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
										ng-click="setModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span>
										Add Item/Item Group
									</button>
	
								</div>
								<div class="card-body">
											
								  <ul class="nav nav-tabs" role="tablist">
								    <li class="nav-item" ng-repeat="combo_detail in combo_details">
								      <a class="nav-link" ng-class="{'active': $first}" data-toggle="tab" ng-click="changeTableData(combo_detail.id)">{{combo_detail.combo_detail_name}}</a>
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

	</div>
</body>
</html>