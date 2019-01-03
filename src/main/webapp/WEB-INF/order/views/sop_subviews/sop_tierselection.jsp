<body>
	<div id="tier-selection-overlay" class="page-overlay"
		ng-click="hideFromView('tierSelection')">
		<div class="h-100 w-100 d-flex flex-row">
			<div class="align-self-center d-flex flex-column modal-container-90">
				<div
					class="align-self-center modal-sub-container-90  d-flex flex-column"
					ng-click="$event.stopPropagation();">
					<div class="pt-1 pb-1 pl-1 pr-1">
						<span class="xs-resp-font">{{currentLanguageData.tier_selection_selection}}&nbsp;{{selectedTier.tierNumber}}/{{itemComboTierList.length}}</span>
					</div>
					<div class="flex-fill d-flex flex-column d-flex">
						<div class="d-flex flex-column">
							<nav>
								<div class="nav nav-tabs flex-limiter" id="nav-tab"
									role="tablist">
									<a id="nav-tier-{{comboTier.tierNumber}}"
										class="nav-item nav-link sm-resp-font text-nowrap active"
										tabindex="0" ng-repeat-start="comboTier in itemComboTierList"
										ng-if="comboTier == selectedTier"
										ng-click="switchToView('tierSelection', comboTier)"
										ng-class="{'tier-completed': comboTier.isTierCompleted}">{{comboTier.name}}</a>
									<a id="nav-tier-{{comboTier.tierNumber}}"
										class="nav-item nav-link sm-resp-font text-nowrap"
										tabindex="0"
										ng-repeat-end="comboTier in selectedItemComboTier"
										ng-if="comboTier != selectedTier"
										ng-click="switchToView('tierSelection', comboTier)"
										ng-class="{'tier-completed': comboTier.isTierCompleted}">{{comboTier.name}}</a>
								</div>
							</nav>
							<div class="sm-resp-font pl-2">
								<span><b>{{selectedTier.quantity -
										selectedTier.selectedQuantity}}</b></span>&nbsp;{{currentLanguageData.tier_selection_itemRemaining}}
							</div>
							<div class="flex-fill scrollable-y">
								<ul class="list-group">
									<li class="list-group-item xs-resp-font pl-1 pr-1"
										ng-repeat="itemData in selectedTier.itemList">
										<div class="row ml-0 mr-0">
											<div
												class="col-6 col-sm-6 col-md-6 col-lg-8 col-xl-8 pl-0 pr-0 xs-resp-font text-limiter">{{itemData.name}}</div>
											<div
												class="col-3 col-sm-3 col-md-3 col-lg-2 col-xl-2 pl-0 pr-0 xs-resp-font text-limiter">+{{priceTag}}{{itemData.price}}</div>
											<div
												class="col-3 col-sm-3 col-md-3 col-lg-2 col-xl-2 pl-0 pr-0 text-right text-nowrap">
												<div class="row ml-0 mr-0">
													<div class="col-4 pl-0 pr-0 text-center">
														<img class="md-resp-img"
															ng-click="minusItemQuantity(itemData, selectedTier)"
															src="${pageContext.request.contextPath}/assets/images/order/icon/minus_icon.svg" />
													</div>
													<div class="col-4 pl-0 pr-0 text-center">
														<span class="sm-resp-font">{{itemData.selectedQuantity}}</span>
													</div>
													<div class="col-4 pl-0 pr-0 text-center">
														<img class="md-resp-img"
															ng-click="addItemQuantity(itemData, selectedTier)"
															src="${pageContext.request.contextPath}/assets/images/order/icon/plus_icon.svg" />
													</div>
												</div>
											</div>
										</div>
										<div class="row ml-0 mr-0"
											ng-repeat="modifierGroupList in itemData.modifierGroupData">
											<div class="col-12 pl-0 pr-0 text-truncate">
												<span class="xs-resp-font"><b>{{itemData.name}}&nbsp;{{($index+
														1)}}</b></span>
											</div>
											<div class="col-12 pl-0 pr-0"
												ng-repeat="modifierGroupData in modifierGroupList">
												<div class="row ml-0 mr-0">
													<div class="col-6 pl-0 pr-0 text-truncate">{{modifierGroupData.name}}</div>
													<div class="col-6 pl-0 pr-0">
														<select class="form-control xs-resp-font pt-0 pb-0"
															ng-options="modifierData.name for modifierData in modifierGroupData.modifierList"
															ng-model="modifierGroupData.selectedModifier"></select>
													</div>
												</div>
											</div>
										</div>
									</li>
								</ul>
							</div>
						</div>
					</div>
					<div class="d-flex flex-column pt-1 pb-1">
						<button
							class="btn btn-primary btn-main md-resp-font align-self-center"
							type="button" ng-click="hideFromView('tierSelection')">{{currentLanguageData.tier_selection_done}}</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>