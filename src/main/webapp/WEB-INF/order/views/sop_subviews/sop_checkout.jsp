<body>
	<div id="item-checkout-overlay" ng-show="isItemCheck"
		class="page-overlay scrollable-content">
		<div id="header-content">
			<nav id="back-nav"
				class="navbar navbar-expand-lg navbar-light bg-light"></nav>
		</div>
		<div id="fake-header-content"></div>
		<div id="body-content" class="container item-display-container">

			<div class="text-center">
				<h3>{{OrderResultMessage}}</h3>
				<br>
				<button class="btn btn-info" ng-click="processOrderResponse()">{{OrderButtonMessage}}</button>
			</div>
		</div>
	</div>
</body>