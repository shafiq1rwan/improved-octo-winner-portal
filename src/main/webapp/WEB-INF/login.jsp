<%
	String resetExceptionMsg = (String) (request.getAttribute("resetExceptionMsg") == null ? "" : request.getAttribute("resetExceptionMsg"));
%>

<!DOCTYPE html>
<html>
<head>
<title>Managepay | BYOD ADMIN</title>

<!-- Bootstrap CSS -->
<link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<%-- <script src="${pageContext.request.contextPath}/assets/plugins/jQuery/jquery-2.2.3.min.js"></script> --%>


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
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<body style="background: url(${pageContext.request.contextPath}/assets/images/wallpaper/admin_login_background.jpg) no-repeat; background-size: cover; min-height: 100vh;">
 	<div class="container">
		<div class="row justify-content-center" style="padding-top: 100px;">	
			<div class="col-md-4 col-md-offset-4">
				<div class="card">
					<div class="card-heading">
						<div class="row-fluid user-row">
							<img src="${pageContext.request.contextPath}/assets/images/logo/mpay-crm.png"
								class="img-responsive" alt="Console Admin" />
						</div>
					</div>

					<div class="card-body">
						<label id="error" style="color:red; font-size: 16px;">${exceptionMsg}</label>
						<form action="${pageContext.request.contextPath}/user/signin" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
							<div class="form-group">
								<label class="login-label">User Name</label>
								<input class="form-control" name="username" placeholder="User Name" type="text" required> 
							</div>
							<div class="form-group">
								<label class="login-label">Password</label>
								<input class="form-control" name="userpassword" placeholder="Password" type="password" required>
							</div>
							<br>
							<div class="form-group">
								<input class="btn btn-lg btn-danger btn-block" type="submit" value="Login">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</div>
				
						</form>													
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script>

<% if (!(resetExceptionMsg.equals(null) || resetExceptionMsg.equals(""))) { %>
$('#error').focus();
<% } %>
</script>
</html>