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
span.order-icon {
	display: inline-block;
    width: 1.5em;
    height: 1.5em;
    vertical-align: middle;
    content: "";
    background: no-repeat center center;
    background-size: 100% 100%;
	background-image: url("data:image/svg+xml;charset=utf8,%3Csvg viewBox='0 0 512 512' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath stroke='${applicationData.getMainTextColor()}' fill='${applicationData.getMainTextColor()}' stroke-width='2' stroke-linecap='round' d='M458.737,422.218l-22.865-288.116c-1.425-18.562-17.123-33.103-35.739-33.103H354.97v-2.03 C354.97,44.397,310.573,0,256.001,0s-98.969,44.397-98.969,98.969v2.03H111.87c-18.617,0-34.316,14.54-35.736,33.064 L53.262,422.257c-1.77,23.075,6.235,46.048,21.961,63.026C90.949,502.261,113.242,512,136.385,512h239.231 c23.142,0,45.436-9.738,61.163-26.717C452.505,468.304,460.509,445.332,458.737,422.218z M187.022,98.969	c0-38.035,30.945-68.979,68.979-68.979s68.979,30.945,68.979,68.979v2.03H187.022V98.969z M414.776,464.905	c-10.218,11.03-24.124,17.105-39.16,17.105h-239.23c-15.036,0-28.942-6.075-39.16-17.105 c-10.217-11.031-15.211-25.363-14.063-40.315l22.87-288.195c0.232-3.032,2.796-5.406,5.837-5.406h45.162v36.935 c0,8.281,6.714,14.995,14.995,14.995c8.281,0,14.995-6.714,14.995-14.995v-36.935H324.98v36.935 c0,8.281,6.714,14.995,14.995,14.995s14.995-6.714,14.995-14.995v-36.935h45.163c3.04,0,5.604,2.375,5.84,5.446l22.865,288.115 C429.988,439.542,424.993,453.873,414.776,464.905z M323.556,254.285c-5.854-5.856-15.349-5.856-21.204,0l-66.956,66.956l-25.746-25.746c-5.855-5.856-15.35-5.856-21.206,0	c-5.856,5.856-5.856,15.35,0,21.206l36.349,36.349c2.928,2.928,6.766,4.393,10.602,4.393s7.675-1.464,10.602-4.393l77.558-77.558 C329.412,269.635,329.412,260.141,323.556,254.285z'/%3E%3C/svg%3E") !important;
}
span.back-icon {
	display: inline-block;
    width: 1.5em;
    height: 1.5em;
    vertical-align: middle;
    content: "";
    background: no-repeat center center;
    background-size: 100% 100%;
	background-image: url("data:image/svg+xml;charset=utf8,%3Csvg viewBox='0 0 199.404 199.404' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath stroke='${applicationData.getMainTextColor()}' fill='${applicationData.getMainTextColor()}' stroke-width='2' stroke-linecap='round' d='M135.412,0 35.709,99.702 135.412,199.404 163.695,171.119 92.277,99.702 163.695,28.285z'/%3E%3C/svg%3E") !important;
}
span.cart-icon {
	display: inline-block;
    width: 2.0em;
    height: 2.0em;
    vertical-align: middle;
    content: "";
    background: no-repeat center center;
    background-size: 100% 100%;
	background-image: url("data:image/svg+xml;charset=utf8,%3Csvg viewBox='0 0 446.843 446.843' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath stroke='${applicationData.getMainTextColor()}' fill='${applicationData.getMainTextColor()}' stroke-width='2' stroke-linecap='round' d='M444.09,93.103c-2.698-3.699-7.006-5.888-11.584-5.888H109.92c-0.625,0-1.249,0.038-1.85,0.119l-13.276-38.27 c-1.376-3.958-4.406-7.113-8.3-8.646L19.586,14.134c-7.374-2.887-15.695,0.735-18.591,8.1c-2.891,7.369,0.73,15.695,8.1,18.591 l60.768,23.872l74.381,214.399c-3.283,1.144-6.065,3.663-7.332,7.187l-21.506,59.739c-1.318,3.663-0.775,7.733,1.468,10.916 c2.24,3.183,5.883,5.078,9.773,5.078h11.044c-6.844,7.616-11.044,17.646-11.044,28.675c0,23.718,19.298,43.012,43.012,43.012 s43.012-19.294,43.012-43.012c0-11.029-4.2-21.059-11.044-28.675h93.776c-6.847,7.616-11.048,17.646-11.048,28.675 c0,23.718,19.294,43.012,43.013,43.012c23.718,0,43.012-19.294,43.012-43.012c0-11.029-4.2-21.059-11.043-28.675h13.433 c6.599,0,11.947-5.349,11.947-11.948c0-6.599-5.349-11.947-11.947-11.947H143.647l13.319-36.996 c1.72,0.724,3.578,1.152,5.523,1.152h210.278c6.234,0,11.751-4.027,13.65-9.959l59.739-186.387 C447.557,101.567,446.788,96.802,444.09,93.103z M169.659,409.807c-10.543,0-19.116-8.573-19.116-19.116 s8.573-19.117,19.116-19.117s19.116,8.574,19.116,19.117S180.202,409.807,169.659,409.807z M327.367,409.807 c-10.543,0-19.117-8.573-19.117-19.116s8.574-19.117,19.117-19.117c10.542,0,19.116,8.574,19.116,19.117 S337.909,409.807,327.367,409.807z M402.52,148.149h-73.161V115.89h83.499L402.52,148.149z M381.453,213.861h-52.094v-37.038 h63.967L381.453,213.861z M234.571,213.861v-37.038h66.113v37.038H234.571z M300.684,242.538v31.064h-66.113v-31.064H300.684z M139.115,176.823h66.784v37.038h-53.933L139.115,176.823z M234.571,148.149V115.89h66.113v32.259H234.571z M205.898,115.89v32.259 h-76.734l-11.191-32.259H205.898z M161.916,242.538h43.982v31.064h-33.206L161.916,242.538z M329.359,273.603v-31.064h42.909 l-9.955,31.064H329.359z'/%3E%3C/svg%3E") !important;
}
.dropdown-selector {
	border: 1px solid ${applicationData.getMainTextColor()};
	border-radius: 5px;
	padding-right: 50px;
    vertical-align: middle;
    background: no-repeat;
    background-position: right 10px top;
	background-image: url("data:image/svg+xml;charset=utf8,%3Csvg viewBox='0 0 292.362 292.362' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath stroke='${applicationData.getMainTextColor()}' fill='${applicationData.getMainTextColor()}' stroke-width='2' stroke-linecap='round' d='M286.935,69.377c-3.614-3.617-7.898-5.424-12.848-5.424H18.274c-4.952,0-9.233,1.807-12.85,5.424,C1.807,72.998,0,77.279,0,82.228c0,4.948,1.807,9.229,5.424,12.847l127.907,127.907c3.621,3.617,7.902,5.428,12.85,5.428,s9.233-1.811,12.847-5.428L286.935,95.074c3.613-3.617,5.427-7.898,5.427-12.847C292.362,77.279,290.548,72.998,286.935,69.377z'/%3E%3C/svg%3E") !important;
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
		templateUrl : '${pageContext.request.contextPath}/order/views/pleaseScanQR'
	})
	.when('/', {
    	templateUrl : '${pageContext.request.contextPath}/order/views/pleaseScanQR'
	})
	.when('/store/:storeId/tn/:tableId', {
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
