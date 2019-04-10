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
											<div class="row" style="padding-right: 2px; padding-left: 2px;">
												<div class="col-md-12" style="padding-right: 2px; padding-left: 2px;">
														<form ng-submit="generateReport()">
															<div class="row">

											     			     <div class="col-sm-3 form-group">
											     			   		<label>Starting Date</label>
																	<md-datepicker id="startDate" ng-model="reportStartDate" md-placeholder="Enter date" required></md-datepicker>
											     			     </div>

											        			 <div class="col-sm-3 form-group">
											        			   	<label>Ending Date</label>
																	<md-datepicker id="endDate" ng-model="reportEndDate" md-min-date="reportStartDate" md-placeholder="Enter date" required></md-datepicker>
											        			 </div>

															</div>
							
															<br><br>
															<div class="row">
																<div class="col-sm-12">
																	<div style="position: absolute; padding-right: 15px; bottom: 0; right: 0;">
																		<button class="btn btn-block btn-info" type="submit">Generate Report</button>
																	</div>
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