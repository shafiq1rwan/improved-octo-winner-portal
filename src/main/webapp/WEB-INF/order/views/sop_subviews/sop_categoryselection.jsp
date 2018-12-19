<body>
	<div id="category-selection-overlay" class="page-overlay" ng-click="hideFromView('categorySelection')">
		<div class="h-100 w-100 d-flex flex-row">
			<div class="w-100 h-50 d-flex flex-column align-self-center">
				<ul class="mw-100 align-self-center list-group md-resp-font scrollable-y" ng-click="$event.stopPropagation();">
					<li class="list-group-item text-truncate active"
						ng-repeat-start="data in menuList"
						ng-if="data == selectedCategory" ng-click="selectNewCategory(data)">{{data.name}}</li>
					<li class="list-group-item text-truncate" ng-repeat-end="data in categoryList"
						ng-if="data != selectedCategory" ng-click="selectNewCategory(data)">{{data.name}}</li>
				</ul>
			</div>
		</div>
	</div>
</body>