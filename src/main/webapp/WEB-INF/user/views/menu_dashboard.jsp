<!DOCTYPE html>
<html lang="en">
<style>
.testimonial-group > .row {
  overflow-x: scroll;
  flex-wrap: nowrap;
}
.testimonial-group > .row > .col-xl-4 {
  display: inline-block;
  min-width: 50%;
}

.testimonial-group > .row::-webkit-scrollbar-track, .custom-scroll::-webkit-scrollbar-track
{
	-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
	border-radius: 6px;
	background-color: #F5F5F5;
}

.testimonial-group > .row::-webkit-scrollbar
{
	width: 8px;
	background-color: #F5F5F5;
}

.testimonial-group > .row::-webkit-scrollbar-thumb
{
	border-radius: 6px;
	-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
	background-color: #c6c6c6;
}

.custom-scroll::-webkit-scrollbar
{
	width: 0px;
	background-color: #F5F5F5;
}

.custom-scroll::-webkit-scrollbar-thumb
{
	border-radius: 0px;
	-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
	background-color: #b0b0b0;
}

.selected 
{
	border-width:2px !important;
}



/* The switch - the box around the slider */
.switch {
  position: relative;
  display: inline-block;
  width: 54px;
  height: 28px;
  float:right;
}

/* Hide default HTML checkbox */
.switch input {display:none;}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input.info:checked + .slider {
  background-color: #54e8a5;
}


input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(20px);
  -ms-transform: translateX(20px);
  transform: translateX(20px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}

.custom-badge-pos {
    position: relative;
    top: -0.9rem;
    right: -1.5rem;
    border-radius:40px;
    min-width:25px;
    font-size:0.7rem;
}


</style>
<body class="adminbody">

<div id="main">
    <div class="content-page">
		<!-- Start content -->
        <div class="content">      	     
			<div class="container-fluid d-flex flex-column" style="height:86vh;">				
				<div class="row" >
						<div class="col-xl-12">
								<div class="breadcrumb-holder">
	                                    <h1 class="main-title float-left">Menu Dashboard</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item">Home</li>
											<li class="breadcrumb-item active">Menu Dashboard</li>
	                                    </ol>
	                                    <div class="clearfix"></div>
	                            </div>
						</div>
				</div>
          	 	<!-- end row -->
				<div class="row">
                    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 col-xl-4" style="padding-bottom:13px;">
                    	<div class="h-100 custom-scroll" style="overflow-y:scroll;">						
							<div class="card mb-2">
								<div class="card-header d-flex flex-row justify-content-between p-2">
								<div><h5>Category</h5></div>
						  		<button type="button" class="btn btn-outline-secondary" style="border-radius:40px;"><i class="fa fa-plus-square bigfonts"></i> Category</button>
								</div>																							
							</div><!-- end card-->

							<div class="card mb-2 m-2" ng-repeat="item in data" ng-click="selectCategory($event)">
								<div class="d-flex flex-row">
					  			<div class="p-0">
					  			    <img  style="width:75px; height:75px;" src="${pageContext.request.contextPath}/assets/images/25f9a11e-phones-1.png" class="img-thumbnail rounded-circle border-0" />  				
					  			</div>
					  			<div class="pl-3 pt-2 pr-2 pb-2 w-75 border-left">
					  					<h5 class="text-primary">
					  						{{item.category}}
												<span class="badge badge-secondary pull-right custom-badge-pos">4</span>
										</h5>
					  					<h6 class="text-info">Photographer</h6>				  				
								</div>
								</div>
								<ul class="list-group list-group-flush">
									<li class="list-group-item p-1">
										<div class="btn-toolbar justify-content-between" role="toolbar" aria-label="Toolbar with button groups">
											<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize"><i class="fa fa-clock-o"></i> Period</button>
											<div class="input-group">
												<label class="switch ">
												<input type="checkbox" class="info">
												<span class="slider round"></span>
												</label>
											</div>
										</div>
									</li>
								</ul>
							</div>																																
						</div>					
					</div>
					<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
						<div class="container-fluid d-flex flex-column testimonial-group" style="height:100%;">				
							<div class="row h-100" >
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
                    	<div class="h-100 custom-scroll" id="itemSet" style="overflow-y:scroll;">
                    		<div class="card mb-2">
								<div class="card-header d-flex flex-row justify-content-between p-2">						
						  			<div><h5>Meal</h5></div>
						  			<button type="button" class="btn btn-outline-secondary" style="border-radius:40px;"><i class="fa fa-plus-square bigfonts"></i> Item Set</button>							
								</div>																							
							</div><!-- end card-->						
							<div class="card mb-2 m-2" ng-repeat="item in data" ng-click="selectCategory($event)">
								<div class="d-flex flex-row">
					  			<div class="p-0">
					  			    <img  style="width:75px; height:75px;" src="${pageContext.request.contextPath}/assets/images/25f9a11e-phones-1.png" class="img-thumbnail rounded-circle border-0" />  				
					  			</div>
					  			<div class="pl-3 pt-2 pr-2 pb-2 w-75 border-left">
					  					<h5 class="text-primary">
					  						{{item.category}}
												<span class="badge badge-primary pull-right custom-badge-pos custom-fontsize">4</span>
										</h5>
					  					<h6 class="text-info">Photographer</h6>				  				
								</div>
								</div>
								<ul class="list-group list-group-flush">
									<li class="list-group-item p-1">
										<div class="btn-toolbar justify-content-between" role="toolbar" aria-label="Toolbar with button groups">
											<button type="button" class="btn btn-success badge-pill p-1 custom-fontsize">$ 20.00</button>															
					  						<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize"><i class="fa fa-clock-o"></i> Period</button>
					  						<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize">Modifier</button>
					  						<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize"><i class="fa fa-edit"></i> Edit</button>
					  					
											<div class="input-group">
												<label class="switch ">
												<input type="checkbox" class="info">
												<span class="slider round"></span>
												</label>
											</div>
										</div>
									</li>
								</ul>
							</div>												
						</div>					
					</div>
					<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 col-xl-4" ng-repeat="items in itemGroup">
                    	<div class="h-100 custom-scroll border border-danger selected" style="overflow-y:scroll;">
                    		<div class="card mb-2">
								<div class="card-header text-center">
						  			<h5>{{items.name}}</h5>
								</div>																							
							</div><!-- end card-->						
							<div class="card mb-2 m-2" ng-repeat="item in items.item">
								<div class="d-flex flex-row">
					  			<div class="p-0">
					  			    <img  style="width:75px; height:75px;" src="${pageContext.request.contextPath}/assets/images/25f9a11e-phones-1.png" class="img-thumbnail rounded-circle border-0" />  				
					  			</div>
					  			<div class="pl-3 pt-2 pr-2 pb-2 w-75 border-left">
					  					<h5 class="text-primary">
					  						{{item.name}}
						  					
										</h5>
					  					<h6 class="text-info">Photographer</h6>				  				
								</div>
								</div>
								<ul class="list-group list-group-flush">
									<li class="list-group-item p-1">
										<div class="btn-toolbar justify-content-between" role="toolbar" aria-label="Toolbar with button groups">
											<button type="button" class="btn btn-success badge-pill p-1 custom-fontsize">$ 20.00</button>															
					  						<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize"><i class="fa fa-clock-o"></i> Period</button>
					  						<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize">Modifier</button>
					  						<button type="button" class="btn btn-outline-secondary border-0 p-1 custom-fontsize"><i class="fa fa-edit"></i> Edit</button>
					  					
											<div class="input-group">
												<label class="switch ">
												<input type="checkbox" class="info">
												<span class="slider round"></span>
												</label>
											</div>
										</div>
									</li>
								</ul>																						
							</div><!-- end card-->													
						</div>					
					</div>					
								
															
							</div>					
						</div>
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