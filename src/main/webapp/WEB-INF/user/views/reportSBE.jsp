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
								<h1 class="main-title float-left">Sales by Employee Report</h1>
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
								<div class="card-body">
									<div ng-init="initiation();">
										<div class="row"
											style="padding-right: 2px; padding-left: 2px;">
											<div class="col-md-12"
												style="padding-right: 2px; padding-left: 2px;">
												<form ng-submit="generateReport()">
												<div class="card" style="background-color: lightgoldenrodyellow;">
													<div class="row" style="padding-top: 10px; padding-left: 6px; padding-right: 6px;">
														<div
															class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
															<table>
																<tbody>
																	<tr>
																		<td style="padding-right: 10px;"><div class="form-group">
																				<label for="startDate">Starting Date</label><br>
																				<md-datepicker id="startDate"
																					ng-model="reportStartDate"
																					md-placeholder="Enter date"
																					ng-change="dateChanged()" required></md-datepicker>
																			</div></td>
																		<td style="padding-right: 10px;"><div class="form-group">
																				<label for="endDate">Ending Date</label><br>
																				<md-datepicker id="endDate" ng-model="reportEndDate"
																					md-min-date="reportStartDate"
																					md-placeholder="Enter date" required></md-datepicker>
																			</div></td>
																		<td style="padding-right: 10px;"><div class="form-group">
																				<label for="storeName">Store Name</label> <select
																					id="storeName" class="form-control"
																					ng-model="storeName"
																					ng-options="mit.id as mit.name for mit in storeItem">
																					<option value="" disabled selected hidden>Choose a store</option>
																					<option value="0">All Store</option> 
																				</select>
																			</div></td>
																		<td style="padding-right: 10px;"><div class="form-group">
																				<label for="menuItemType">Employee
																				Name</label> <select id="employeeName" class="form-control"
																					ng-model="employeeName"
																					ng-options="mit.id as mit.name for mit in employeeItem">
																						<option value="" disabled selected hidden>Choose
																							employee</option>
																				</select>
																			</div></td>
																		<td
																			style="padding-top: 10px; padding-right: 10px"><div
																				class="form-group">
																				<label for="startDate"> </label><br>
																				<button id="" class="btn btn-primary"
																					type="button" ng-click="refreshTable()">
																					<i class="fas fa-search"></i> Search
																				</button>
																			</div></td>
																		<td style="padding-top: 10px"><div
																				class="form-group">
																				<label for="startDate"> </label><br>
																				<button class="btn btn-success"
																					type="submit" data-toggle="tooltip" 
																					data-placement="top" title="Export as Excel!">
																					<i class="fas fa-download"></i> Export
																				</button>
																			</div></td>
																	</tr>
																</tbody>
															</table>
														</div>
													</div></div>
													<!-- <div class="row">
														<div
															class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
															<table>
																<tbody>
																	<tr>
																		<td style="padding-right: 10px;"><label>Starting Date</label><br> <md-datepicker
																				id="startDate" ng-model="reportStartDate"
																				md-placeholder="Enter date"
																				ng-change="dateChanged()" required></md-datepicker>
																		</td>
																		<td style="padding-right: 10px;"><label>Ending Date</label><br> <md-datepicker
																				id="endDate" ng-model="reportEndDate"
																				md-min-date="reportStartDate"
																				md-placeholder="Enter date" required></md-datepicker>
																		</td>
																		<td style="padding-right: 10px;"><label for="menuItemType">Store Name</label>
																			<select id="storeName" class="form-control"
																			ng-model="storeName" ng-change="getEmployeeName()"
																			ng-options="mit.id as mit.name for mit in storeItem">
																				<option value="" disabled selected hidden>Choose
																					a store</option>
																		</select></td>
																		<td style="padding-right: 10px;"><label for="menuItemType">Employee
																				Name</label> <select id="employeeName" class="form-control"
																			ng-model="employeeName"
																			ng-options="mit.id as mit.name for mit in employeeItem">
																				<option value="" disabled selected hidden>Choose
																					employee</option>
																		</select></td>
																		<td style="padding-top: 10px; padding-right: 10px">
																			<div class="form-group"><button id="" class="btn btn-block btn-primary"
																				type="button" ng-click="refreshTable()">
																				<i class="fas fa-search"></i> Search
																			</button></div>
																		</td>
																		<td style="padding-top: 10px;">
																			<div class="form-group"><button class="btn btn-success" type="submit">
																				<i class="fas fa-download"></i> Export
																			</button></div>
																		</td>
																	</tr>
																</tbody>
															</table>
														</div>
													</div> -->
													<br />
													<div class="card">
														<div class="row" style="margin: 8px;">
															<table id="menuItem_dtable3"
																class="table table-bordered display" style="width: 100%">
																<thead>
																	<tr>
																		<th>No</th>
																		<th>Store Name</th>
																		<th>Store Address</th>
																		<th>Staff Name</th>
																		<th>Sales (RM)</th>
																		<th>Date</th>
																	</tr>
																</thead>
															</table>
														</div>
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