<script>
var locationRegex = /^\/store\/\d+\/tn\/\d+$/g

/*Custom JQuery*/
jQuery.fn.extend({
    fadeInFromLeft: function () {
        $(this).animate({
        	left: "0"
        }, 300);
    },
	fadeOutToLeft: function () {
		$(this).animate({
			left: "-100%"
        }, 300);
	},
	fadeInFromTop: function () {
        $(this).animate({
        	top: "0"
        }, 300);
    },
	fadeOutToTop: function () {
		$(this).animate({
			top: "-100%"
        }, 300);
	}
});

byodApp.controller('OrderController', function($scope, $http, $timeout) {
	/*Config Data*/
	$scope.localeData
	$scope.languageData;
	$scope.menuList;
	/*Variables*/
	$scope.isLoadingFailed;
	$scope.loadingPercentage;
	$scope.loadingText;
	$scope.currentLocale;
	$scope.currentLanguageData;
	$scope.storeName = "Store X";
	$scope.tableID = "ID Y";
	$scope.selectedCategory;
	$scope.selectedItem;
	$scope.isItemCompleted;
	$scope.totalItemPrice;
	
	$scope.changeLocale = function(data) {
		$scope.currentLocale = data;
		$scope.currentLanguageData = $scope.languageData[$scope.currentLocale.shortName];
	}
	
	/*SOP Switch*/
	$scope.backToLanding = function() {
		$scope.hideFromView("itemCategory");
		$scope.hideFromView("itemList");
		$scope.hideFromView("categorySelection");
		$scope.hideFromView("itemDetail");
		$scope.hideFromView("itemCheckOut");
		$scope.hideFromView("itemCart");
	}
	$scope.switchToView = function(viewName, param1) {
		if (viewName == "itemCategory") {
			$("div#item-category-overlay").fadeInFromLeft();
		} else if (viewName == "itemList") {
			$scope.selectedCategory = param1;
			$("div#item-list-overlay").fadeInFromLeft();
		} else if (viewName == "categorySelection") {
			$("div#category-selection-overlay").fadeInFromTop();
		} else if (viewName == "itemDetail") {
			$scope.selectedItem = param1;
			$scope.totalItemPrice = $scope.selectedItem.price;
			$("div#item-detail-overlay").fadeInFromLeft();
		}
	}
	$scope.hideFromView = function(viewName) {
		if (viewName == "itemCategory") {
			$("div#item-category-overlay").fadeOutToLeft();
		} else if (viewName == "itemList") {
			$("div#item-list-overlay").fadeOutToLeft();
		} else if (viewName == "categorySelection") {
			$("div#category-selection-overlay").fadeOutToTop();
		} else if (viewName == "itemDetail") {
			$("div#item-detail-overlay").fadeOutToLeft();
		} else if (viewName == "itemCart") {
			$("div#item-checkout-overlay").fadeOutToLeft();
		} else if (viewName == "itemCheckOut") {
			$("div#item-cart-overlay").fadeOutToLeft();
		}
	}
	
	/*SOP Directive*/
	$scope.selectNewCategory = function(data) {
		$scope.selectedCategory = data;
		$scope.hideFromView("categorySelection");
	}
	
	/*Language Loading*/
	$scope.loadLanguageData = function() {
		$scope.loadingPercentage = 30;
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
				$scope.currentLocale = $scope.localeData[0];
				$scope.currentLanguageData = $scope.languageData[$scope.currentLocale.shortName];
				
				$scope.loadMenuData();
			} else {
				$scope.loadFailed();
			}
		}, function (error) {
			$scope.loadFailed();
	    });
	}
	/*Menu Loading*/
	$scope.loadMenuData = function() {
		$scope.loadingPercentage = 50;
		$scope.loadingText = "Loading Menu...";
		$http({
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			data: {
			},
			url: '${pageContext.request.contextPath}/order/getMenuData'
		}).then(function (response) {
			if (response != null && response.data != null && response.data.menuList != null) {
				$scope.menuList = response.data.menuList;
				
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
	
	/*Init Function*/
	$scope.backToLanding();
	$scope.beginLoading();
});
</script>