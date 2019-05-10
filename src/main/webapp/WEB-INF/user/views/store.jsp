<!DOCTYPE html>
<html lang="en">
<style>
.ng-hide.ng-hide-animate{
     display: none !important;
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
	                                    <h1 class="main-title float-left">Store List</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item active">Store</li>
	                                    </ol>
	                                    <div class="clearfix"></div>
	                            </div>
						</div>
				</div>
          	 	<!-- end row -->
				<div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">						
						<div class="card mb-3">
							<div class="card-header d-flex flex-row justify-content-between">
								<h3>Store List</h3>								
								<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#storeModal" ng-click="modalType('create')">
									<span class="btn-label"><i class="fa fa-plus"></i></span> Create Store
								</button>									
							</div>						
							<div class="card-body">							
								<div class="table-responsive">
									<table id="store_dtable" class="table table-bordered table-hover display nowrap" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Backend ID</th>
												<th>Name</th>
												<!-- <th>Image</th> -->
												<th>Country</th>
												<th>Publish Status</th>
												<th>Action</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>								
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
	
	
	<!-- Store Modal -->
	<div class="modal fade" id="storeModal" tabindex="-1" role="dialog" aria-labelledby="storeModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="storeForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'" class="modal-title">Create Store</h5>
		        <h5 ng-show="action=='update'" class="modal-title">Edit Store</h5>
		        <button type="button" class="close" data-dismiss="modal" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body" id="accordion">
				  <div class="card">
				    <div class="card-header p-2" id="headingOne">
				      <h5 class="mb-0">
				        <button class="btn btn-link" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
				        	Store Information
				        </button>
				      </h5>
				    </div>
				
				    <div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#accordion">
				      <div class="card-body">
						<div class="row">
							<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" id="storeName" name="storeName" placeholder="Name" ng-model="store.name" type="text" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Currency</label>
									<input class="form-control" name="storeCurrency" placeholder="MYR" ng-model="store.currency" type="text" required> 
								</div>
							</div>
						</div>	
						<div class="row">
							<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
								<div class="form-group">
									<label class="login-label">Address</label>
									<input class="form-control" name="storeAddress" placeholder="Address" ng-model="store.address" type="text" required> 
								</div>
							</div>
							<!-- <div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Table Count</label>
									<input class="form-control" name="storeTableCount" placeholder="50" ng-model="store.tableCount" type="number" required> 
								</div>
							</div> -->
						</div>
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Longitude</label>
									<input class="form-control" name="storeLongitude" ng-blur="setPrecision(event, store.longitude)" ng-click="event = $event" placeholder="Longitude" ng-model="store.longitude" type="number" step=".000001" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Latitude</label>
									<input class="form-control" name="storeLatitude" ng-blur="setPrecision(event, store.latitude)" ng-click="event = $event" placeholder="Latitude" ng-model="store.latitude" type="number" step=".000001" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Country</label>
									<input class="form-control" name="storeCountry" placeholder="Country" ng-model="store.country" type="text" required> 
								</div>
							</div>						
						</div>
						<div class="row">
					        <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
					            <div class="form-group">
					            	<label class="login-label">Operating Start Time</label>
					                <div class="input-group date" id="operatingStartTime" data-target-input="nearest">
					                    <input type="text" class="form-control datetimepicker-input" data-target="#operatingStartTime"/>
					                    <div class="input-group-append" data-target="#operatingStartTime" data-toggle="datetimepicker">
					                        <div class="input-group-text"><i class="far fa-clock"></i></div>
					                    </div>
					                </div>
					            </div>
					        </div>
					        <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
					            <div class="form-group">
					            	<label class="login-label">Operating End Time</label>
					                <div class="input-group date" id="operatingEndTime" data-target-input="nearest">
					                    <input type="text" class="form-control datetimepicker-input" data-target="#operatingEndTime"/>
					                    <div class="input-group-append" data-target="#operatingEndTime" data-toggle="datetimepicker">
					                        <div class="input-group-text"><i class="far fa-clock"></i></div>
					                    </div>
					                </div>
					            </div>
					        </div>
					    </div>
					    <div class="row">
					    	<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
								<div class="form-group">
									<label class="login-label">Store Type</label>
									<select class="form-control" ng-model="store.storeType" ng-options="x.store_type_name for x in storeType track by x.id" required>
										<option value="" disabled>Please choose a store type</option>
									</select>
								</div>																	
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">	
								<div class="form-group">
									<label class="login-label">Tax Type</label>
									<select class="form-control" ng-model="store.storeTaxType" ng-options="x.store_tax_type_name for x in storeTaxType track by x.id" required>
										<option value="" disabled>Please choose a tax type</option>
									</select>
								</div>	
							</div>					
						</div>					
						<div class="row">
							 <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							 	<div class="form-group">
							 		<img style="max-width:100%;max-height:100%;" ng-show="store.imagePath!=null" ng-src="{{store.imagePath}}" />	
							 	</div>									
								<div class="form-group">																							
									<label class="login-label">Image</label> 
									<input id="storeImage" type="file" accept="image/*" /> 
								</div>
							</div>
						</div>
				      </div>
				    </div>
				  </div>
				  <div class="card">
				    <div class="card-header p-2" id="headingTwo">
				      <h5 class="mb-0">
				        <button class="btn btn-link collapsed" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
				        	Contact Person Information
				        </button>
				      </h5>
				    </div>
				    <div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordion">
				      <div class="card-body">
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" name="storeContactPerson" placeholder="Name" ng-model="store.contactPerson" type="text" required> 
								</div>
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Mobile Number</label>
									<input class="form-control" name="storeMobileNumber" placeholder="Mobile Number" ng-model="store.mobileNumber" type="text" required> 
								</div>
							</div>				
						</div>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Email</label>
									<input class="form-control" name="storeEmail" placeholder="Email" ng-model="store.email" type="email" required> 
								</div>
							</div>			
						</div>
				      </div>
				    </div>
				  </div>
				  <div class="card">
				    <div class="card-header p-2" id="headingThree">
				      <h5 class="mb-0">
				        <button class="btn btn-link collapsed" data-toggle="collapse" data-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
				        	POS Setting
				        </button>
				      </h5>
				    </div>
				    <div id="collapseThree" class="collapse" aria-labelledby="headingThree" data-parent="#accordion">
				      <div class="card-body">
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<div class="form-check">
									  <input class="form-check-input" type="radio" ng-model="store.ecpos" id="exampleRadios1" ng-value=true checked>
									  <label class="form-check-label" for="exampleRadios1">
									    ECPOS
									  </label>
									</div>									
									<div class="form-check">
									  <input class="form-check-input" type="radio" ng-model="store.ecpos" id="exampleRadios2" ng-value=false checked>
									  <label class="form-check-label" for="exampleRadios2">
									    Other POS
									  </label>
									</div>
								</div>
							</div>
							<div ng-show="store.ecpos" class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">			
								<div class="form-group">
									<label class="login-label">ECPOS URL</label>
									<input class="form-control" name="ecposUrl" placeholder="IP Address" ng-model="store.ecposUrl" type="text" required> 
								</div>																	
							</div>					
						</div>
				      </div>
				    </div>
				</div>
				<div class="card">
				    <div class="card-header p-2" id="headingFour">
				      <h5 class="mb-0">
				        <button class="btn btn-link collapsed" data-toggle="collapse" data-target="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
				        	Payment Delay Setting
				        </button>
				      </h5>
				    </div>
				    <div id="collapseFour" class="collapse" aria-labelledby="headingFour" data-parent="#accordion">
				      <div class="card-body">
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
								<div class="form-group">
									<label class="login-label">BYOD</label>
									<select class="form-control" ng-model="store.byodPaymentDelayType" ng-options="x.payment_delay_name for x in paymentDelayType track by x.id" required>
										<option value="" disabled>Please choose a payment type</option>
									</select>
								</div>																	
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
								<div class="form-group">
									<label class="login-label">KIOSK</label>
									<select class="form-control" ng-model="store.kioskPaymentDelayType" ng-options="x.payment_delay_name for x in paymentDelayType track by x.id" required>
										<option value="" disabled>Please choose a payment type</option>
									</select>
								</div>																	
							</div>						
						</div>
				      </div>
				    </div>
				</div>		
		      </div>
		      <!-- <div class="modal-body">		       									  
					<div class="form-section">
						<h6 class="pb-2 text-secondary">Store Information</h6>
						<div class="row">
							<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" name="storeName" placeholder="Name" ng-model="store.name" type="text" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Currency</label>
									<input class="form-control" name="storeCurrency" placeholder="MYR" ng-model="store.currency" type="text" required> 
								</div>
							</div>
						</div>	
						<div class="row">
							<div class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">
								<div class="form-group">
									<label class="login-label">Address</label>
									<input class="form-control" name="storeAddress" placeholder="Address" ng-model="store.address" type="text" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Table Count</label>
									<input class="form-control" name="storeTableCount" placeholder="50" ng-model="store.tableCount" type="number" required> 
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Longitude</label>
									<input class="form-control" name="storeLongitude" ng-blur="setPrecision(event, store.longitude)" ng-click="event = $event" placeholder="Longitude" ng-model="store.longitude" type="number" step=".000001" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Latitude</label>
									<input class="form-control" name="storeLatitude" ng-blur="setPrecision(event, store.latitude)" ng-click="event = $event" placeholder="Latitude" ng-model="store.latitude" type="number" step=".000001" required> 
								</div>
							</div>
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<label class="login-label">Country</label>
									<input class="form-control" name="storeCountry" placeholder="Country" ng-model="store.country" type="text" required> 
								</div>
							</div>						
						</div>
						<div class="row">
					        <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
					            <div class="form-group">
					            	<label class="login-label">Operating Start Time</label>
					                <div class="input-group date" id="operatingStartTime" data-target-input="nearest">
					                    <input type="text" class="form-control datetimepicker-input" data-target="#operatingStartTime"/>
					                    <div class="input-group-append" data-target="#operatingStartTime" data-toggle="datetimepicker">
					                        <div class="input-group-text"><i class="far fa-clock"></i></div>
					                    </div>
					                </div>
					            </div>
					        </div>
					        <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
					            <div class="form-group">
					            	<label class="login-label">Operating End Time</label>
					                <div class="input-group date" id="operatingEndTime" data-target-input="nearest">
					                    <input type="text" class="form-control datetimepicker-input" data-target="#operatingEndTime"/>
					                    <div class="input-group-append" data-target="#operatingEndTime" data-toggle="datetimepicker">
					                        <div class="input-group-text"><i class="far fa-clock"></i></div>
					                    </div>
					                </div>
					            </div>
					        </div>
					    </div>
					    <div class="row">
					    	<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
								<div class="form-group">
									<label class="login-label">Store Type</label>
									<select class="form-control" ng-model="store.storeType" ng-options="x.store_type_name for x in storeType track by x.id" required>
										<option value="" disabled>Please choose a store type</option>
									</select>
								</div>																	
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">	
								<div class="form-group">
									<label class="login-label">Tax Type</label>
									<select class="form-control" ng-model="store.storeTaxType" ng-options="x.store_tax_type_name for x in storeTaxType track by x.id" required>
										<option value="" disabled>Please choose a tax type</option>
									</select>
								</div>	
							</div>					
						</div>					
						<div class="row">
							 <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							 	<div class="form-group">
							 		<img style="max-width:100%;max-height:100%;" ng-show="store.imagePath!=null" ng-src="{{store.imagePath}}" />	
							 	</div>									
								<div class="form-group">																							
									<label class="login-label">Image</label> 
									<input id="storeImage" type="file" accept="image/*" /> 
								</div>
							</div>
						</div>								
					</div>
					<hr>
					<div class="form-section">
						<h6 class="pb-2 text-secondary">Contact Person Information</h6>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Name</label>
									<input class="form-control" name="storeContactPerson" placeholder="Name" ng-model="store.contactPerson" type="text" required> 
								</div>
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Mobile Number</label>
									<input class="form-control" name="storeMobileNumber" placeholder="Mobile Number" ng-model="store.mobileNumber" type="text" required> 
								</div>
							</div>				
						</div>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
								<div class="form-group">
									<label class="login-label">Email</label>
									<input class="form-control" name="storeEmail" placeholder="Email" ng-model="store.email" type="email" required> 
								</div>
							</div>			
						</div>
					</div>	
					<hr>
					<div class="form-section">
						<h6 class="pb-2 text-secondary">POS Setting</h6>
						<div class="row">
							<div class="col-4 col-sm-4 col-md-4 col-lg-4 col-xl-4">
								<div class="form-group">
									<div class="form-check">
									  <input class="form-check-input" type="radio" ng-model="store.ecpos" id="exampleRadios1" ng-value=true checked>
									  <label class="form-check-label" for="exampleRadios1">
									    ECPOS
									  </label>
									</div>									
									<div class="form-check">
									  <input class="form-check-input" type="radio" ng-model="store.ecpos" id="exampleRadios2" ng-value=false checked>
									  <label class="form-check-label" for="exampleRadios2">
									    Other POS
									  </label>
									</div>
								</div>
							</div>
							<div ng-show="store.ecpos" class="col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8">			
								<div class="form-group">
									<label class="login-label">ECPOS URL</label>
									<input class="form-control" name="ecposUrl" placeholder="IP Address" ng-model="store.ecposUrl" type="text" required> 
								</div>																	
							</div>					
						</div>
					</div>
					<hr>
					<div class="form-section">
						<h6 class="pb-2 text-secondary">Payment Delay Setting</h6>
						<div class="row">
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
								<div class="form-group">
									<label class="login-label">BYOD</label>
									<select class="form-control" ng-model="store.byodPaymentDelayType" ng-options="x.payment_delay_name for x in paymentDelayType track by x.id" required>
										<option value="" disabled>Please choose a payment type</option>
									</select>
								</div>																	
							</div>
							<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">			
								<div class="form-group">
									<label class="login-label">KIOSK</label>
									<select class="form-control" ng-model="store.kioskPaymentDelayType" ng-options="x.payment_delay_name for x in paymentDelayType track by x.id" required>
										<option value="" disabled>Please choose a payment type</option>
									</select>
								</div>																	
							</div>						
						</div>
					</div>					 									
		      </div> -->
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitStore()"> Submit</button>
		      	<button class="btn btn-success" ng-show="action=='create'" type="submit" ng-click="submitStore(1)"> Submit & Publish Store</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitStore()"> Update</button>
		      	<button class="btn btn-success" ng-show="action=='update' && !store.isPublish" type="submit" ng-click="submitStore(1)"> Update & Publish Store</button>
		      	<button class="btn btn-success" ng-show="action=='update' && store.isPublish" type="submit" ng-click="submitStore(0)"> Update & Unpublish Store</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
</div>
</body>
</html>