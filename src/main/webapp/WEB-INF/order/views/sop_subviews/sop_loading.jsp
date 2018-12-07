<body>
	<div id="loading-overlay" class="page-overlay">
		<div id="loading-main-content"
			class="d-flex flex-column justify-content-center">
			<span class="align-self-center md-resp-font">{{loadingText}}</span>
			<div id="loading-progress" class="progress align-self-center">
				<div class="progress-bar progress-bar-striped progress-bar-animated lg-resp-img"
					style="width: {{loadingPercentage}}%" role="progressbar"></div>
			</div>
			<span class="align-self-center md-resp-font"
				ng-hide="isLoadingFailed">{{loadingPercentage}}%</span>
			<div id="loading-refresh" class="align-self-center">
				<input type="image"
					src="${pageContext.request.contextPath}/assets/images/order/icon/refresh_icon.svg"
					alt="Refresh" ng-show="isLoadingFailed" ng-click="beginLoading()">
			</div>
		</div>
	</div>
</body>