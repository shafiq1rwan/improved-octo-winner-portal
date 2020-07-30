<script>
	app.controller('ctl_reportSBPT',function($scope, $http, $window, $routeParams, $location) {	
		$scope.reportStartDate;
		$scope.reportEndDate;
		/* $scope.reportType; */
		$scope.stateItem = [];
		/* $scope.reportTypeItem = []; */
		$scope.storeItem = [];
		$scope.paymentItem = [];

		$scope.initiation = function(){
			$scope.reportStartDate = new Date();
			$scope.reportEndDate = new Date();
			$scope.refreshTable();
			$scope.getDropdownList();
	    }
		
		$scope.generateReport = function(){
	
			var date1 = moment($scope.reportStartDate).format();
			var date2 = moment($scope.reportEndDate).format();
			var reportType = 4;
			var store = $scope.storeName;
			var employee = "undefined";
			var paymentType = $scope.paymentType;
			
			window.open('${pageContext.request.contextPath}/report/transaction_report/'+date1+"/"+date2+"/"+reportType+"/"+store+"/"+employee+"/"+paymentType);
		}
		
		$scope.refreshTable = function(){	
			
			var date1 = moment($scope.reportStartDate).format();
			var date2 = moment($scope.reportEndDate).format();
			var reportType = 4;
			var store = $scope.storeName;
			var employee = "undefined";
			var paymentType = $scope.paymentType;
			
			$('#menuItem_dtable4')
			.DataTable(
					{
						"ajax" : {
							"url" : '${pageContext.request.contextPath}/report/transaction_report_list/'+date1+"/"+date2+"/"+reportType+"/"+store+"/"+employee+"/"+paymentType,
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
						"columns" : [ 
							{"data" : "no"}, 
							{"data" : "store_name"}, 
							{"data" : "store_address"}, 
							/* {"data" : "staff_name"},  */
							{"data" : "method_pay"}, 
							{"data" : "type_pay"}, 
							{"data" : "money"}, 
							{"data" : "trx_date"}
						],
						"scrollX" : true
					});
		}
		
		$scope.getDropdownList = function(){
			
			/* $http
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
			}); */
			
			$http
			.get(
				'${pageContext.request.contextPath}/report/getPaymentMethod/')
			.then(
				function(response) {	
					$scope.paymentItem = response.data;
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
				'${pageContext.request.contextPath}/report/getStoreList/')
			.then(
				function(response) {	
					$scope.storeItem = response.data;
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