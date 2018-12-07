<body>
	<div id="item-detail-overlay" ng-show="isItemDetail"
		class="page-overlay scrollable-content">
		<div id="header-content">
			<nav id="back-nav"
				class="navbar navbar-expand-lg navbar-light bg-light">
				<button type="button" ng-click="backToItemListPage()"
					class="btn btn-default navbar-btn navbar-custom borderless">
					<span class="back-icon"></span>Back
				</button>
			</nav>
		</div>
		<div id="fake-header-content"></div>
		<div id="body-content" class="container item-display-container">

			<div class="row">
				<div class="col-md-12 col-sm-12 col-12"
					style="padding-top: 30px; padding-bottom: 30px;">
					<div class="item-background-image">
						<img ng-src="{{itemDetail[0].path}}" alt="{{itemDetail[0].name}}" />
					</div>

					<div class="col-md-12 col-sm-12 col-12 main-content">
						<div class="item-info-box">
							<span class="title-text">{{itemDetail[0].name}}</span>
						</div>
						<div class="content" ng-show="itemDetail[0].additionalItem">
							<div class="row">
								<div class="col-md-6 col-sm-12 col-12">
									<ul>
										<li ng-repeat="additionalItem in itemDetail[0].additionalItem">
											{{additionalItem}}</li>
									</ul>

								</div>
							</div>
						</div>

						<div class="text-center">
							<h5>{{itemDetail[0].price | currency: "RM"}} / Item</h5>
							<h6
								ng-class="{'hidden': itemQuantity === 0, 'unhidden': itemQuantity >0}">Total:
								{{itemTotal | currency: "RM"}}</h6>
							<div class="quantity-counter">
								<button class="btn btn-danger"
									ng-click="decreaseItemCount(itemDetail[0].price)"
									style="display: inline-block;">-</button>
								<p
									style="display: inline-block; margin-left: 8px; margin-right: 8px;">{{itemQuantity}}</p>
								<button class="btn btn-info"
									ng-click="increaseItemCount(itemDetail[0].price)"
									style="display: inline-block;">+</button>
							</div>

							<button class="btn add-cart" ng-if="isAddOrModifiedCart"
								ng-click="addItemIntoCart(itemDetail[0].id, itemQuantity)">Update
								Cart</button>
							<button class="btn add-cart" ng-if="!isAddOrModifiedCart"
								ng-click="addItemIntoCart(itemDetail[0].id, itemQuantity)">Add
								to Cart</button>
							<br>
							<button class="btn remove-cart" ng-if="isAddOrModifiedCart"
								ng-click="removeCartItem(itemDetail[0].id)"
								style="margin-top: 5px;">Remove</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>