<script>
	app.controller('ctl_reportBSI',function($scope, $http, $window, $routeParams, $location) {	
		$scope.reportStartDate;
		$scope.reportEndDate;
		$scope.stateItem = [];
		$scope.reportTypeItem = [];
		$scope.storeItem = [];

		$scope.initiation = function(){
			$scope.reportStartDate = new Date();
			$scope.reportEndDate = new Date();
			$scope.refreshTable();
			$scope.getDropdownList();
	    }
		
		$scope.generateReport = function(){
	
			var date1 = moment($scope.reportStartDate).format();
			var date2 = moment($scope.reportEndDate).format();
			var reportType = 2;
			var store = $scope.storeName;
			var employee = "undefined";
			var paymentType = "undefined";
			
			window.open('${pageContext.request.contextPath}/report/transaction_report/'+date1+"/"+date2+"/"+reportType+"/"+store+"/"+employee+"/"+paymentType);
		}
		
		$scope.refreshTable = function(){	
			
			var date1 = moment($scope.reportStartDate).format();
			var date2 = moment($scope.reportEndDate).format();
			var store = $scope.storeName;
			var employee = "undefined";
			var paymentType = "undefined";
			var category = $scope.categoryName;
			
			$('#menuItem_dtable2')
			.DataTable(
					{
						"ajax" : {
							"url" : '${pageContext.request.contextPath}/report/getBestSellingItem/'+date1+"/"+date2+"/"+store+"/"+category,
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
						"searching" : true,
						"columns" : [ 
							/* {"data" : "no"},  */
							{"data" : "category"},
							{"data" : "item_name"}, 
							{"data" : "total_item"}, 
							{"data" : "item_price"}, 
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
			
			$http
			.get(
				'${pageContext.request.contextPath}/report/getCategoryList/')
			.then(
				function(response) {	
					$scope.categoryItem = response.data;
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
		
		
	}).config(function($mdDateLocaleProvider) {
		  $mdDateLocaleProvider.formatDate = function(date) {
			    return date ? moment(date).format('DD-MM-YYYY') : '';
			  };
			  
			  $mdDateLocaleProvider.parseDate = function(dateString) {
			    var m = moment(dateString, 'DD-MM-YYYY', true);
			    return m.isValid() ? m.toDate() : new Date(NaN);
			  };
	});
</script>