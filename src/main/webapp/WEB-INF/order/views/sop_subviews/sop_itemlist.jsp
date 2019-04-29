<body>
	<div id="item-list-overlay" class="page-overlay">
		<div
			class="header-content sub-color d-flex flex-row justify-content-between">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('itemList')"
					class="empty-btn">
					<i class="fas fa-chevron-left sub-text-color xl-resp-font"></i>
				</button>
			</div>
			<span
				class="text-center text-truncate sub-text-color align-self-center flex-fill md-resp-font dropdown-selector pr-4"
				ng-click="switchToView('categorySelection')"><span class=""><b>{{selectedCategory.name}}</b></span><i
				class="fas fa-caret-down xl-resp-font mr-2"></i></span>
			<div class="align-self-center mr-1">
				<button type="button" ng-click="switchToView('itemCart')"
					class="empty-btn badge-btn">
					<i class="fal fa-shopping-cart sub-text-color xl-resp-font"></i> <span
						class="badge badge-pill badge-danger xxs-resp-font ng-binding">{{cart.length}}</span>
				</button>
			</div>
		</div>
		<div class="body-content d-flex flex-column scrollable-y">
			<div class="flex-fill">
				<div class="row card-container mr-0 ml-0">
					<div
						class="col-6 col-sm-6 col-md-4 col-lg-4 col-xl-3 sub-card-container"
						ng-repeat="data in selectedCategory.itemList">
						<div class="card" ng-click="switchToView('itemDetail', data)">
							<img class="max-img-height card-img-top" ng-src="${pageContext.request.contextPath}/{{imagePath}}{{data.path}}"
								alt="{{data.name}}">
							<div class="main-color main-text-color text-center sm-resp-font">
								<b>{{priceTag}}{{data.price}}</b>
							</div>
							<div class="card-body d-flex flex-row">
								<div class="sub-card-body text-center d-flex flex-column justify-content-center">
									<span
										class="w-100 card-title align-self-center xs-resp-font">
										<b>{{data.name}}</b>
									</span>
									<!--<span
										class="w-100 card-title align-self-center resp-font">{{data.description}}
									</span>-->
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>