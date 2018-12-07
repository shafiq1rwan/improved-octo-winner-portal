<script>
var locationRegex = /^\/store\/\d+\/tn\/\d+$/g

byodApp.controller('OrderController', function($scope, $http, $location, $routeParams, $filter) {
	/*Config Data*/
	$scope.localeData
	$scope.languageData;
	/*Variables*/
	$scope.currentLocale;
	$scope.currentLanguageData;
	$scope.storeName = "Temporary Name";
	$scope.tableId = "Temporary Table ID";
	
	$scope.changeLocale = function(localeName) {
		$scope.currentLocale = localeName;
		$scope.currentLanguageData = $scope.languageData[$scope.currentLocale];
	}
	
	$http({
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		data: {
		},
		url: '${pageContext.request.contextPath}/order/getLanguagePack'
	}).then(function (response) {
		if (response != null && response.data != null && response.data.localeData != null && response.data.languageData != null) {
			$scope.localeData = response.data.localeData;
			$scope.languageData = response.data.languageData;
			$scope.currentLocale = $scope.localeData[0].shortName;
			$scope.currentLanguageData = $scope.languageData[$scope.currentLocale];
			console.log(response);
		} else {
			console.log("Something is wrong.")
		}
	}, function (error) {
		console.log(error);
    });
});
</script>