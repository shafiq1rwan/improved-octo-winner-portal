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
								<h1 class="main-title float-left">Sales by Payment Method
									Report</h1>
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item active">Report</li>
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
									<h3> </h3>
								</div>

								<div class="card-body">
									<div ng-init="initiation();">
										<div class="row"
											style="padding-right: 2px; padding-left: 2px;">
											<div class="col-md-12"
												style="padding-right: 2px; padding-left: 2px;">
												<form ng-submit="generateReport()">
													<div class="row">

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label>Starting Date</label><br>
																<md-datepicker id="startDate" ng-model="reportStartDate"
																	md-placeholder="Enter date" ng-change="dateChanged()"
																	required></md-datepicker>
															</div>
														</div>

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label>Ending Date</label><br>
																<md-datepicker id="endDate" ng-model="reportEndDate"
																	md-min-date="reportStartDate"
																	md-placeholder="Enter date" required></md-datepicker>
															</div>
														</div>

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label for="menuItemType">Store Name</label> <select
																	id="storeName" class="form-control"
																	ng-model="storeName" ng-change="storeName == 1"
																	ng-options="mit.id as mit.name for mit in storeItem">
																	<option value="" disabled selected hidden>Choose a store</option>
																</select>
															</div>
														</div>

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label for="menuItemType">Payment Type</label> <select
																	id="paymentType" class="form-control"
																	ng-model="paymentType"
																	ng-options="mit.id as mit.name for mit in paymentItem">
																	<option value="" disabled selected hidden>Choose payment type</option>
																</select>
															</div>
														</div>

													</div>
													<br> <br>
													<div class="row">
														<div class="col-sm-11">
															<div style="position: absolute; bottom: 0; right: 0; padding-right: 30px;">
																<button id="" class="btn btn-block btn-primary"
																	type="button" ng-click="refreshTable()">
																	<i class="fas fa-search"></i> Run
																</button>
															</div>
														</div>
														<div class="col-sm-1">
															<div
																style="position: absolute; padding-right: 14px; bottom: 0; right: 0;">
																<button class="btn btn-block"
																	style="background: #605ca8; color: white;"
																	type="submit">
																	<i class="fas fa-cloud-download"></i> Export
																</button>
															</div>
														</div>
													</div>
													<br />
													<div class="row">
														<table id="menuItem_dtable4" class="table table-bordered"
															style="width: 100%">
															<thead>
																<tr>
																	<th>No</th>
																	<th>Store Name</th>
																	<th>Store Address</th>
																	<!-- <th>Staff Name</th> -->
																	<th>Payment Type</th>
																	<th>Payment Method</th>
																	<th>Sales (RM)</th>
																	<th>Date</th>
																</tr>
															</thead>
														</table>
													</div>
												</form>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>