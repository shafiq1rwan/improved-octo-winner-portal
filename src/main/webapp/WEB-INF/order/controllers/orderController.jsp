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

byodApp.controller('OrderController', function($scope, $http, $routeParams, $timeout) {
	/*Config Data*/
	$scope.localeData;
	$scope.languageData;
	$scope.menuList;
	$scope.priceTag;
	$scope.storeName;
	$scope.tableId;
	$scope.cart
	$scope.cartTotalPrice;
	/*Dialog Config*/
	$scope.isAllowKeyboardDismissal = false;
	$scope.isAllowBackdropClick = false;
	$scope.dialogData = {};
	/*Variables*/
	$scope.isLoadingFailed;
	$scope.loadingPercentage;
	$scope.loadingText;
	$scope.currentLocale;
	$scope.currentLanguageData;
	$scope.selectedCategory;
	$scope.selectedItem;
	$scope.itemComboTierList;
	$scope.itemModifierList;
	$scope.alacarteQuantity;
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
		} else if (viewName == "itemCart") {
			$scope.getCartTotal();
			$("div#item-cart-overlay").fadeInFromLeft();
		}
	}
	$scope.hideFromView = function(viewName) {
		if (viewName == "itemCategory") {
			if ($scope.cart.length > 0) {
				var dialogOption = {};
				dialogOption.title = $scope.currentLanguageData.dialog_reset_cart_title;
				dialogOption.message = "";
				dialogOption.button1 = {
						name: $scope.currentLanguageData.dialog_button_yes,
						fn: function() {
							$("div#item-category-overlay").fadeOutToLeft();
							$("div#modal-dialog").modal("hide");
						}
				}
				dialogOption.button2 = {
						name: $scope.currentLanguageData.dialog_button_no,
						fn: function() {
							$("div#modal-dialog").modal("hide");
						}
				}
				$scope.displayDialog(dialogOption);
			} else {
				$("div#item-category-overlay").fadeOutToLeft();
			}
		} else if (viewName == "itemList") {
			$("div#item-list-overlay").fadeOutToLeft();
		} else if (viewName == "categorySelection") {
			$("div#category-selection-overlay").fadeOutToTop();
		} else if (viewName == "itemDetail") {
			$("div#item-detail-overlay").fadeOutToLeft();
		} else if (viewName == "tierSelection") {
			$("div#tier-selection-overlay").fadeOutToTop();
		} else if (viewName == "itemCart") {
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
		$scope.alacarteQuantity = 0;
		$scope.totalItemPrice = "0.00";
		
		if (data.type == "0") {
			$scope.totalItemPrice = data.price;
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
				
				var itemList = [];
				var hasModifier = false;
				for (var y = 0; y < tierData.itemList.length; y++) {
					var itemData = tierData.itemList[y];
					
					var itemObject = {};
					itemObject.id = itemData.id;
					itemObject.name = itemData.name;
					itemObject.path = itemData.path;
					itemObject.price = itemData.price;
					if (itemData.modifierGroupList.length > 0) {
						hasModifier = true;
						itemObject.modifierGroupList = itemData.modifierGroupList;
						itemObject.modifierGroupData = [];
					}
					itemObject.selectedQuantity = 0;
					
					itemList.push(itemObject);
				}
				if (hasModifier) {
					tierObject.isModifierCompleted = true;
				}
				tierObject.itemList = itemList;
				
				$scope.itemComboTierList.push(tierObject);
			}
		} else {
			if (data.modifierList.length > 0) {
				$scope.itemModifierList = {};
				
				$scope.itemModifierList.modifierGroupList = data.modifierList;
				$scope.itemModifierList.modifierGroupData = [];
			} else {
				$scope.itemModifierList = null;
			}
		}
	}
	$scope.generateItemString = function(tierData) {
		var itemString = "";
		
		for (var x = 0; x < tierData.itemList.length; x++) {
			var itemData = tierData.itemList[x];
			if (itemData.selectedQuantity > 0) {
				if (itemData.modifierGroupData) {
					for (var y = 0; y < itemData.modifierGroupData.length; y++) {
						var modifierGroupData = itemData.modifierGroupData[y];
						var isModifierCompleted = true;
						var modifierString = "";
						for (z = 0; z < modifierGroupData.length; z++) {
							var modifierData = modifierGroupData[z];
							if (modifierData.selectedModifier == null) {
								isModifierCompleted = false;
							} else {
								if (modifierString != "") {
									modifierString += ", ";
								}
								modifierString += modifierData.selectedModifier.name;
							}
						}
						if (isModifierCompleted) {
							if (itemString != "") {
								itemString += ", ";
							}
							itemString += itemData.name + "(" + modifierString + ") x 1 ";
						}
					}
				} else {
					if (itemString != "") {
						itemString += ", ";
					}
					itemString += itemData.name + " x " + itemData.selectedQuantity;
				}
			}
		}
		
		return itemString;
	}
	$scope.addAlacarteQuantity = function(data) {
		$scope.alacarteQuantity += 1;
		$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) + parseFloat(data.price)).toFixed(2);
		
		if ($scope.itemModifierList != null) {
			var modifierGroupData = [];
			
			for (var x = 0; x < $scope.itemModifierList.modifierGroupList.length; x++) {
				var oriModiferData = $scope.itemModifierList.modifierGroupList[x];
				var modifierData = {};
				modifierData.name = oriModiferData.name;
				modifierData.modifierList = oriModiferData.modifierList;
				modifierData.selectedModifier = null;
				modifierData.lastSelectedModifier = null;
				
				modifierGroupData.push(modifierData);
			}
			
			$scope.itemModifierList.modifierGroupData.push(modifierGroupData);
		}
		
		$scope.checkCartReadiness();
	}
	$scope.minusAlacarteQuantity = function(data) {
		if ($scope.alacarteQuantity > 0) {
			$scope.alacarteQuantity -= 1;
			$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) - parseFloat(data.price)).toFixed(2);
			
			if ($scope.itemModifierList != null) {
				var delModifierGroupData = $scope.itemModifierList.modifierGroupData.pop();
				if (delModifierGroupData) {
					for (var x = 0; x < delModifierGroupData.length; x++) {
						var delModifierData = delModifierGroupData[x];
						if (delModifierData.selectedModifier && delModifierData.selectedModifier != null) {
							$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) - parseFloat(delModifierData.selectedModifier.price)).toFixed(2);
						}
					}
				}
			}
		}
		
		$scope.checkCartReadiness();
	}
	$scope.updateAlacarteModifierData = function(modifierGroupData) {
		if (modifierGroupData.lastSelectedModifier != null) {
			$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) - parseFloat(modifierGroupData.lastSelectedModifier.price)).toFixed(2);
		}
		modifierGroupData.lastSelectedModifier = angular.copy(modifierGroupData.selectedModifier);
		$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) + parseFloat(modifierGroupData.selectedModifier.price)).toFixed(2);
		
		$scope.checkCartReadiness();
	}
	$scope.addItemQuantity = function(itemData, tierData) {
		if (tierData.selectedQuantity >= tierData.quantity) {
			return;
		}
		itemData.selectedQuantity += 1;
		tierData.selectedQuantity += 1;
		tierData.totalPrice = Number(parseFloat(tierData.totalPrice) + parseFloat(itemData.price)).toFixed(2);
		$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) + parseFloat(itemData.price)).toFixed(2);
		
		if (itemData.modifierGroupList) {
			tierData.isModifierCompleted = false;
			
			var modifierGroupData = [];
			
			for (var x = 0; x < itemData.modifierGroupList.length; x++) {
				var oriModiferData = itemData.modifierGroupList[x];
				var modifierData = {};
				modifierData.name = oriModiferData.name;
				modifierData.modifierList = oriModiferData.modifierList;
				modifierData.selectedModifier = null;
				modifierData.lastSelectedModifier = null;
				
				modifierGroupData.push(modifierData);
			}
			
			itemData.modifierGroupData.push(modifierGroupData);
		}
		
		tierData.itemString = $scope.generateItemString(tierData);
		
		if (tierData.selectedQuantity == tierData.quantity && tierData.isModifierCompleted) {
			tierData.isTierCompleted = true;
			$scope.checkCartReadiness();
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
		
		if (itemData.modifierGroupList) {
			if (itemData.modifierGroupData.length > 0) {
				var delModifierGroupData = itemData.modifierGroupData.pop();
				if (delModifierGroupData) {
					for (var x = 0; x < delModifierGroupData.length; x++) {
						var delModifierData = delModifierGroupData[x];
						if (delModifierData.selectedModifier && delModifierData.selectedModifier != null) {
							tierData.totalPrice = Number(parseFloat(tierData.totalPrice) - parseFloat(delModifierData.selectedModifier.price)).toFixed(2);
							$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) - parseFloat(delModifierData.selectedModifier.price)).toFixed(2);
						}
					}
				}
			}
			
			var isModifierCompleted = true;
			for (var x = 0; x < tierData.itemList.length; x++) {
				var indItemData = tierData.itemList[x];
				if (indItemData.modifierGroupData) {
					for (var y = 0; y < indItemData.modifierGroupData.length; y++) {
						var indModiferData = indItemData.modifierGroupData[y];
						for (var z = 0; z < indModiferData.length; z++) {
							var modifierData = indModiferData[z];
							if (!modifierData.selectedModifier) {
								isModifierCompleted = false;
								break;
							}
						}
					}
				}
			}
			
			tierData.isModifierCompleted = isModifierCompleted;
		}
	}
	$scope.updateModifierData = function(modifierGroupData, tierData) {
		if (modifierGroupData.lastSelectedModifier != null) {
			tierData.totalPrice = Number(parseFloat(tierData.totalPrice) - parseFloat(modifierGroupData.lastSelectedModifier.price)).toFixed(2);
			$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) - parseFloat(modifierGroupData.lastSelectedModifier.price)).toFixed(2);
		}
		modifierGroupData.lastSelectedModifier = angular.copy(modifierGroupData.selectedModifier);
		tierData.totalPrice = Number(parseFloat(tierData.totalPrice) + parseFloat(modifierGroupData.selectedModifier.price)).toFixed(2);
		$scope.totalItemPrice = Number(parseFloat($scope.totalItemPrice) + parseFloat(modifierGroupData.selectedModifier.price)).toFixed(2);
		
		var isTierModiferCompleted = true;
		for (var x = 0; x < tierData.itemList.length; x++) {
			var itemData = tierData.itemList[x];
			if (itemData.modifierGroupData) {
				for (var y = 0; y < itemData.modifierGroupData.length; y++) {
					var modifierGroupData = itemData.modifierGroupData[y];
					for (var z = 0; z < modifierGroupData.length; z++) {
						var modifierData = modifierGroupData[z];
						if (!modifierData.selectedModifier) {
							isTierModiferCompleted = false;
							break;
						}
					}
				}
			}
		}
		tierData.isModifierCompleted = isTierModiferCompleted;
		tierData.itemString = $scope.generateItemString(tierData);
		if (tierData.selectedQuantity == tierData.quantity && tierData.isModifierCompleted) {
			tierData.isTierCompleted = true;
			$scope.checkCartReadiness();
		}
	}
	$scope.checkCartReadiness = function() {
		if ($scope.itemComboTierList != null) {
			//Combo Readiness
			var isAllTierCompleted = true;
			for (var x = 0; x < $scope.itemComboTierList.length; x++) {
				var itemData = $scope.itemComboTierList[x];
				if (!itemData.isTierCompleted) {
					isAllTierCompleted = false;
					break;
				}
			}
			$scope.isReadyForCart = isAllTierCompleted;
			if ($scope.isReadyForCart) {
				$scope.hideFromView("tierSelection");
			} else {
				if ($scope.selectedTier.tierNumber < $scope.itemComboTierList.length) {
					var currentTierIndex = $scope.selectedTier.tierNumber - 1;
					while ((currentTierIndex < $scope.itemComboTierList.length) && $scope.itemComboTierList[currentTierIndex].isTierCompleted) {
						currentTierIndex++;
					}
					if (currentTierIndex == $scope.itemComboTierList.length) {
						var isAllCompleted = true;
						for (var x = 0; x < $scope.itemComboTierList.length; x++) {
							var itemData = $scope.itemComboTierList[x];
							if (!itemData.isTierCompleted) {
								isAllCompleted = false;
								$scope.selectedTier = $scope.itemComboTierList[x];
								$("a#nav-tier-" + (x + 1))[0].scrollIntoView({behavior: "smooth", block: "start", inline: "start"});
								break;
							}
						}
						if (isAllCompleted) {
							//Just In Case
							$scope.isReadyForCart = true;
							$scope.hideFromView("tierSelection");
						}
					} else {
						$scope.selectedTier = $scope.itemComboTierList[currentTierIndex];
						$("a#nav-tier-" + ($scope.selectedTier.tierNumber))[0].scrollIntoView({behavior: "smooth", block: "start", inline: "start"});
					}
				} else {
					for (var x = 0; x < $scope.itemComboTierList.length; x++) {
						var itemData = $scope.itemComboTierList[x];
						if (!itemData.isTierCompleted) {
							$scope.selectedTier = $scope.itemComboTierList[x];
							$("a#nav-tier-" + (x + 1))[0].scrollIntoView({behavior: "smooth", block: "start", inline: "start"});
							break;
						}
					}
				}
			}
		} else {
			//Alacarte Readiness
			if ($scope.alacarteQuantity > 0) {
				if ($scope.itemModifierList && $scope.itemModifierList.modifierGroupData.length > 0) {
					var isModifierCompleted = true;
					for (var x = 0; x < $scope.itemModifierList.modifierGroupData.length; x++) {
						var modifierGroupData = $scope.itemModifierList.modifierGroupData[x];
						for (var y = 0; y < modifierGroupData.length; y++) {
							var modifierData = modifierGroupData[y];
							if (!modifierData.selectedModifier) {
								isModifierCompleted = false;
								break;
							}
						}
					}
					
					$scope.isReadyForCart = isModifierCompleted;
				} else {
					$scope.isReadyForCart = true;
				}
			} else {
				$scope.isReadyForCart = false;
			}
		}
	}
	$scope.addToCart = function() {
		if (!$scope.isProcessingCartData) {
			$scope.isProcessingCartData = true;
			var cartObj = angular.copy($scope.selectedItem);
			if (cartObj.type == '0') {
				cartObj.quantity = 1;
			} else {
				cartObj.quantity = $scope.alacarteQuantity;
			}
			cartObj.totalPrice = $scope.totalItemPrice;
			delete cartObj.comboList;
			cartObj.comboData = angular.copy($scope.itemComboTierList);
			cartObj.modifierData = angular.copy($scope.itemModifierList);
			
			$scope.cart.push(cartObj);
			console.log(cartObj)
			
			var dialogOption = {};
			dialogOption.title = $scope.currentLanguageData.dialog_cart_add_success_title;
			dialogOption.message = "";
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
	$scope.getCartTotal = function() {
		var total = 0;
	    for (var i = 0; i < $scope.cart.length; i++){
	    	total += parseFloat($scope.cart[i].totalPrice);
	    }
	    $scope.cartTotalPrice = total.toFixed(2);
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
	
	/*Menu Loading*/
	$scope.loadStoreData = function() {
		$scope.loadingPercentage = 20;
		$scope.loadingText = "Loading Store Data...";
		$http({
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			params: {
				storeId: $routeParams.storeId,
				tableId: $routeParams.tableId
			},
			url: '${pageContext.request.contextPath}/order/getStoreData'
		}).then(function (response) {
			if (response != null && response.data != null && response.data.resultCode != null) {
				if (response.data.resultCode == "00") {
					$scope.menuList = response.data.menuList;
					$scope.storeName = response.data.storeName;
					$scope.tableId = $routeParams.tableId;
					$scope.priceTag = response.data.priceTag;
					
					$scope.loadLanguageData();
				} else {
					$scope.loadFailed(response.data.resultMessage);
				}
			} else {
				$scope.loadFailed();
			}
		}, function (error) {
			$scope.loadFailed();
	    });
	}
	/*Language Loading*/
	$scope.loadLanguageData = function() {
		$scope.loadingPercentage = 80;
		$scope.loadingText = "Loading Language Pack...";
		$http({
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			params: {
			},
			url: '${pageContext.request.contextPath}/order/getLanguagePack'
		}).then(function (response) {
			if (response != null && response.data != null && response.data.resultCode != null) {
				if (response.data.resultCode == "00") {
					$scope.localeData = response.data.localeData;
					$scope.languageData = response.data.languageData;
					$scope.currentLocale = $scope.localeData[0];
					$scope.currentLanguageData = $scope.languageData[$scope.currentLocale.shortName];
					
					$scope.loadSuccess();
				} else {
					$scope.loadFailed(response.data.resultMessage);
				}
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
		$scope.loadStoreData();
	}
	$scope.loadSuccess = function() {
		$scope.loadingPercentage = 100;
		$scope.loadingText = "Loading Completed.";
		$timeout(function() {$("div#loading-overlay").slideUp()}, 1000);
	}
	$scope.loadFailed = function(message) {
		$scope.isLoadingFailed = true;
		$scope.loadingPercentage = 0;
		
		if (message) {
			$scope.loadingText = message;
		} else {
			$scope.loadingText = "Loading Failed.";
		}
	}
	
	/*Init Function*/
	$scope.backToLanding();
	$scope.beginLoading();
});
</script>