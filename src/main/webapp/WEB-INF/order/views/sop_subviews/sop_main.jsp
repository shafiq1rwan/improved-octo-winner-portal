<body>
	<div id="main-overlay" class="page-overlay scrollable-content"
		ng-show="isMain">
		<div id="header-content">
			<nav id="back-nav"
				class="navbar navbar-expand-lg navbar-light bg-light">
				<ul class="navbar-nav mr-auto">
					<li class="nav-item">
						<button type="button" ng-click="backToStorePage()"
							class="btn btn-default navbar-btn navbar-custom borderless">
							<span class="back-icon"></span>Back
						</button>
					</li>
				</ul>
				<button type="button" ng-click="goToCartPage()"
					class="btn btn-default navbar-btn navbar-custom borderless pull-right">
					<span id="itemCount" ng-show="noOfCartListItem >= 1"></span> <span
						class="cart-icon"></span>
				</button>
			</nav>
		</div>
		<div id="fake-header-content"></div>
		<div id="body-content" class="container item-display-container"
			style="margin-top: 25px;">
			<div class="row">
				<div class="col-md-4 col-sm-4 col-4" style="padding: 4px;"
					ng-repeat="category in categoryList">
					<a class="card category-card" ng-click="displayItems(category.id)">
						<div class="category-image" style="background: #fafafa;">
							<img ng-src="{{category.path}}" alt="{{category.name}}">
						</div>
						<div class="category-name">
							<p class="item-title">{{category.name}}</p>
						</div>
					</a>
				</div>
			</div>
		</div>
	</div>
</body>