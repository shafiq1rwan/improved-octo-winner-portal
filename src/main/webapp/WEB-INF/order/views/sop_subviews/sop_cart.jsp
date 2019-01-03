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
				<b>{{currentLanguageData.cart_summary}}</b>
			</div>
			<div>
				<!-- Empty Content -->
			</div>
		</div>
		<div class="body-content d-flex flex-column pt-2">
			<div
				class="flex-fill pl-1 pr-1 d-flex flex-column justify-content-center"
				ng-show="cart.length <= 0">
				<div class="w-100 align-self-center text-center">
					<span class="md-resp-font"><b>{{currentLanguageData.cart_empty_message}}</b></span>
				</div>
				<div class="align-self-center">
					<button class="btn btn-primary btn-main md-resp-font" type="button"
						ng-click="hideFromView('itemCart')">{{currentLanguageData.landing_orderNow}}</button>
				</div>
			</div>
			<div class="flex-fill scrollable-y" ng-show="cart.length > 0">
				<div class="row ml-0 mr-0 pl-1 pr-1">
					<div class="col-12 cart-item pl-1 pr-1 mb-2"
						ng-repeat="cartItem in cart">
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{cartItem.name}}</b>
							</div>
							<div class="col-2 pl-0 pr-0 xs-resp-font text-center">
								<b>x{{cartItem.quantity}}</b>
							</div>
							<div class="col-3 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{cartItem.price}}</b>
							</div>
						</div>
						<div class="row ml-0 mr-0" ng-if="cartItem.value = '0'"
							ng-repeat="comboItem in cartItem.comboData">
							<div class="col-12 pl-0 pr-0">
								<div class="row ml-0 mr-0" ng-if="itemData.selectedQuantity > 0"
									ng-repeat="itemData in comboItem.itemList">
									<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">&nbsp;&nbsp;{{itemData.name}}</div>
									<div class="col-2 pl-0 pr-0 xs-resp-font text-center">x{{itemData.selectedQuantity}}</div>
									<div class="col-3 pl-0 pr-0 xs-resp-font text-right">+{{priceTag}}{{itemData.price}}</div>
								</div>
							</div>
						</div>
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{currentLanguageData.cart_total}}</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{cartItem.totalPrice}}</b>
							</div>
						</div>
					</div>
					<div class="col-12 cart-item pl-1 pr-1 mb-2">
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{currentLanguageData.cart_total}}</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{getCartTotal()}}</b>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="pt-1 pb-1 align-self-center" ng-show="cart.length > 0">
				<button class="btn btn-primary btn-main sm-resp-font" type="button"
					ng-click="">{{currentLanguageData.cart_checkout}}&nbsp;({{priceTag}}{{getCartTotal()}})</button>
			</div>
		</div>
	</div>
</body>