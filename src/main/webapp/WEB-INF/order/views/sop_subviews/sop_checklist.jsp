<body>
	<div id="check-list-overlay" class="page-overlay">
		<div
			class="header-content sub-color d-flex flex-row justify-content-between">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('checkList')"
					class="empty-btn">
					<i class="fas fa-chevron-left sub-text-color xl-resp-font"></i>
				</button>
			</div>
			<div
				class="text-truncate align-self-center sub-text-color md-resp-font">
				<b>Order List</b>
			</div>
			<div>
				<!-- Empty Content -->
			</div>
		</div>
		<div class="body-content d-flex flex-column pt-2">
			<div
				class="flex-fill pl-1 pr-1 d-flex flex-column justify-content-center"
				ng-show="!isCheckOpen">
				<div class="w-100 align-self-center text-center">
					<span class="md-resp-font"><b>{{currentLanguageData.order_list_expired}}</b></span>
				</div>
			</div>
			<div
				class="flex-fill pl-1 pr-1 d-flex flex-column justify-content-center"
				ng-show="isCheckOpen && checkList.length <= 0">
				<div class="w-100 align-self-center text-center">
					<span class="md-resp-font"><b>{{currentLanguageData.order_list_empty_message}}</b></span>
				</div>
			</div>
			<div class="flex-fill scrollable-y" ng-show="isCheckOpen && checkList.length > 0">
				<div class="row ml-0 mr-0 pl-1 pr-1">
					<div class="col-12 cart-item pl-1 pr-1 mb-2"
						ng-repeat="checkItem in checkList">
						<div class="row ml-0 mr-0">
							<div class="col-12 pl-0 pr-0 xs-resp-font text-truncate">
								<b><u>{{currentLanguageData.order_list_order_time}}:&nbsp{{checkItem.datetime}}</u></b>
							</div>
						</div>
						<div class="row ml-0 mr-0" ng-if="(checkItem.type == 0 && checkItem.items.length == 0) || checkItem.type == 1">
							<div class="col-7 pl-0 pr-0 xs-resp-font">
								<b>{{checkItem.name}}</b>
							</div>
							<div class="col-2 pl-0 pr-0 xs-resp-font text-center flex-column d-flex justify-content-center">
								<b>x{{checkItem.quantity}}</b>
							</div>
							<div class="col-3 pl-0 pr-0 xs-resp-font text-right flex-column d-flex justify-content-center">
								<b>{{priceTag}}{{checkItem.price}}</b>
							</div>
						</div>
						<div class="row ml-0 mr-0" ng-if="checkItem.type == 0 && checkItem.items.length > 0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{checkItem.name}}</b>
							</div>
							<div class="col-2 pl-0 pr-0 xs-resp-font text-center">
								<b>x{{checkItem.quantity}}</b>
							</div>
							<div class="col-3 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{checkItem.price}}</b>
							</div>
							<div class="col-12 pl-0 pr-0 row ml-0 mr-0"
								ng-repeat="modifierItem in checkItem.items">
								<div class="col-9 pl-0 pr-0 xs-resp-font text-truncate">
									&nbsp;&nbsp;-{{modifierItem.name}}</div>
								<div class="col-3 pl-0 pr-0 xs-resp-font text-right">
									{{priceTag}}{{modifierItem.price}}</div>
							</div>
						</div>
						<div class="row ml-0 mr-0" ng-if="checkItem.type == 1"
							ng-repeat="comboItem in checkItem.items">
							<div class="col-12 pl-0 pr-0">
								<div class="row ml-0 mr-0">
									<div class="col-12 pl-0 pr-0 row ml-0 mr-0"
										ng-if="comboItem.items.length > 0">
										<div class="col-12 pl-0 pr-0 row ml-0 mr-0">
											<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">&nbsp;&nbsp;{{comboItem.name}}</div>
											<div class="col-2 pl-0 pr-0 xs-resp-font text-center">x{{comboItem.quantity}}</div>
											<div class="col-3 pl-0 pr-0 xs-resp-font text-right">+{{priceTag}}{{comboItem.price}}</div>
										</div>
										<div class="col-12 pl-0 pr-0 row ml-0 mr-0" ng-repeat="modifierItem in comboItem.items">
											<div class="col-9 pl-0 pr-0 xs-resp-font text-truncate">&nbsp;&nbsp;&nbsp;&nbsp;-{{modifierItem.name}}</div>
											<div class="col-3 pl-0 pr-0 xs-resp-font text-right">+{{priceTag}}{{modifierItem.price}}</div>
										</div>
									</div>
									<div class="col-12 pl-0 pr-0 row ml-0 mr-0"
										ng-if="comboItem.items.length == 0">
										<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">&nbsp;&nbsp;{{comboItem.name}}</div>
										<div class="col-2 pl-0 pr-0 xs-resp-font text-center">x{{comboItem.quantity}}</div>
										<div class="col-3 pl-0 pr-0 xs-resp-font text-right">+{{priceTag}}{{comboItem.price}}</div>
									</div>
								</div>
							</div>
						</div>
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{currentLanguageData.cart_subtotal}}</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{checkItem.total_price}}</b>
							</div>
						</div>
					</div>
					<div class="col-12 pl-1 pr-1 mb-2 text-center">
						<span>--End Of Order--</span>
					</div>
					<!--<div class="col-12 cart-item pl-1 pr-1 mb-2">
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{currentLanguageData.cart_subtotal}}</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{cartSubtotalPrice}}</b>
							</div>
						</div>
					</div>-->
					<!--<div class="col-12 cart-item pl-1 pr-1 mb-2">
						<div class="row ml-0 mr-0" ng-repeat="taxObj in taxDisplayList">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{taxObj.name}}&nbsp;{{taxObj.rate}}%</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{taxObj.price}}</b>
							</div>
						</div>
						<div class="row ml-0 mr-0">
							<div class="col-7 pl-0 pr-0 xs-resp-font text-truncate">
								<b>{{currentLanguageData.cart_total}}</b>
							</div>
							<div class="col-5 pl-0 pr-0 xs-resp-font text-right">
								<b>{{priceTag}}{{cartTotalPrice}}</b>
							</div>
						</div>
					</div>-->
				</div>
			</div>
		</div>
	</div>
</body>