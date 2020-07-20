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
								<h1 class="main-title float-left">Report</h1>
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
									<h3>Report</h3>
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
																<label>Starting Date</label>
																<md-datepicker id="startDate" ng-model="reportStartDate"
																	md-placeholder="Enter date" ng-change="dateChanged()" required></md-datepicker>
															</div>
														</div>

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label>Ending Date</label>
																<md-datepicker id="endDate" ng-model="reportEndDate"
																	md-min-date="reportStartDate"
																	md-placeholder="Enter date" required></md-datepicker>
															</div>
														</div>

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label for="menuItemType">Report Type</label> <select
																	id="reportType" class="form-control" ng-model="reportType"
																	ng-change="reportType.id == 1" 
																	ng-options="mit.id as mit.name for mit in reportTypeItem">
																</select>
															</div>
														</div>

														<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
															<div class="form-group">
																<label for="menuItemType">Branch State</label> <select
																	id="branchState" class="form-control"
																	ng-model="branchState" ng-options="mit.id as mit.name for mit in stateItem">
																</select>
															</div>
														</div>
													</div>
													<br>
													<br>
													<div class="row">
														<div class="col-sm-10">
															<div style="position: absolute; bottom: 0; right: 0;">
																<button id="" class="btn btn-block btn-primary"
																	type="button" ng-click="refreshTable()">Search</button>
															</div>
														</div>
														<div class="col-sm-2">
															<div
																style="position: absolute; padding-right: 14px; bottom: 0; right: 0;">
																<button class="btn btn-block btn-info" type="submit">Generate
																	Report</button>
															</div>
														</div>
													</div>
													<br />
													<div class="table-responsive">
														<table id="menuItem_dtable"
															class="table table-bordered" style="width: 100%">
															<thead>
																<tr>
																	<th>No</th>
																	<th>Branch Name</th>
																	<th>Branch Address</th>
																	<th>Staff Name</th>
																	<th>Payment Method</th>
																	<th>Payment Type</th>
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