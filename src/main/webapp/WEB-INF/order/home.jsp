<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">

<title page-title>${applicationData.getAppName()}</title>

<!-- JS File -->
<script src="${pageContext.request.contextPath}/assets/plugins/jquery-3.3.1/js/jquery-3.3.1.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-route.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-animate.min.js"></script>

<!-- CSS File -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/css/bootstrap-grid.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/css/bootstrap-reboot.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/order/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-pro-5.6.1/css/all.css">

<link rel="shortcut icon" type="image/png" href="${pageContext.request.contextPath}${applicationData.getShortcutLogoPath()}" />

<style>
.main-bg {
	background-image: url(${pageContext.request.contextPath}${applicationData.getMainBackgroundPath()}) !important;
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
}
.main-color {
	background: ${applicationData.getMainColor()} !important;
}
.sub-color {
	background: ${applicationData.getSubColor()} !important;
}
.main-text-color {
	color: ${applicationData.getMainTextColor()} !important;
}
.sub-text-color {
	color: ${applicationData.getMainTextColor()} !important;
}
.plus-color {
	color: #28A13C;
}
.minus-color {
	color: #B32719;
}
button.btn-locale {
	color: ${applicationData.getLocaleButtonColor()} !important;
	border-color: ${applicationData.getLocaleButtonColor()};
	background: none !important;
}
button.btn-main {
	color: ${applicationData.getMainButtonTextColor()} !important;
	border-color: ${applicationData.getMainButtonBackgroundColor()};
	background: ${applicationData.getMainButtonBackgroundColor()};
}
button.btn-main:hover {
	box-shadow: 0 0 5px ${applicationData.getMainButtonBackgroundHoverColor()} !important;
	border-color: ${applicationData.getMainButtonBackgroundHoverColor()} !important;
	background: ${applicationData.getMainButtonBackgroundHoverColor()} !important;
}
button.btn-main:focus, button.btn-main:active {
	box-shadow: 0 0 10px ${applicationData.getMainButtonBackgroundFocusColor()} !important;
	border-color: ${applicationData.getMainButtonBackgroundFocusColor()} !important;
	background: ${applicationData.getMainButtonBackgroundFocusColor()} !important;
}
.dropdown-selector {
	position: relative;
	border: 1px solid ${applicationData.getMainTextColor()};
	border-radius: 5px;
    vertical-align: middle;
}
.dropdown-selector i.fa-caret-down {
	position: absolute;
    right: 0;
    top: 0.05em;
}
</style>

</head>
<body ng-app="byodApp" class="main-bg" oncontextmenu="return false;" style="display: none;">
	<div class="view-body w-100 h-100" ng-view></div>
</body>

<!-- Angular Code -->
<script>
var byodApp = angular.module('byodApp', ['ngRoute']);

//Configure Route Here
byodApp.config(function($routeProvider) {
	$routeProvider
	.when('', {
		templateUrl : '${pageContext.request.contextPath}/order/views/singleOrderPage',
    	controller : "OrderController"
	})
	.when('/', {
		templateUrl : '${pageContext.request.contextPath}/order/views/singleOrderPage',
    	controller : "OrderController"
	})
	.otherwise({
		templateUrl : '${pageContext.request.contextPath}/order/views/error'
	});
});
</script>

<!-- Controllers -->
<jsp:include page="/WEB-INF/order/controllers/orderController.jsp" />
</html>
