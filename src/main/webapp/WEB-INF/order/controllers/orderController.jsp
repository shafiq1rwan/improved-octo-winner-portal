<script>
var locationRegex = /^\/store\/\d+\/tn\/\d+$/g

byodApp.controller('OrderController', function($scope, $http, $timeout) {
	/*Config Data*/
	$scope.localeData
	$scope.languageData;
	/*Variables*/
	$scope.isLoadingFailed;
	$scope.loadingPercentage;
	$scope.loadingText;
	$scope.currentLocale;
	$scope.currentLanguageData;
	$scope.storeName = "Temporary Name";
	$scope.tableID = "Temporary Table ID";
	
	$scope.changeLocale = function(e, localeName) {
		$("a.language-dropdown-item").removeClass("lang-selected");
		$(e.target).addClass("lang-selected");
		$scope.currentLocale = localeName;
		$scope.currentLanguageData = $scope.languageData[$scope.currentLocale];
	}
	
	/*Language Loading*/
	$scope.loadLanguageData = function() {
		$scope.loadingPercentage = 50;
		$scope.loadingText = "Loading Language Pack...";
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
				
				$scope.loadSuccess();
			} else {
				$scope.loadFailed();
			}
		}, function (error) {
			$scope.loadFailed();
	    });
	}
	
	/*Loading Function*/
	$scope.beginLoading = function() {
		$("div#loading-overlay").slideDown();
		$scope.isLoadingFailed = false;
		$scope.loadingPercentage = 0;
		$scope.loadingText = "Loading...";
		$scope.loadLanguageData();
	}
	$scope.loadSuccess = function() {
		$scope.loadingPercentage = 100;
		$scope.loadingText = "Loading Completed.";
		$timeout(function() {$("div#loading-overlay").slideUp()}, 1000);
	}
	$scope.loadFailed = function() {
		$scope.isLoadingFailed = true;
		$scope.loadingPercentage = 0;
		$scope.loadingText = "Loading Failed.";
	}
	
	$scope.beginLoading();
});
</script>