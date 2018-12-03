<!DOCTYPE html>
<html lang="en">
<style>
.btn-primary-outline {
  background-color: transparent;
  border-color: #ccc;
  border: 0;
}

.list-group-item {
  padding: .5rem 1.25rem;
}

.subItem{
	padding: .5rem 1.25rem;
} 

.innerItem{
	padding: .5rem 1.25rem;
} 

.btn {
  margin-left:7px;
}

/* .addMenu{
   display:flex;
   margin-right:10px;
} */

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
	                                    <h1 class="main-title float-left">Menu</h1>	                                    	                      
	                                    <ol class="breadcrumb float-right">                            	
											<li class="breadcrumb-item">Home</li>
											<li class="breadcrumb-item active">Menu</li>
	                                    </ol>
	                                    <!-- <div class="addMenu float-right"><button type="button" ng-click="addMenu()" class="btn btn-outline-primary btn-sm pull-right"><i class="fa fa-plus"></i> Add Menu</button>
										</div>-->
	                                    <div class="clearfix"></div>
	                            </div>
						</div>
				</div>
          	 	<!-- end row -->
				<div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">						
						<div class="card mb-4">
							<div class="card-header">
								<h3 style="display:inline-block">MasterMenu</h3>
								<button type="button" class="btn btn-primary pull-right"><i class="fa fa-edit"></i> Items</button>
								<button type="button" class="btn btn-primary pull-right"><i class="fa fa-edit"></i> Item Groups</button>
								<button type="button" class="btn btn-primary pull-right"><i class="fa fa-edit"></i> Category</button>
								<div class="btn-group pull-right" role="group" aria-label="First group">
									<button type="button" class="btn btn-primary-outline"><i class="fa fa-plus-square"></i> SubMenu</button>	
								    <button type="button" class="btn btn-primary-outline"><i class="fa fa-edit"></i> Edit Stores</button>
								    <button type="button" class="btn btn-primary-outline"><i class="fa fa-cog"></i> Settings</button>						    				
								</div>							
							</div>
							<div ng-repeat="child in children">							
								<div class="card-header d-flex justify-content-between align-items-center subItem">
									<span><b>SubMenu {{child.name}}</b></span>
									<div class="btn-group" role="group" aria-label="First group">
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-edit"></i> Edit SubMenu</button>	
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-edit"></i> Edit Stores</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-cog"></i> Settings</button>						    				
									</div>
								</div>
								<div ng-repeat="grandChild in child.grandChildren" class="card-body d-flex justify-content-between align-items-center innerItem">
									<span><i class="fa fa-building-o"></i> {{grandChild.name}}</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</div>
							</div>																								
							
							<!-- Approach 2 -->
							
							<!-- <ul class="list-group">
								<li class="list-group-item"><h5 class="card-title">SubMenu 1</h5></li>
							    <li class="list-group-item d-flex justify-content-between align-items-center">
							    	<span><i class="fa fa-building-o"></i> KFC (Thai)</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</li>
							    <li class="list-group-item d-flex justify-content-between align-items-center">
							    	<span><i class="fa fa-building-o"></i> KFC (MY)</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</li>
							    <li class="list-group-item d-flex justify-content-between align-items-center">
							    	<span><i class="fa fa-building-o"></i> KFC (SG)</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</li>
						  	</ul>
						  	<ul class="list-group">
						  		<li class="list-group-item"><h5 class="card-title">SubMenu 2</h5></li>				  		
							    <li class="list-group-item d-flex justify-content-between align-items-center">
							    	<span><i class="fa fa-building-o"></i> KFC (Thai)</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</li>
							    <li class="list-group-item d-flex justify-content-between align-items-center">
							    	<span><i class="fa fa-building-o"></i> KFC (MY)</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</li>
							    <li class="list-group-item d-flex justify-content-between align-items-center">
							    	<span><i class="fa fa-building-o"></i> KFC (SG)</span>
								    <div class="btn-group" role="group" aria-label="First group">
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-signal"></i> PING POS</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-mobile"></i> MOBILE</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-tablet"></i> KIOSK</button>
									    <button type="button" class="btn btn-primary-outline"><i class="fa fa-clock-o"></i> PERIOD</button>
										<button type="button" class="btn btn-primary-outline"><i class="fa fa-database"></i> INVENTORY</button>
									</div>
								</li>
							</ul>		  -->										
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