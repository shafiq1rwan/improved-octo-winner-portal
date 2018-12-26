<body>
	<div id="item-cart-overlay" class="page-overlay">
		<div
			class="header-content sub-color d-flex flex-row justify-content-between">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('itemCart')"
					class="empty-btn">
					<span class="md-resp-font back-icon"></span>
				</button>
			</div>
			<div
				class="text-truncate align-self-center main-text-color md-resp-font">
				<b>Checkout</b>
			</div>
			<div>
				<!-- Empty Content -->
			</div>
		</div>
		<div class="body-content d-flex flex-column pt-2">
			<div
				class="flex-fill pl-1 pr-1 d-flex flex-column justify-content-center"
				ng-show="cart.length <= 0">
				<span class="w-100 md-resp-font text-center"><b>The cart
						is empty. Please proceed to order. </b></span>
			</div>
			<div class="flex-fill scrollable-y" ng-show="cart.length > 0">
				<div class="row ml-0 mr-0 pl-1 pr-1">
					<div class="col-12 cart-item pl-1 pr-1 mb-2"
						ng-repeat="cartItem in cart">
						<div class="row ml-0 mr-0" ng-if="cartItem.value = '0'">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{cartItem.name}}</b>
							</div>
							<div class="col-2 pl-0 pr-0 xs-resp-font text-center">
								<b>x1</b>
							</div>
							<div class="col-3 pl-0 pr-0 xs-resp-font text-right">
								<b>{{systemData.priceTag}}{{cartItem.price}}</b>
							</div>
						</div>
						<div class="row ml-0 mr-0" ng-if="cartItem.value = '0'"
							ng-repeat="comboItem in cartItem.comboData">
							<div class="col-12 pl-0 pr-0">
								<div class="row ml-0 mr-0" ng-if="itemData.selectedQuantity > 0"
									ng-repeat="itemData in comboItem.itemList">
									<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">&nbsp;&nbsp;{{itemData.name}}</div>
									<div class="col-2 pl-0 pr-0 xs-resp-font text-center">x{{itemData.selectedQuantity}}</div>
									<div class="col-3 pl-0 pr-0 xs-resp-font text-right">+{{systemData.priceTag}}{{itemData.price}}</div>
								</div>
							</div>
						</div>
						<div class="row ml-0 mr-0" ng-if="cartItem.value != '0'">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{cartItem.name}}</b>
							</div>
							<div class="col-2 pl-0 pr-0 xs-resp-font text-center">
								<b>x{{cartItem.quantity}}</b>
							</div>
							<div class="col-3 pl-0 pr-0 xs-resp-font text-right">
								<b>{{systemData.priceTag}}{{cartItem.price}}</b>
							</div>
						</div>
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>Total</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{systemData.priceTag}}{{cartItem.totalPrice}}</b>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>