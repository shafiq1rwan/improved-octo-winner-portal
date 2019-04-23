<!DOCTYPE html>
<html lang="en">
<style>
.form-section {
   padding-left:15px;
   border-left:2px solid #FF851B;
}
</style>
<body class="adminbody">
<div id="main">
    <div class="content-page">
		<!-- Start content -->
        <div class="content">        
			<div class="container-fluid">				
				<div class="row">
						<div class="col-xl-12">
								<div class="breadcrumb-holder">
	                                    <h1 class="main-title float-left">Setting</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item active">Setting</li>
	                                    </ol>
	                                    <div class="clearfix"></div>
	                            </div>
						</div>
				</div>
          	 	<!-- end row -->
				<div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">						
						<div class="card mb-3">
						<div class="card-header">
							BYOD Setting
						</div>
						<div class="card-body">
							<form id="storeForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
								<div class="form-section">
									<h6 class="pb-2 text-secondary">Application</h6>
									<div class="row">
										<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
											<div class="form-group">
												<label class="login-label">Name</label>
												<input class="form-control" id="appName" ng-model="byod.appName" type="text" required> 
											</div>
										</div>
								 	</div>
							 	</div>
							 	<hr>
								<div class="form-section">
									<h6 class="pb-2 text-secondary">Image</h6>
									<div class="row">
										<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										 	<div class="form-group">
										 		<img style="max-width:100%;max-height:100%;" ng-show="byod.mainLogo!=null" ng-src="{{byod.mainLogo}}" />	
										 	</div>									
											<div class="form-group">																							
												<label class="login-label">Main Logo</label> 
												<input id="mainLogo" type="file" accept="image/*" /> 
											</div>
										</div>
										<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										 	<div class="form-group">
										 		<img style="max-width:100%;max-height:100%;" ng-show="byod.shortcutLogo!=null" ng-src="{{byod.shortcutLogo}}" />	
										 	</div>									
											<div class="form-group">																							
												<label class="login-label">Shortcut Logo</label> 
												<input id="shortcutLogo" type="file" accept="image/*" /> 
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										 	<div class="form-group">
										 		<img style="max-width:100%;max-height:100%;" ng-show="byod.mainBackground!=null" ng-src="{{byod.mainBackground}}" />	
										 	</div>									
											<div class="form-group">																							
												<label class="login-label">Main Background Image</label> 
												<input id="mainBackground" type="file" accept="image/*" /> 
											</div>
										</div>
										<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
										 	<div class="form-group">
										 		<img style="max-width:100%;max-height:100%;" ng-show="byod.landingLogo!=null" ng-src="{{byod.landingLogo}}" />	
										 	</div>									
											<div class="form-group">																							
												<label class="login-label">Landing Logo Image</label> 
												<input id="landingLogo" type="file" accept="image/*" /> 
											</div>
										</div>
									</div>
								</div>
								<hr>	       									  
								<div class="form-section">
									<h6 class="pb-2 text-secondary">Layout</h6>
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Main Color</label>
												<input class="form-control"type="color" ng-model="byod.mainColor"  style="width:85%;">	
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Main Text Color</label>
												<input class="form-control"type="color" ng-model="byod.mainTextColor"  style="width:85%;">	
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Sub Color</label>
												<input class="form-control"type="color" ng-model="byod.subColor"  style="width:85%;">	
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Sub Text Color</label>
												<input class="form-control"type="color" ng-model="byod.subTextColor"  style="width:85%;">	
											</div>
										</div>
									</div>
									<h6 class="pb-2 text-secondary">Main Button</h6>
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Text Color</label>
												<input class="form-control"type="color" ng-model="byod.mbTextColor"  style="width:85%;">	
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Background Color</label>
												<input class="form-control"type="color" ng-model="byod.mbBackgroundColor"  style="width:85%;">	
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Background Hover Color</label>
												<input class="form-control"type="color" ng-model="byod.mbBackgroundHoverColor"  style="width:85%;">	
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Background Focus Color</label>
												<input class="form-control"type="color" ng-model="byod.mbBackgroundFocusColor"  style="width:85%;">	
											</div>
										</div>
									</div>
									<h6 class="pb-2 text-secondary">Locale Button</h6>
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Color</label>
												<input class="form-control"type="color" ng-model="byod.lbColor"  value="#FFFFFF" style="width:85%;">	
											</div>
										</div>
									</div>								
								</div>											 									
					       </form>
						</div>
						<div class="card-footer">
							<button class="btn btn-primary pull-right" type="submit" ng-click="promptSubmit()"> Submit</button>
						</div>																		
						</div><!-- end card-->						
					</div>														
				</div><!-- end card-->					
            </div>	
            <!-- END container-fluid -->			
		</div>
		<!-- END content -->	
    </div>
	<!-- END content-page -->
</div>
</body>
</html>