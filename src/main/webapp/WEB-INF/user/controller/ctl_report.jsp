<script>
	app.controller('ctl_report',function($scope, $http, $window, $routeParams, $location) {	
		$scope.reportStartDate;
		$scope.reportEndDate;

		$scope.initiation = function(){
			$scope.reportStartDate = new Date();
			$scope.reportEndDate = new Date();
	    }
		
		$scope.generateReport = function(){
	
		 	if($scope.reportStartDate == null || $scope.reportStartDate == '' || $scope.reportEndDate == null || $scope.reportEndDate == ''){
			} else {
		   		var jsonData = JSON.stringify({
					"startDate" : moment($scope.reportStartDate).format(),
					"endDate" : moment($scope.reportEndDate).format(),
					"reportType" : 1
				});
		   		
				console.log("JSONDATA: " + jsonData);
		   		
		   	 	$http.post("${pageContext.request.contextPath}/report/transaction_report", jsonData)
				.then(function(response) {
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/user";
				});
			}
		}
		
	});
</script>