<body>
	<div id="edit-item-detail-overlay" class="page-overlay">
		<div class="header-content sub-color d-flex flex-row">
			<div class="align-self-center">
				<button type="button" ng-click="hideFromView('editItemDetail')"
					class="empty-btn">
					<i class="fas fa-chevron-left sub-text-color xl-resp-font"></i>
				</button>
			</div>
		</div>
		<div class="body-content scrollable-y">
			<div class="h-100 d-flex flex-column">
				<div class="w-100 flex-fill d-flex flex-column align-self-center">
					<div
						class="w-100 flex-fill card d-flex flex-column align-self-center scrollable-y">
						<div class="w-100 align-self-center text-center">
							<img class="max-img-height"
								src="${pageContext.request.contextPath}/{{imagePath}}{{selectedItem.path}}"
								alt="{{selectedItem.name}}">
						</div>
						<div class="w-100 align-self-center text-center">
							<span class="text-limiter lg-resp-font"><b>{{selectedItem.name}}</b></span>
						</div>
						<div class="w-100 align-self-center text-center">
							<span class="text-limiter three-liner xs-resp-font">{{selectedItem.description}}</span>
						</div>
						<hr class="ml-1 mr-1">
						<div class="flex-fill pb-2">
							<div class="row ml-0 mr-0">
								<div
									class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
									<div
										class="w-100 align-self-center text-limiter two-liner sm-resp-font">
										<b ng-show="selectedItem.type == 1">{{currentLanguageData.item_detail_comboPrice}}</b>
										<b ng-show="selectedItem.type == 0">{{currentLanguageData.item_detail_alacartePrice}}</b>
									</div>
								</div>
								<div
									class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
									<div
										class="w-100 align-self-center flex-fill d-flex flex-column">
										<div class="text-right text-nowrap sm-resp-font">{{priceTag}}{{selectedItem.price}}</div>
									</div>
								</div>
							</div>
							<div class="w-100" ng-show="selectedItem.type != '1'">
								<hr class="ml-3 mr-3">
								<div class="row ml-0 mr-0">
									<div
										class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-column pr-0">
										<div class="text-limiter two-liner sm-resp-font">
											<b>{{currentLanguageData.item_detail_quantity}}</b>
										</div>
									</div>
									<div
										class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
										<div
											class="w-100 align-self-center flex-fill d-flex flex-column">
											<span class="sm-resp-font text-right"> <i
												class="fas fa-minus-circle minus-color md-resp-font"
												ng-click="minusAlacarteQuantity(selectedItem)"></i>&nbsp;{{alacarteQuantity}}&nbsp;<i
												class="fas fa-plus-circle plus-color md-resp-font"
												ng-click="addAlacarteQuantity(selectedItem)"></i></span>
										</div>
									</div>
								</div>
								<hr class="ml-1 mr-1"
									ng-show="itemModifierList && itemModifierList.modifierGroupData.length > 0">
								<div class="row ml-0 mr-0">
									<div class="col-12"
										ng-repeat="modifierGroupList in itemModifierList.modifierGroupData">
										<div class="row ml-0 mr-0">
											<div class="col-12 pl-0 pr-0 text-truncate">
												<b>{{selectedItem.name}}&nbsp;{{$index + 1}}</b>
											</div>
										</div>
										<div class="row ml-0 mr-0"
											ng-repeat="modifierGroupData in modifierGroupList">
											<div class="col-6 pl-0 pr-0 text-truncate">{{modifierGroupData.name}}</div>
											<div class="col-6 pl-0 pr-0">
												<select class="form-control xs-resp-font pt-0 pb-0"
													ng-model="modifierGroupData.selectedModifier"
													ng-change="updateAlacarteModifierData(modifierGroupData)">
													<option
														ng-repeat="modifierData in modifierGroupData.modifierList"
														ng-value="modifierData">{{modifierData.name}} (+{{priceTag}}{{modifierData.price}})</option>
												</select>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="w-100" ng-show="selectedItem.type = '1'"
								ng-repeat="comboTier in itemComboTierList">
								<hr class="ml-3 mr-3">
								<div class="row ml-0 mr-0">
									<div
										class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-column pr-0">
										<div class="text-limiter two-liner sm-resp-font"
											ng-class="{'tier-completed': comboTier.isTierCompleted}">
											<b>{{comboTier.name}}</b>
										</div>
										<div class="text-limiter two-liner resp-font">
											<span>{{comboTier.itemString}}</span>
										</div>
									</div>
									<div
										class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
										<div
											class="w-100 align-self-center flex-fill d-flex flex-column">
											<div class="text-right text-nowrap sm-resp-font"
												ng-show="comboTier.isTierCompleted">
												<button class="btn btn-primary btn-main sm-resp-font"
													type="button"
													ng-click="switchToView('editTierSelection', comboTier)">{{priceTag}}{{comboTier.totalPrice}}</button>
											</div>
											<div class="text-right text-nowrap sm-resp-font"
												ng-show="!comboTier.isTierCompleted">
												<button class="btn btn-primary btn-main sm-resp-font"
													type="button"
													ng-click="switchToView('editTierSelection', comboTier)">{{currentLanguageData.item_detail_select}}&nbsp;&gt;</button>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="mt-2 mb-2">
			<div class="row ml-0 mr-0">
				<div
					class="col-8 col-sm-8 col-md-8 col-lg-9 col-xl-9 d-flex flex-row pr-0">
					<div
						class="w-100 align-self-center text-limiter two-liner sm-resp-font">
						<b>{{currentLanguageData.item_detail_totalPrice}}</b>
					</div>
				</div>
				<div
					class="col-4 col-sm-4 col-md-4 col-lg-3 col-xl-3 d-flex flex-row pl-0">
					<div class="w-100 align-self-center flex-fill d-flex flex-column">
						<div class="text-right text-nowrap sm-resp-font">{{priceTag}}{{totalItemPrice}}</div>
					</div>
				</div>
			</div>
			<div class="row ml-1 mr-0 d-flex flex-column"
				ng-show="isReadyForCart">
				<button
					class="btn btn-primary btn-main sm-resp-font align-self-center"
					type="button" ng-click="editToCart()">
					<i class="fal fa-cart-arrow-down"></i>&nbsp;{{currentLanguageData.edit_item_detail_editCart}}
				</button>
			</div>
		</div>
	</div>
</body>