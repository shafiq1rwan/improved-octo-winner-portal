<!DOCTYPE html>
<html>
<head>
<title>Access Denied</title>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/bootstrap/css/bootstrap.min.css">

<style>
.vertical-center {
  /* min-height: 100%;  */
  min-height: 100vh;
  display: flex;
  align-items: center;
}
</style>

</head>
<body>   
<!--  <div class='fullscreenDiv'> -->
<div style="background: url(${pageContext.request.contextPath}/assets/img/status/status_background.jpg) no-repeat;background-size: cover;">
    <div class="vertical-center">
        <div class="container-fluid center-block text-center">
    	<h1 class="alert-danger">You do not have permission to access this page!</h1>
    	<br><br>
    	<span><img src="${pageContext.request.contextPath}/assets/img/status/403_noentry.png" style="height:200px"></span>
    	<br><br>
    	<span>
    	<a href="${pageContext.request.contextPath}/user/signin" class="btn btn-info" role="button">HOME</a>
<%--     	     <form action="${pageContext.request.contextPath}/admin/logout" method="post">
				  <input type="submit" class="btn btn-default btn-flat" value="BACK" /> 
				  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form> --%> 
    	</span>
    	</div>
    </div>
</div> 
</body>
</html>