<style>
.shadowBox {
  box-shadow: 1px 1px 3px grey;
}
</style>
<body>
	<div id="item-category-overlay" class="page-overlay">
		<div
			class="header-content sub-color d-flex flex-row justify-content-between">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('itemCategory')"
					class="empty-btn">
					<i class="fas fa-chevron-left sub-text-color xl-resp-font"></i>
				</button>
			</div>
			<div
				class="text-truncate align-self-center sub-text-color md-resp-font">
				<b>{{storeName}}</b>
			</div>
			<div class="align-self-center mr-2">
				<button type="button" ng-click="switchToView('itemCart')"
					class="empty-btn badge-btn">
					<i class="fal fa-shopping-cart sub-text-color xl-resp-font"></i> <span
						class="badge badge-pill badge-danger xxs-resp-font">{{cart.length}}</span>
				</button>
			</div>
		</div>
		<div class="body-content scrollable-y">
			<div class="row card-container" style="margin: 0;">
				<div
					class="col-6 col-sm-6 col-md-4 col-lg-4 col-xl-3 sub-card-container"
					ng-repeat="data in menuList">
					<div class="card shadowBox" ng-click="switchToView('itemList', data)">
						<img class="max-img-height card-img-top" ng-src="${pageContext.request.contextPath}{{imagePath}}{{data.path}}"
							alt="{{data.name}}" style="height: 160px">
						<div class="card-body d-flex flex-row">
							<div
								class="sub-card-body text-center align-self-center d-flex flex-column">
								<p
									class="w-100 card-title align-self-center xs-resp-font">
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