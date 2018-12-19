<body class="main-bg">
	<div id="landing-overlay" class="page-overlay main-bg">
		<div
			class="header-content main-color d-flex flex-row justify-content-between">
			<div class="dropdown align-self-center ml-2">
				<button class="btn btn-secondary btn-locale md-resp-font"
					type="button" id="languageDropDown" data-toggle="dropdown"
					aria-haspopup="false" aria-expanded="false">{{currentLocale.shortName}}</button>
				<div class="dropdown-menu" aria-labelledby="languageDropDown">
					<a
						class="language-dropdown-item dropdown-item md-resp-font lang-selected"
						ng-repeat-start="localeObj in localeData" ng-if="localeObj == currentLocale"
						ng-click="changeLocale(localeObj)">{{localeObj.name}}
						({{localeObj.shortName}})</a> <a
						class="language-dropdown-item dropdown-item md-resp-font"
						ng-repeat-end="localeObj in localeData" ng-if="localeObj != currentLocale"
						ng-click="changeLocale(localeObj)">{{localeObj.name}}
						({{localeObj.shortName}})</a>
				</div>
			</div>
			<img class="align-self-center"
				src="${pageContext.request.contextPath}${applicationData.getMainLogoPath()}"
				alt="${applicationData.getAppName()}" height="60%">
			<div>
				<!-- Empty Content -->
			</div>
		</div>
		<div class="body-content">
			<div id="landing-main-content"
				class="d-flex flex-column justify-content-center">
				<img class="align-self-center"
					style="max-height: 30%; max-width: 30%;"
					src="${pageContext.request.contextPath}${applicationData.getLandingLogoPath()}">
				<div
					class="align-self-center d-flex flex-row justify-content-center">
					<img class="align-self-center xs-resp-img"
						src="${pageContext.request.contextPath}/assets/images/order/icon/store_icon.svg">
					<span class="align-self-center xs-resp-font">&nbsp;{{currentLanguageData.landing_storeName}}</span>
				</div>
				<span class="align-self-center md-resp-font font-weight-bold">{{storeName}}</span>
				<div
					class="align-self-center d-flex flex-row justify-content-center">
					<img class="align-self-center xs-resp-img"
						src="${pageContext.request.contextPath}/assets/images/order/icon/table_icon.svg">
					<span class="align-self-center xs-resp-font">&nbsp;{{currentLanguageData.landing_table}}</span>
				</div>
				<span class="align-self-center md-resp-font font-weight-bold">{{tableID}}</span>
				<div class="align-self-center">
					<button class="btn btn-primary btn-main md-resp-font" type="button"
						ng-click="switchToView('itemCategory')">{{currentLanguageData.landing_orderNow}}</button>
				</div>
			</div>
			<div id="landing-footer-content"
				class="d-flex flex-row justify-content-center">
				<span class="align-self-center xs-resp-font">Powered By&nbsp;</span>
				<img class="align-self-center xs-resp-img"
					src="${pageContext.request.contextPath}/assets/images/order/logo/mpay_poweredby_logo.png">
			</div>
		</div>
	</div>
</body>