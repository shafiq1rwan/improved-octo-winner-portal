<body>
	<div id="item-cart-overlay" ng-show="isItemCart"
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
		<div id="body-content" class="container item-display-container">

			<div class="text-center" ng-hide="cartFinalList.length>=1">
				<h3>Please add at least 1 item into the Cart.</h3>
			</div>

			<div class="table-responsive" ng-show="cartFinalList.length>=1">
				<table class="table">
					<thead>
						<tr>
							<th>Name</th>
							<th>Qty</th>
							<th>Price</th>
						</tr>
					<thead>
					<tbody>
						<tr ng-repeat="item in cartFinalList">
							<td>{{item.name}}</td>
							<td>X {{item.quantity}}</td>
							<td>{{item.total | currency: "RM"}}</td>
						</tr>

					</tbody>
				</table>
			</div>

			<div class="text-center" ng-show="cartFinalList.length>=1">
				<button class="btn btn-info" ng-click="checkoutCartList(cartList)">Checkout
					- {{cartTotalPrice | currency: "RM"}}</button>
			</div>
		</div>
	</div>
</body>