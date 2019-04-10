<!DOCTYPE html>
<html ng-app="myApp">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<meta name="viewport" content="width=device-width, initial-scale=0.65">
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

<!-- Page title set in pageTitle directive -->
<title page-title>ManagePay BYOD Cloud</title>

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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-pro-5.6.1/css/all.css">
	
	<!-- Custom CSS -->
	<link href="${pageContext.request.contextPath}/assets/css/byodadmin/style.css" rel="stylesheet" type="text/css" />
	
	<!-- BEGIN CSS for this page -->
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/css/dataTables.bootstrap4.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/extensions/Select/css/select.dataTables.min.css"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/extensions/Buttons/css/buttons.dataTables.min.css"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/extensions/RowReorder/css/rowReorder.dataTables.min.css"/>	
	<!-- END CSS for this page -->

	<link href="${pageContext.request.contextPath}/assets/plugins/jQuery.filer-1.3.0/css/jquery.filer.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/assets/plugins/jQuery.filer-1.3.0/css/themes/jquery.filer-dragdropbox-theme.css" rel="stylesheet" />	
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/angular-material-1.1.12/angular-material.min.css">	
	
	<!-- ANGULAR JS IMPORT - Version 1.5 above -->
	<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-animate.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-route.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/angular-1.7.4/js/angular-aria.min.js"></script>

	<!-- Angular Material Library -->
 	<script src="${pageContext.request.contextPath}/assets/plugins/angular-material-1.1.12/angular-material.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/assets/plugins/jquery-3.3.1/js/jquery-3.3.1.min.js"></script>
	<%-- <script src="${pageContext.request.contextPath}/assets2/js/jquery.min.js"></script> --%>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/js/dataTables.bootstrap4.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/extensions/Select/js/dataTables.select.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/extensions/Buttons/js/dataTables.buttons.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/plugins/datatable-1.10.16/extensions/RowReorder/js/dataTables.rowReorder.min.js"></script>
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/datetimepicker/css/tempusdominus-bootstrap-4.min.css" />
	
	<link href="${pageContext.request.contextPath}/assets/plugins/select2-4.0.6-rc.1/css/select2.min.css" rel="stylesheet" />
	<script src="${pageContext.request.contextPath}/assets/plugins/select2-4.0.6-rc.1/js/select2.min.js"></script>
	
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

		<div class="h-100" ng-view></div>
	</div>
	</div>
</body>

<script>
	var app = angular.module("myApp", ["ngMaterial","ngRoute"]);

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
		       	checkSession:checkSession,
		    	checkPageAccess:checkPageAccess('store')
		       }
		})
		.when("/Router_group_category", {
			templateUrl : "${pageContext.request.contextPath}/user/views/groupCategory",
			controller : "ctl_group_category",
			resolve : {
		       	checkSession:checkSession,
		       	checkPageAccess:checkPageAccess('group-category')
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
		.when("/Router_dashboard", {
			templateUrl : "${pageContext.request.contextPath}/user/views/dashboard",
			controller : "ctl_dashboard",
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
		.when('/Router_item_group', {
			templateUrl : '${pageContext.request.contextPath}/user/views/itemGroup',	
			controller : "ctl_item_group",
			resolve : {
		       	checkSession:checkSession,
		       	checkPageAccess:checkPageAccess('menu-item')
		       }
		})
		.when('/Router_modifier_group', {
			templateUrl : '${pageContext.request.contextPath}/user/views/modifierGroup',	
			controller : "ctl_modifier_group",
			resolve : {
		       	checkSession:checkSession,
		       	checkPageAccess:checkPageAccess('menu-item')
		       }
		})
		.when('/Router_menu_item', {
			templateUrl : '${pageContext.request.contextPath}/user/views/menuItem',	
			controller : "ctl_menu_item",
			resolve : {
		       	checkSession:checkSession,
		      	checkPageAccess:checkPageAccess('menu-item')
		       }
		})
		.when('/Router_combo/:id', {
			templateUrl : function(params){
				return '${pageContext.request.contextPath}/user/views/combo/'+ params.id
			},
			controller : "ctl_combo",
			resolve : {
		       	checkSession:checkSession
		       }
		})
		.when('/Router_assign_modifier/:id',{
			templateUrl : function(params){
				return '${pageContext.request.contextPath}/user/views/assignModifier/'+ params.id
			},
			controller : "ctl_assign_modifier",
			resolve : {
		       	checkSession:checkSession
		    }
		})
		.when('/Router_report',{
			templateUrl : function(params){
				return '${pageContext.request.contextPath}/user/views/report'
			},
			controller : "ctl_report",
			resolve : {
		       	checkSession:checkSession
		    }
		})
		.when('/Router_setting',{
			templateUrl : function(params){
				return '${pageContext.request.contextPath}/user/views/setting'
			},
			controller : "ctl_setting",
			resolve : {
		       	checkSession:checkSession,
		       	checkPageAccess:checkPageAccess('setting')
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
	
	var checkPageAccess = function(type) {
		return function($location, $q){
			console.log("APIs Type " + type)
				$.ajax({
					type : "GET",
					url : "${pageContext.request.contextPath}/user/checkaccessrights/" + type,
					contentType : "application/json",
					success : function(resultData) {
						var deferred = $q.defer();
					    if (resultData == "authorized") {
					        deferred.resolve();
					    } else {
					        deferred.reject();
					        window.location.href = "${pageContext.request.contextPath}/user/views/unauthorized";
					    }
					    return deferred.promise;
					}
				})
		}
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
<script src="${pageContext.request.contextPath}/assets/plugins/jquery-sortable/js/jquery-sortable.js"></script>	
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/plugins/sortable/js/Sortable.min.js"></script>
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
<jsp:include page="/WEB-INF/user/controller/ctl_ecpos.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_byod.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_kiosk.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_category.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_modifier_group.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_item_group.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_menu_item.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_combo.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_assign_modifier.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_dashboard.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_report.jsp" />
<jsp:include page="/WEB-INF/user/controller/ctl_setting.jsp" />
<!-- *****************************ANGULAR JS CONTROLLER***************************** -->
</html>