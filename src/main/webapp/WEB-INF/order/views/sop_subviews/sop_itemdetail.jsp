<body>
	<div id="item-detail-overlay" class="page-overlay">
		<div class="header-content sub-color d-flex flex-row">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('itemDetail')"
					class="empty-btn">
					<span class="md-resp-font back-icon"></span>
				</button>
			</div>
		</div>
		<div class="body-content scrollable-y">
			<div class="h-100 d-flex flex-column">
				<div class="align-self-center">
					<img class="max-img-height" src="{{selectedItem.path}}"
						alt="{{selectedItem.name}}">
				</div>
				<div
					class="w-100 flex-fill card d-flex flex-column align-self-center">
					<div class="w-100 align-self-center text-center">
						<span class="text-limiter lg-resp-font"><b>{{selectedItem.name}}</b></span>
					</div>
					<div class="w-100 align-self-center text-center">
						<span class="text-limiter three-liner xs-resp-font">{{selectedItem.description}}</span>
					</div>
					<hr class="ml-1 mr-1">
					<div class="flex-fill scrollable-y pb-2">
						<div class="row ml-0 mr-0">
							<div
								class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
								<div
									class="w-100 align-self-center text-limiter two-liner sm-resp-font">
									<b ng-show="selectedItem.type == 0">{{currentLanguageData.item_detail_comboPrice}}</b>
									<b ng-show="selectedItem.type == 1">{{currentLanguageData.item_detail_alacartePrice}}</b>
								</div>
							</div>
							<div
								class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
								<div
									class="w-100 align-self-center flex-fill d-flex flex-column">
									<div class="text-right text-nowrap sm-resp-font">{{priceTag}}{{selectedItem.price}}</div>
								</div>
							</div>
						</div>
						<div class="w-100"
							ng-show="selectedItem.type != '0' && itemModifierList == null">
							<hr class="ml-3 mr-3">
							<div class="row ml-0 mr-0">
								<div
									class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-column pr-0">
									<div class="text-limiter two-liner sm-resp-font">
										<b>{{currentLanguageData.item_detail_quantity}}</b>
									</div>
								</div>
								<div
									class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
									<div
										class="w-100 align-self-center flex-fill d-flex flex-column">
										<div class="row ml-0 mr-0">
											<div class="col-4 pl-0 pr-0 text-center">
												<img class="md-resp-img"
													ng-click="minusAlacarteQuantity(selectedItem)"
													src="${pageContext.request.contextPath}/assets/images/order/icon/minus_icon.svg" />
											</div>
											<div class="col-4 pl-0 pr-0 text-center">
												<span class="sm-resp-font">{{alacarteQuantity}}</span>
											</div>
											<div class="col-4 pl-0 pr-0 text-center">
												<img class="md-resp-img"
													ng-click="addAlacarteQuantity(selectedItem)"
													src="${pageContext.request.contextPath}/assets/images/order/icon/plus_icon.svg" />
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="w-100" ng-show="selectedItem.type = '0'"
							ng-repeat="comboTier in itemComboTierList">
							<hr class="ml-3 mr-3">
							<div class="row ml-0 mr-0">
								<div
									class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-column pr-0">
									<div class="text-limiter two-liner sm-resp-font"
										ng-class="{'tier-completed': comboTier.isTierCompleted}">
										<b>{{comboTier.name}}</b>
									</div>
									<div class="text-limiter two-liner resp-font">
										<span>{{comboTier.itemString}}</span>
									</div>
								</div>
								<div
									class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
									<div
										class="w-100 align-self-center flex-fill d-flex flex-column">
										<div class="text-right text-nowrap sm-resp-font"
											ng-show="comboTier.isTierCompleted">
											<button class="btn btn-primary btn-main sm-resp-font"
												type="button"
												ng-click="switchToView('tierSelection', comboTier)">{{priceTag}}{{comboTier.totalPrice}}</button>
										</div>
										<div class="text-right text-nowrap sm-resp-font"
											ng-show="!comboTier.isTierCompleted">
											<button class="btn btn-primary btn-main sm-resp-font"
												type="button"
												ng-click="switchToView('tierSelection', comboTier)">{{currentLanguageData.item_detail_select}}&nbsp;&gt;</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<hr class="ml-1 mr-1 mt-1 mb-1">
					<div class="mt-2 mb-2">
						<div class="row ml-0 mr-0">
							<div
								class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
								<div
									class="w-100 align-self-center text-limiter two-liner sm-resp-font">
									<b>{{currentLanguageData.item_detail_totalPrice}}</b>
								</div>
							</div>
							<div
								class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
								<div
									class="w-100 align-self-center flex-fill d-flex flex-column">
									<div class="text-right text-nowrap sm-resp-font">{{priceTag}}{{totalItemPrice}}</div>
								</div>
							</div>
						</div>
						<div class="row ml-1 mr-0 d-flex flex-column"
							ng-show="isReadyForCart">
							<button
								class="btn btn-primary btn-main sm-resp-font align-self-center"
								type="button" ng-click="addToCart()">
								<span class="resp-font cart-icon"></span>&nbsp;{{currentLanguageData.item_detail_addToCart}}
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>