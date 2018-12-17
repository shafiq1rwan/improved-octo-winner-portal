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

.main {
	margin-top: 50px;
}
</style>
	<!-- Favicon -->
	<link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">

	<!-- Bootstrap CSS -->
	<link href="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	
	<!-- Font Awesome CSS -->
	<link href="${pageContext.request.contextPath}/assets/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
	
	<!-- Custom CSS -->
	<link href="${pageContext.request.contextPath}/assets/css/byodadmin/style.css" rel="stylesheet" type="text/css" />
	
	<!-- BEGIN CSS for this page -->
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/css/dataTables.bootstrap4.min.css"/>
	<!-- END CSS for this page -->
	
	<link href="${pageContext.request.contextPath}/assets/plugins/jQuery.filer-1.3.0/css/jquery.filer.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/assets/plugins/jQuery.filer-1.3.0/css/themes/jquery.filer-dragdropbox-theme.css" rel="stylesheet" />		
	
	<!-- ANGULAR JS IMPORT - Version 1.5 above -->
	<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-route.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/assets/plugins/jquery-3.3.1/js/jquery-3.3.1.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/assets2/js/jquery.min.js"></script> --%>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/js/dataTables.bootstrap4.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/datetimepicker/css/tempusdominus-bootstrap-4.min.css" />
	
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
		.when("/Router_group_category", {
			templateUrl : "${pageContext.request.contextPath}/user/views/groupCategory",
			controller : "ctl_group_category",
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
		.when('/Router_store_ecpos/:id', {                            
	        templateUrl: function(params){
	        	return '${pageContext.request.contextPath}/user/views/store/'+params.id+'/ecpos'
	        }
	        ,
	        controller: 'ctl_ecpos',
	        resolve : {
		       	checkSession:checkSession
		       }
	    })
		.when('/Router_store_byod/:id', {                            
	        templateUrl: function(params){
	        	return '${pageContext.request.contextPath}/user/views/store/'+params.id+'/byod'
	        }
	        ,
	        controller: 'ctl_byod',
	        resolve : {
		       	checkSession:checkSession
		       }
	    })
	    .when('/Router_store_kiosk/:id', {                            
	        templateUrl: function(params){
	        	return '${pageContext.request.contextPath}/user/views/store/'+params.id+'/kiosk'
	        }
	        ,
	        controller: 'ctl_kiosk',
	        resolve : {
		       	checkSession:checkSession
		       }
	    })
		.when('/Router_group_category_category/:id', {
			templateUrl : function(params) {
				return '${pageContext.request.contextPath}/user/views/groupCategory/'+ params.id + '/category'
			},	
			controller : "ctl_category",
			resolve : {
		       	checkSession:checkSession
		       }
		})
		.when('/Router_modifier_group', {
			templateUrl : '${pageContext.request.contextPath}/user/views/modifierGroup',	
			controller : "ctl_modifier_group",
			resolve : {
		       	checkSession:checkSession
		       }
		});
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
	
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/popper.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/bootstrap-4.1.3/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/fastclick.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/jquery.nicescroll.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/modernizr.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/moment.min.js"></script>	
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/detect.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/jquery.nicescroll.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/parsleyjs/parsley.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/jQuery.filer-1.3.0/js/jquery.filer.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/pikeadmin/js/pikeadmin.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/sweetalert/js/sweetalert.min.js"></script>
<!-- Bootstrap DatetimePicker* -->
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/plugins/datetimepicker/js/tempusdominus-bootstrap-4.min.js"></script>
	
	<!-- App js -->

	<%--

	<!-- BEGIN Java Script for this page -->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>

	<!-- Counter-Up-->
	<script src="${pageContext.request.contextPath}/assets/plugins/waypoints/lib/jquery.waypoints.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/counterup/jquery.counterup.min.js"></script> --%>

<!-- *****************************ANGULAR JS CONTROLLER***************************** -->
<jsp:include page="/WEB-INF/user/controller/ctl_store.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_group_category.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_profile.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_menu.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_add_menu.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_menu_dashboard.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_ecpos.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_byod.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_kiosk.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_category.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_modifier_group.jsp" />
<!-- *****************************ANGULAR JS CONTROLLER***************************** -->
</html>