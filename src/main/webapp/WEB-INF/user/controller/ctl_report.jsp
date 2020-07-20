<script>
	app.controller('ctl_report',function($scope, $http, $window, $routeParams, $location) {	
		$scope.reportStartDate;
		$scope.reportEndDate;
		$scope.reportType;
		$scope.stateItem = [];
		$scope.reportTypeItem = [];

		$scope.initiation = function(){
			$scope.reportStartDate = new Date();
			$scope.reportEndDate = new Date();
			$scope.refreshTable();
			$scope.getDropdownList();
	    }
		
		$scope.generateReport = function(){
	
			var date1 = moment($scope.reportStartDate).format();
			var date2 = moment($scope.reportEndDate).format();
			var reportType = $scope.reportType;
			
			window.open('${pageContext.request.contextPath}/report/transaction_report/'+date1+"/"+date2+"/"+reportType);
		}
		
		$scope.refreshTable = function(){	
			
			var date1 = moment($scope.reportStartDate).format();
			var date2 = moment($scope.reportEndDate).format();
			var reportType = $scope.reportType;
			
			$('#menuItem_dtable')
			.DataTable(
					{
						"ajax" : {
							"url" : '${pageContext.request.contextPath}/report/transaction_report_list/'+date1+"/"+date2+"/"+reportType,
							"dataSrc": function ( json ) {                
				                return json;
				            },  
							"statusCode" : {
								403 : function() {
									alert("Session TIME OUT");
									$(location)
											.attr('href',
													'${pageContext.request.contextPath}/admin');
								}
							}
						},
						"processing": true,
						"destroy" : true,
						"stateSave": true,
						"columns" : [ 
							{"data" : "no"}, 
							{"data" : "store_name"}, 
							{"data" : "store_address"}, 
							{"data" : "staff_name"}, 
							{"data" : "method_pay"}, 
							{"data" : "type_pay"}, 
							{"data" : "money"}, 
							{"data" : "trx_date"}
						],
						"scrollX" : true
					});
		}
		
		$scope.getDropdownList = function(){
			
			$http
			.get(
				'${pageContext.request.contextPath}/report/getState/')
			.then(
				function(response) {	
					$scope.stateItem = response.data;
				},
				function(response) {
					swal({
						  title: "Error",
						  text: response.data,
						  icon: "warning",
						  dangerMode: true,
					});
		});
			
			$http
			.get(
				'${pageContext.request.contextPath}/report/getReportType/')
			.then(
				function(response) {	
					$scope.reportTypeItem = response.data;
				},
				function(response) {
					swal({
						  title: "Error",
						  text: response.data,
						  icon: "warning",
						  dangerMode: true,
					});
		});
		}
	});
</script>