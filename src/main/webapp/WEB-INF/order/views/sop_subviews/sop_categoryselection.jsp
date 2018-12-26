<body>
	<div id="category-selection-overlay" class="page-overlay"
		ng-click="hideFromView('categorySelection')">
		<div class="h-100 w-100 d-flex flex-column">
			<div class="h-100 mw-100 align-self-center d-flex flex-row">
				<div class="h-50 align-self-center d-flex flex-row">
					<ul class="mh-100 align-self-center list-group md-resp-font non-scrollable-x scrollable-y"
						ng-click="$event.stopPropagation();">
						<li class="list-group-item text-nowrap active"
							ng-repeat-start="data in menuList"
							ng-if="data == selectedCategory"
							ng-click="selectNewCategory(data)">{{data.name}}</li>
						<li class="list-group-item text-nowrap"
							ng-repeat-end="data in categoryList"
							ng-if="data != selectedCategory"
							ng-click="selectNewCategory(data)">{{data.name}}</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</body>