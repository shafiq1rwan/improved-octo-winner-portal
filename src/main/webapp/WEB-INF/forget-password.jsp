<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forget Password</title>

<!-- Bootstrap CSS -->
<link href="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/css/bootstrap.min.css" rel="stylesheet" type="text/css" />

<!-- ANGULAR JS IMPORT - Version 1.5 above -->
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-route.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/jquery-3.3.1/js/jquery-3.3.1.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/js/bootstrap.bundle.min.js"></script>

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
<body  style="background: url(${pageContext.request.contextPath}/assets/images/byodadmin/wallpaper/admin_login_background.jpg) no-repeat; background-size: cover; min-height: 100vh;" ng-app="forget_password_app" ng-controller="ctl_forget_password">

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
							<div ng-show="errorMessage">
								<label id="error" style="color:red; font-size: 16px;">{{errorMessage}}</label>
							</div>
							<div ng-show="successMessage">
								<label id="success" style="color:green; font-size: 16px;">{{successMessage}}</label>
							</div>
				
						<form name="form" role="form" class="form-signin" ng-submit="submitData()">
							<div class="form-group">
								<label class="login-label">Email</label>
								<input class="form-control" name="email" placeholder="User Email" type="email" ng-model="forget_password.email" required>
								<span style="color:red; font-size: 16px;" ng-show="form.email.$dirty 
									&& form.email.$error.required">Email is required</span>
							</div>
							<br>
							<div class="form-group">
								<input class="btn btn-lg btn-primary btn-block" type="submit" value="Forget Password" ng-disabled="form.$invalid">
							</div>
				
						</form>
						<a href="${pageContext.request.contextPath}/user/signin">Login</a>										
					</div>
				</div>
			</div>
		</div>
	</div>
	
			<!-- Loading Modal [START] -->
			<div class="modal fade" data-backdrop="static" id="loading_modal" role="dialog">
				<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-body">
						<div class="text-center">
							<img style="width:75%" src="${pageContext.request.contextPath}/img/gif/loading.gif"><br>
								<span>Synchronizing Data...</span>
						</div>
					</div>
				</div>
				</div>
			</div>
			<!-- Loading Modal [END] -->

</body>

<script>
var app = angular.module('forget_password_app', []);
app.controller('ctl_forget_password', function($scope, $http, $location) {
	$scope.forget_password = {};
	$scope.errorMessage = '';
	$scope.successMessage = '';
	
	$scope.submitData = function(){	
		$('#loading_modal').modal('show');
		
		if($scope.forget_password.email == null ||$scope.forget_password.email == ''){
		} else {		
			
			var jsonData = JSON.stringify({
				email : $scope.forget_password.email
			});
			
			console.log(jsonData);
			
			$http({
				method : 'POST',
				url : '${pageContext.request.contextPath}/forget-password/send-reset-email',
				data : jsonData
			})
			.then(
				function(response) {
					console.log("HH " + response.data.responseCode);
			 		if(response.data.responseCode === "00"){			
						$scope.successMessage = response.data.successMessage;		
						console.log(response.data.successMessage);
					} else if(response.data.responseCode === "01"){
						$scope.errorMessage = response.data.errorMessage;
						console.log(response.data.errorMessage);
					}
					$('#loading_modal').modal('hide');
				},
				function(response) {
					$('#loading_modal').modal('hide');
			});
		}
	}
	
	
	
	
});
</script>

</html>