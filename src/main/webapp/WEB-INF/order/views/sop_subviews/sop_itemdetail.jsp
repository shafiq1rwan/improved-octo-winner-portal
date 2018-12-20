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
			<div class="w-100 d-flex flex-column">
				<div class="w-100 d-flex flex-column align-self-center">
					<img class="max-img-height align-self-center"
						src="{{selectedItem.path}}" alt="{{selectedItem.name}}">
				</div>
				<div class="w-100 card d-flex flex-column align-self-center pb-2">
					<span class="align-self-center text-limiter lg-resp-font"><b>{{selectedItem.name}}</b></span>
					<span
						class="align-self-center text-center text-limiter three-liner xs-resp-font">{{selectedItem.description}}</span>
					<hr class="ml-1 mr-1">
					<div class="row ml-0 mr-0">
						<div
							class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
							<div
								class="w-100 align-self-center text-limiter two-liner sm-resp-font">
								<b ng-show="selectedItem.type == 0">Combo Price</b> <b
									ng-show="selectedItem.type == 1">À la carte Price</b>
							</div>
						</div>
						<div
							class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
							<div class="w-100 align-self-center flex-fill d-flex flex-column">
								<div class="text-right text-nowrap sm-resp-font">{{systemData.priceTag}}{{selectedItem.price}}</div>
							</div>
						</div>
					</div>
					<div class="w-100" ng-repeat="comboTier in itemComboTierList">
						<hr class="ml-3 mr-3">
						<div class="row ml-0 mr-0">
							<div
								class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
								<div
									class="w-100 align-self-center text-limiter two-liner sm-resp-font">
									<b>{{comboTier.name}}</b>
								</div>
							</div>
							<div
								class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
								<div
									class="w-100 align-self-center flex-fill d-flex flex-column">
									<div class="text-right text-nowrap sm-resp-font"
										ng-show="comboTier.isTierCompleted">{{systemData.priceTag}}{{comboTier.totalPrice}}</div>
									<div class="text-right text-nowrap sm-resp-font"
										ng-show="!comboTier.isTierCompleted">
										<button class="btn btn-primary btn-main md-resp-font"
											type="button"
											ng-click="switchToView('tierSelection', comboTier)">Select
											&gt;</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<hr class="ml-3 mr-3">
					<div class="row ml-0 mr-0">
						<div
							class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
							<div
								class="w-100 align-self-center text-limiter two-liner sm-resp-font">
								<b>Total Price</b>
							</div>
						</div>
						<div
							class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
							<div class="w-100 align-self-center flex-fill d-flex flex-column">
								<div class="text-right text-nowrap sm-resp-font">{{systemData.priceTag}}{{totalItemPrice}}</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>