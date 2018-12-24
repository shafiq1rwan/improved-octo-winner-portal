<div class="modal fade" id="modal-dialog" tabindex="-1" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title sm-resp-font" ng-show="dialogData.title != ''">{{dialogData.title}}</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close" ng-show="isAllowCloseButton">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body xs-resp-font" ng-show="dialogData.message != ''">{{dialogData.message}}</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary btn-main sm-resp-font" ng-click="dialogData.button1.fn()" ng-show="dialogData.isButton1">{{dialogData.button1.name}}</button>
				<button type="button" class="btn btn-primary btn-main sm-resp-font" ng-click="dialogData.button2.fn()" ng-show="dialogData.isButton2">{{dialogData.button2.name}}</button>
			</div>
		</div>
	</div>
</div>