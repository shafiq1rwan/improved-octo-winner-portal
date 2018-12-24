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
	$scope.systemData;
	$scope.localeData;
	$scope.languageData;
	$scope.menuList;
	/*Dialog Config*/
	$scope.isAllowKeyboardDismissal = false;
	$scope.isAllowBackdropClick = false;
	$scope.isAllowCloseButton = false;
	$scope.dialogData = {};
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
	$scope.itemComboTierList;
	$scope.itemModifierList;
	$scope.selectedTier;
	$scope.isReadyForCart;
	$scope.isProcessingCartData;
	$scope.totalItemPrice;
	$scope.cart = [];
	
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
		$scope.hideFromView("tierSelection");
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
			$scope.generateItemDetails($scope.selectedItem);
			$("div#item-detail-overlay").fadeInFromLeft();
		} else if (viewName == "tierSelection") {
			$scope.selectedTier = param1;
			$("div#tier-selection-overlay").fadeInFromTop();
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
		} else if (viewName == "tierSelection") {
			$("div#tier-selection-overlay").fadeOutToTop();
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
	
	/*Other Fn*/
	$scope.generateItemDetails = function(data) {
		$scope.isReadyForCart = false;
		$scope.itemComboTierList = null;
		$scope.itemModifierList = null;
		$scope.totalItemPrice = data.price;
		
		if (data.type == "0") {
			$scope.itemComboTierList = [];
			var tierStep = 1;
			for (var x = 0; x < data.comboList.length; x++) {
				var tierData = data.comboList[x];
				
				var tierObject = {};
				tierObject.name = tierData.name;
				tierObject.totalPrice = "0.00";
				tierObject.isTierCompleted = false;
				tierObject.quantity = parseInt(tierData.quantity);
				tierObject.selectedQuantity = 0;
				tierObject.itemString = "";
				tierObject.tierNumber = tierStep++;
				
				var itemList = []
				for (var y = 0; y < tierData.itemList.length; y++) {
					var itemData = tierData.itemList[y];
					
					var itemObject = {};
					itemObject.id = itemData.id;
					itemObject.name = itemData.name;
					itemObject.path = itemData.path;
					itemObject.price = itemData.price;
					itemObject.selectedQuantity = 0;
					
					itemList.push(itemObject);
				}
				tierObject.itemList = itemList;
				
				$scope.itemComboTierList.push(tierObject);
			}
		} else {
			if (data.type == "1") {
				$scope.selectedItem.quantity = 0;
			}
		}
	}
	$scope.generateItemString = function(tierData) {
		var itemString = "";
		
		for (var x = 0; x < tierData.itemList.length; x++) {
			var itemData = tierData.itemList[x];
			if (itemData.selectedQuantity > 0) {
				if (itemString != "") {
					itemString += ", ";
				}
				itemString += itemData.name + " x " + itemData.selectedQuantity;
			}
		}
		
		return itemString;
	}
	$scope.addItemQuantity = function(itemData, tierData) {
		if (tierData.selectedQuantity >= tierData.quantity) {
			return;
		}
		itemData.selectedQuantity += 1;
		tierData.selectedQuantity += 1;
		tierData.totalPrice = Number(parseFloat(tierData.totalPrice) + parseFloat(itemData.price)).toFixed(2);
		$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) + parseFloat(itemData.price)).toFixed(2);
		tierData.itemString = $scope.generateItemString(tierData);
		if (tierData.selectedQuantity == tierData.quantity) {
			tierData.isTierCompleted = true;
			var isAllTierCompleted = true;
			for (var x = 0; x < $scope.itemComboTierList.length; x++) {
				var itemData = $scope.itemComboTierList[x];
				if (!itemData.isTierCompleted) {
					isAllTierCompleted = false;
				}
			}
			$scope.isReadyForCart = isAllTierCompleted;
			if ($scope.selectedTier.tierNumber < $scope.itemComboTierList.length) {
				$scope.selectedTier = $scope.itemComboTierList[$scope.selectedTier.tierNumber];
				$("a#nav-tier-" + ($scope.selectedTier.tierNumber))[0].scrollIntoView({behavior: "smooth", block: "start", inline: "start"});
			} else {
				$scope.hideFromView("tierSelection");
			}
		}
	}
	$scope.minusItemQuantity = function(itemData, tierData) {
		if (itemData.selectedQuantity == 0 || tierData.selectedQuantity == 0) {
			return;
		}
		$scope.isReadyForCart = false;
		tierData.isTierCompleted = false;
		itemData.selectedQuantity -= 1;
		tierData.selectedQuantity -= 1;
		tierData.totalPrice = Number(parseFloat(tierData.totalPrice) - parseFloat(itemData.price)).toFixed(2);
		$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) - parseFloat(itemData.price)).toFixed(2);
		tierData.itemString = $scope.generateItemString(tierData);
	}
	$scope.addToCart = function() {
		if (!$scope.isProcessingCartData) {
			$scope.isProcessingCartData = true;
			var cartObj = $.extend(true, {}, $scope.selectedItem);
			delete cartObj.comboList;
			cartObj.comboData = $.extend(true, {}, $scope.itemComboTierList);
			cartObj.modifierData = $.extend(true, {}, $scope.itemModifierList);
			
			$scope.cart.push(cartObj);
			
			var dialogOption = {};
			dialogOption.title = $scope.currentLanguageData.dialog_cart_add_success_title;
			dialogOption.message = $scope.currentLanguageData.dialog_cart_add_success_title;
			dialogOption.button1 = {
					name: $scope.currentLanguageData.dialog_button_ok,
					fn: function() {
						$scope.hideFromView("itemDetail")
						$("div#modal-dialog").modal("hide");
					}
			}
			$scope.displayDialog(dialogOption);
			$scope.isProcessingCartData = false;
		}
	}
	
	/*Dialog Fn*/
	$scope.displayDialog = function(dialogOption) {
		$scope.dialogData = {};
		$scope.dialogData.title = dialogOption.title;
		$scope.dialogData.message = dialogOption.message;
		$scope.dialogData.button1 = dialogOption.button1;
		$scope.dialogData.button2 = dialogOption.button2;
		$scope.dialogData.isButton1 = typeof $scope.dialogData.button1 !== "undefined";
		$scope.dialogData.isButton2 = typeof $scope.dialogData.button2 !== "undefined";
		$("div#modal-dialog").modal({backdrop: $scope.isAllowBackdropClick, keyboard: $scope.isAllowKeyboardDismissal});
	}
	
	/*System Loading*/
	$scope.loadSystemData = function() {
		$scope.loadingPercentage = 10;
		$scope.loadingText = "Loading System Data...";
		$http({
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			data: {
			},
			url: '${pageContext.request.contextPath}/order/getSystemData'
		}).then(function (response) {
			if (response != null && response.data != null) {
				$scope.systemData = response.data;
				
				$scope.loadLanguageData();
			} else {
				$scope.loadFailed();
			}
		}, function (error) {
			$scope.loadFailed();
	    });
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
		$scope.loadSystemData();
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