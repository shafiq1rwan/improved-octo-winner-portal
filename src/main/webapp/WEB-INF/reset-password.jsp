<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reset Password</title>

<!-- Bootstrap CSS -->
<link href="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/css/bootstrap.min.css" rel="stylesheet" type="text/css" />

<!-- ANGULAR JS IMPORT - Version 1.5 above -->
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-route.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/jquery-3.3.1/js/jquery-3.3.1.min.js"></script>

<style>
.form-signin input[type="text"] {
	margin-bottom: 5px;
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
}

.form-signin input[type="password"] {
	margin-bottom: 10px;
	border-top-left-radius: 0;
	border-top-right-radius: 0;
}

.form-signin .form-control {
	position: relative;
	font-size: 16px;
	font-family: 'Open Sans', Arial, Helvetica, sans-serif;
	height: auto;
	padding: 10px;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
}

.img-responsive {
	display: block;
	max-width: 100%;
	height: auto;
	margin: auto;
}

.card {
	margin-bottom: 20px;
	background-color: #080808a0;
	border-radius: 4px;
}

.login-label {
	font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
    font-size: 14px;
    line-height: 1.42857143;
    color: #ffffff;
}

.underline:hover {
	text-decoration: underline;
}
</style>
</head>
<body  style="background: url(${pageContext.request.contextPath}/assets/images/byodadmin/wallpaper/admin_login_background.jpg) no-repeat; background-size: cover; min-height: 100vh;" ng-app="reset_password_app" ng-controller="ctl_reset_password">

 	<div class="container">
		<div class="row justify-content-center" style="padding-top:100px;">	
			<div class="col-md-4 col-md-offset-4">
				<div class="card">
					<div class="card-heading">
						<div class="row-fluid user-row">
							<img src="${pageContext.request.contextPath}/assets/images/byodadmin/logo/mpay.png"
								class="img-responsive" alt="Console Admin" />
						</div>
					</div>

					<div class="card-body">
						<label id="error" style="color:red; font-size: 16px;">${error}</label>
						<input id="token" value="${token}" hidden>
						<form name="form" role="form" class="form-signin" ng-submit="submitData()">
							<div class="form-group">
								<label class="login-label">Password</label>
								<input class="form-control" name="password" placeholder="Password" type="password" ng-model="reset_password.password" required>
								<span style="color:red; font-size: 16px;" ng-show="form.password.$dirty 
									&& form.password.$error.required">Password is required</span>
							</div>	
							<div class="form-group">
								<label class="login-label">Confirm Password</label>
								<input class="form-control" name="confirm_password" placeholder="Confirm Password" type="password" ng-model="reset_password.confirm_password" required>
								<span style="color:red; font-size: 16px;" ng-show="form.confirm_password.$dirty 
									&& form.confirm_password.$error.required">Confirm Password is required</span>
								<span style="color:red; font-size: 16px;" ng-show="reset_password.password != reset_password.confirm_password">Password not match</span>
							</div>
							
							<br>
							<div class="form-group">
								<input class="btn btn-lg btn-primary btn-block" type="submit" value="Reset Password" ng-disabled="form.$invalid || reset_password.password != reset_password.confirm_password">
							</div>
				
						</form>								
					</div>
				</div>
			</div>
		</div>
	</div>

</body>

<script>
var app = angular.module('reset_password_app', []);
app.controller('ctl_reset_password', function($scope, $http, $location) {
	$scope.reset_password = {};
	$scope.submitData = function(){
		var token = $('#token').val();
		if($scope.reset_password.password == null || $scope.reset_password.password == '' ||
			$scope.reset_password.confirm_password == null || $scope.reset_password.confirm_password == ''
		){
			
		} else {
			var postData = JSON.stringify({
				'password': $scope.reset_password.password,
				'confirm-password' : $scope.reset_password.confirm_password,
				'token' : token
			});
			
			$http({
				method : 'POST',
				url : '${pageContext.request.contextPath}/reset-password/reset',
				data : postData
			})
			.then(
				function(response) {
					if(response.data.responseCode === "00"){
						console.log("success send");
						$(location)
						.attr('href',
							'${pageContext.request.contextPath}/'+ response.data.responseRedirect);	
					} else if(response.data.responseCode === "01"){
						$(location)
						.attr('href',
							'${pageContext.request.contextPath}/'+ response.data.responseRedirect);
					}
				},
				function(response) {
					console.log("error");
					$(location)
					.attr('href',
						'${pageContext.request.contextPath}/user/signin');			
			});
		}
	}

});
</script>





</html>