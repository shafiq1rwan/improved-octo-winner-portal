<body>
	<div id="loading-modal-overlay" class="page-overlay flex-container"
		ng-show="isShowLoading" style="background: rgba(0, 0, 0, 0.7);">
		<div class="flex-content" style="background: white;">
			<div class="flex-content" style="margin: 5vh 3vh 5vh 3vh;">
				<img src="${pageContext.request.contextPath}/images/gif/loader.gif"
					style="width: 15vh; height: 15vh;" />
				<h4>Processing Order...</h4>
			</div>
		</div>
	</div>
</body>