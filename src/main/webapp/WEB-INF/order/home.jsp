<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">

<title page-title>${applicationData.getString('appName')}</title>

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

<link rel="shortcut icon" type="image/png" href="${pageContext.request.contextPath}${applicationData.getString('shortcutLogoPath')}" />

<style>
.main-bg {
	background-image: url(${pageContext.request.contextPath}${applicationData.getString('mainBackgroundPath')}) !important;
    background-size: cover !important;
    background-repeat: no-repeat !important;
    background-position: center !important;
}
.main-color {
	background: ${applicationData.getString('mainColor')} !important;
}
.sub-color {
	background: ${applicationData.getString('subColor')} !important;
}
.main-text-color {
	color: ${applicationData.getString('mainTextColor')} !important;
}
.sub-text-color {
	color: ${applicationData.getString('mainTextColor')} !important;
}
.plus-color {
	color: #28A13C;
}
.minus-color {
	color: #B32719;
}
button.btn-locale {
	color: ${applicationData.getString('localeButtonColor')} !important;
	border-color: ${applicationData.getString('localeButtonColor')};
	background: none !important;
}
button.btn-main {
	color: ${applicationData.getString('mainButtonTextColor')} !important;
	border-color: ${applicationData.getString('mainButtonBackgroundColor')};
	background: ${applicationData.getString('mainButtonBackgroundColor')};
}
button.btn-main:hover {
	box-shadow: 0 0 5px ${applicationData.getString('mainButtonBackgroundHoverColor')} !important;
	border-color: ${applicationData.getString('mainButtonBackgroundHoverColor')} !important;
	background: ${applicationData.getString('mainButtonBackgroundHoverColor')} !important;
}
button.btn-main:focus, button.btn-main:active {
	box-shadow: 0 0 10px ${applicationData.getString('mainButtonBackgroundFocusColor')} !important;
	border-color: ${applicationData.getString('mainButtonBackgroundFocusColor')} !important;
	background: ${applicationData.getString('mainButtonBackgroundFocusColor')} !important;
}
.dropdown-selector {
	position: relative;
	border: 1px solid ${applicationData.getString('mainTextColor')};
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
<body ng-app="byodApp" oncontextmenu="return false;" style="display: none;">
	<div class="view-body w-100 h-100" ng-view></div>
</body>

<!-- Angular Code -->
<script>
var byodApp = angular.module('byodApp', ['ngRoute']);

//Configure Route Here
byodApp.config(function($routeProvider) {
	$routeProvider
	.when('', {
		templateUrl : '${pageContext.request.contextPath}/order/views/singleOrderPage/${brandId}',
    	controller : "OrderController"
	})
	.when('/', {
		templateUrl : '${pageContext.request.contextPath}/order/views/singleOrderPage/${brandId}',
    	controller : "OrderController"
	})
	.otherwise({
		templateUrl : '${pageContext.request.contextPath}/order/views/error/${brandId}'
	});
});
</script>

<!-- Controllers -->
<jsp:include page="/WEB-INF/order/controllers/orderController.jsp" />
</html>
