<body>
	<div id="item-category-overlay" class="page-overlay">
		<div
			class="header-content sub-color d-flex flex-row justify-content-between">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('itemCategory')"
					class="empty-btn">
					<i class="fas fa-chevron-left main-text-color xl-resp-font"></i>
				</button>
			</div>
			<div
				class="text-truncate align-self-center main-text-color md-resp-font">
				<b>{{storeName}}</b>
			</div>
			<div class="align-self-center mr-2">
				<button type="button" ng-click="switchToView('itemCart')"
					class="empty-btn badge-btn">
					<i class="fas fa-shopping-cart main-text-color xl-resp-font"></i> <span
						class="badge badge-pill badge-danger xxs-resp-font">{{cart.length}}</span>
				</button>
			</div>
		</div>
		<div class="body-content scrollable-y">
			<div class="row card-container" style="margin: 0;">
				<div
					class="col-6 col-sm-6 col-md-4 col-lg-4 col-xl-3 sub-card-container"
					ng-repeat="data in menuList">
					<div class="card" ng-click="switchToView('itemList', data)">
						<img class="max-img-height card-img-top" ng-src="{{data.path}}"
							alt="{{data.name}}">
						<div class="card-body d-flex flex-row">
							<div
								class="sub-card-body text-center align-self-center d-flex flex-column">
								<p
									class="w-100 text-truncate card-title align-self-center xs-resp-font">
									<b>{{data.name}}</b>
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>