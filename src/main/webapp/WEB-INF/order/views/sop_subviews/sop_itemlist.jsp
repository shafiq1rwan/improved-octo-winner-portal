<body>
	<div id="item-list-overlay" class="page-overlay">
		<div
			class="header-content sub-color d-flex flex-row justify-content-between">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('itemList')"
					class="empty-btn">
					<span class="md-resp-font back-icon"></span>
				</button>
			</div>
			<span
				class="text-center text-truncate sub-text-color align-self-center flex-fill md-resp-font dropdown-selector mr-2"
				ng-click="switchToView('categorySelection')"><b>{{selectedCategory.name}}</b></span>
		</div>
		<div class="body-content scrollable-y">
			<div class="row card-container" style="margin: 0;">
				<div
					class="col-6 col-sm-6 col-md-4 col-lg-4 col-xl-3 sub-card-container"
					ng-repeat="data in selectedCategory.itemList">
					<div class="card" ng-click="switchToView('itemDetail', data)">
						<img class="max-img-height card-img-top" ng-src="{{data.path}}" alt="{{data.name}}">
						<div class="main-color main-text-color text-center sm-resp-font">
							<b>{{systemData.priceTag}}{{data.price}}</b>
						</div>
						<div class="card-body d-flex flex-row">
							<div
								class="sub-card-body text-center d-flex flex-column">
								<span
									class="w-100 text-limiter two-liner card-title align-self-center xs-resp-font">
									<b>{{data.name}}</b>
								</span> <span class="w-100 text-limiter three-liner card-title align-self-center resp-font">{{data.description}}
								</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>