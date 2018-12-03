<!DOCTYPE html>
<html ng-app="myApp">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<meta name="viewport" content="width=device-width, initial-scale=0.65">
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

<!-- Page title set in pageTitle directive -->
<title page-title>ManagePay CRM CORPORATE DISTRIBUTOR PORTAL</title>

<!-- ****BASE FOLDER DEFINE**** -->
<base href="/assets/">

<link rel="shortcut icon" type="image/ico" href="${pageContext.request.contextPath}/assets/favicon.png" />

<style>
/* Paste this css to your style sheet file or under head tag */
/* This only works with JavaScript, 
if it's not present, don't show loader */
.no-js #loader {
	display: none;
}

.js #loader {
	display: block;
	position: absolute;
	left: 100px;
	top: 0;
}

.se-pre-con {
	position: fixed;
	left: 0px;
	top: 0px;
	width: 100%;
	height: 100%;
	z-index: 9999;
	background: url(images/loader-64x/Preloader_2.gif) center no-repeat #fff;
}
</style>
	<!-- Favicon -->
	<link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">

	<!-- Bootstrap CSS -->
	<link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	
	<!-- Font Awesome CSS -->
	<link href="${pageContext.request.contextPath}/assets/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
	
	<!-- Custom CSS -->
	<link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet" type="text/css" />
	
	<!-- BEGIN CSS for this page -->
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.16/css/dataTables.bootstrap4.min.css"/>
	<!-- END CSS for this page -->
	
	<link href="${pageContext.request.contextPath}/assets/plugins/jquery.filer/css/jquery.filer.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/assets/plugins/jquery.filer/css/themes/jquery.filer-dragdropbox-theme.css" rel="stylesheet" />		
	
<!-- ANGULAR JS IMPORT - Version 1.5 above -->
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.7/angular.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.7/angular-route.js"></script>

<style>
.main {
	margin-top: 50px;
}
</style>

<%-- <link rel="import" href="${pageContext.request.contextPath}/assets/component.html"> --%>
</head>

<!-- Body -->
<!-- appCtrl controller with serveral data used in theme on diferent view -->
<body ng-app="myApp" class="adminbody">
	<div id="main">
	<div class="wrapper">
		<!-- *****************************TO-DO : INCLUDE main_header***************************** -->
		<jsp:include page="/WEB-INF/user/webparts_include/main_header.jsp" />
		<!-- *****************************TO-DO : INCLUDE main_header***************************** -->

		<!-- *****************************TO-DO : INCLUDE menu_drawer***************************** -->
		<jsp:include page="/WEB-INF/user/webparts_include/menu_drawer.jsp" />
		<!-- *****************************TO-DO : INCLUDE menu_drawer***************************** -->

		<div ng-view></div>
	</div>
	</div>
</body>

<script>
	var app = angular.module("myApp", [ "ngRoute" ]);

	app.config(function($routeProvider) {
		$routeProvider
		/* .when("/", {
			templateUrl : "${pageContext.request.contextPath}/admin/views/profile",
			controller : "ctl_profile"
		}) */
		.when("/Router_store", {
			templateUrl : "${pageContext.request.contextPath}/user/views/store",
			controller : "ctl_store",
			resolve : {
		       	checkSession:checkSession
		       }
		})
		.when("/Router_profile", {
			templateUrl : "${pageContext.request.contextPath}/user/views/profile",
			controller : "ctl_profile",
			resolve : {
		       	checkSession:checkSession
		       }
		})
		.when("/Router_menu", {
			templateUrl : "${pageContext.request.contextPath}/user/views/menu",
			controller : "ctl_menu",
			resolve : {
		       	checkSession:checkSession
		       }
		})
		.when("/Router_menu_dashboard", {
			templateUrl : "${pageContext.request.contextPath}/user/views/menuDashboard",
			controller : "ctl_menu_dashboard",
			resolve : {
		       	checkSession:checkSession
		       }
		})
		.when("/Router_add_menu", {
			templateUrl : "${pageContext.request.contextPath}/user/views/addMenu",
			controller : "ctl_add_menu",
			resolve : {
		       	checkSession:checkSession
		       }
		})
	});
	
	var checkSession = function ($location,$q) {
		$.ajax({
			type : "GET",
			url : "${pageContext.request.contextPath}/user/checksession/",
			contentType : "application/json",
			success : function(resultData) {
				var deferred = $q.defer();
			    if (resultData == "exist") {
			        deferred.resolve();
			    } else {
			        deferred.reject();
			        window.location.href = "${pageContext.request.contextPath}/user/signin/error/" + "timeout";
			    }
			    return deferred.promise;
			}
		})
	};
</script>

<script src="${pageContext.request.contextPath}/assets/js/modernizr.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/moment.min.js"></script>
			
	<script src="${pageContext.request.contextPath}/assets/js/popper.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/assets/js/detect.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/fastclick.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.blockUI.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.nicescroll.js"></script>
	
	<!-- App js -->
	<script src="${pageContext.request.contextPath}/assets/js/pikeadmin.js"></script>

	<!-- BEGIN Java Script for this page -->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.16/js/dataTables.bootstrap4.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/jquery.filer/js/jquery.filer.min.js"></script>

	<!-- Counter-Up-->
	<script src="${pageContext.request.contextPath}/assets/plugins/waypoints/lib/jquery.waypoints.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/counterup/jquery.counterup.min.js"></script>	

<!-- *****************************ANGULAR JS CONTROLLER***************************** -->
<jsp:include page="/WEB-INF/user/controller/ctl_store.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_profile.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_menu.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_add_menu.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_menu_dashboard.jsp" />
<!-- *****************************ANGULAR JS CONTROLLER***************************** -->
</html>