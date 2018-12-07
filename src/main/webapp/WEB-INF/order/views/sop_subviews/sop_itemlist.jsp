<body>
	<div id="item-list-overlay" ng-show="isItemList"
		class="page-overlay scrollable-content">
		<div id="header-content">
			<nav id="back-nav"
				class="navbar navbar-expand-lg navbar-light bg-light">
				<button type="button" ng-click="backToCategoryPage()"
					class="btn btn-default navbar-btn navbar-custom borderless">
					<span class="back-icon"></span>Back
				</button>
			</nav>
		</div>
		<div id="fake-header-content"></div>
		<div id="body-content" class="container item-display-container"
			style="margin-top: 25px;">

			<div class="row">
				<div class="col-md-6 col-sm-6 col-6" style="padding: 0; margin: 0;"
					ng-repeat="item in itemList">
					<a class="card" ng-click="displayItemDetail(item.id)">
						<div class="item-image" style="background: white">
							<img ng-src="{{item.path}}" alt="{{item.name}}" />
						</div>
						<div class="item-info">
							<div class="item-price">
								<span>{{item.price | currency: "RM"}}</span>
							</div>
							<div class="item-title">{{item.name}}</div>
						</div>
					</a>
				</div>
			</div>

		</div>
	</div>
</body>