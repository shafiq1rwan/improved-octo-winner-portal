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
								<ol class="breadcrumb float-right">
									<li class="breadcrumb-item active">Settings</li>
								</ol>
								<div class="clearfix"></div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="card mb-3 shadow" style="margin: 0 auto; float: none;">
							<div class="card-header d-flex flex-row justify-content-between">
								<form id="userForm" method="POST" accept-charset="UTF-8"
									role="form" class="form-signin">
									<div class="modal-header">
										<h5>Change Password</h5>
									</div>
									<div class="modal-body">
										<div class="form-section">

											<div class="row">
												<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
													<div class="form-group">
														<label class="login-label">Current Password</label> <input
															class="form-control" name="currentPassword" ng-model="currentPassword"
															placeholder="******" type="password" required>
													</div>
												</div>
											</div>

											<div class="row">
												<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
													<div class="form-group">
														<label class="login-label">New Password</label> <input
															class="form-control" name="newPassword" ng-model="newPassword" placeholder="******"
															type="password" required>
													</div>
												</div>
												<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
													<div class="form-group">
														<label class="login-label">Confirm New Password</label> <input
															class="form-control" name="confirmPassword" ng-model="confirmPassword"
															placeholder="******" type="password" nx-equal="newPassword" required>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="modal-footer">
										<button class="btn btn-primary" type="submit"
											ng-click="submitChangePassword()">Save Changes</button>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- USER MODAL ENDED -->
</body>
</html>