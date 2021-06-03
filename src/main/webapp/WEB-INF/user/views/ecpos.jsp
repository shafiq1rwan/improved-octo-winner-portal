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
	                                    <h1 class="main-title float-left"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store"><i class="fa fa-chevron-left"></i></a> &nbsp;ECPOS</h1>
	                                    <ol class="breadcrumb float-right">
											<li class="breadcrumb-item"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store">Store</a></li>
											<li class="breadcrumb-item active">ECPOS</li>
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
								<h3>ECPOS Info</h3>
								<div class="btn-group">					
									<button type="button" class="btn pull-right btn-success ml-3" data-toggle="modal" data-target="#staffListModal">
										<span class="btn-label"><i class="fa fa-edit"></i></span> Manage Staff
									</button>
									<button type="button" id="manageTableBtn" class="btn pull-right btn-info ml-3" data-toggle="modal" data-target="#tableListModal">
										<span class="btn-label"><i class="fa fa-edit"></i></span> Manage Table
									</button>
									<button type="button" id="manageRoomBtn" class="btn pull-right btn-info ml-3" data-toggle="modal" data-target="#roomListModal">
										<span class="btn-label"><i class="fa fa-edit"></i></span> Manage Room
									</button>
									<button type="button" class="btn pull-right btn-primary ml-3" ng-click="ecposModalType('create')">
										<span class="btn-label"><i class="fa fa-plus"></i></span> Add ECPOS
									</button>			
								</div>									
							</div>
							<div class="card-body">													
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<h6><label>Brand ID : {{store.brand_id}}</label></h6>
											<h6><label>Store ID : {{store.id}}</label></h6>
											<h6><label>Backend ID : {{store.backend_id}}</label></h6>
											<h6><label>Store Name : {{store.name}}</label></h6>
											<h6><label>Number of ECPOS : {{store.ecpos_count}}</label></h6>
										</div>
									</div>									
								</div>
							</div>																										
						</div><!-- end card-->
						<div class="card mb-3 shadow" ng-repeat="item in store.ecpos">				
							<div class="card-body">		
								<div class="form-section" id="test">
									<div class="btn btn-success pull-right" ng-click="ecposModalType('update', $index)"><span class="btn-label"><i class="fa fa-edit"></i></span> Edit</div>
									<h5>ECPOS # {{store.ecpos.length - $index}} <b ng-show="item.device_role_lookup_id==1">(Master)</b></h5>
									<br>
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation ID</label>
												<input class="form-control" ng-model="item.activation_id" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Activation Key</label>
												<input class="form-control" ng-model="item.activation_key"  type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Date Created</label>
												<input class="form-control" ng-model="item.created_date" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Status</label>
												<input class="form-control" ng-model="item.status" type="text" disabled>
											</div>
										</div>										
									</div>
									
									<div class="row">
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">MAC Address</label>
												<input class="form-control" ng-model="item.mac_address" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Device Name</label>
												<input class="form-control" ng-model="item.device_name" type="text" disabled>
											</div>
										</div>
										<div class="col-3 col-sm-3 col-md-3 col-lg-3 col-xl-3">
											<div class="form-group">
												<label class="login-label">Device URL</label>
												<input class="form-control" ng-model="item.device_url" type="text" disabled>
											</div>
										</div>																
									</div>		
										
								</div>							
							</div>
							<div class="card-footer">
								<div class="d-flex justify-content-end" ng-show="item.device_role_lookup_id==1" role="toolbar" aria-label="Toolbar with button groups">
									<div class="btn-toolbar">							
				  						<!-- <button type="button" ng-click="syncTransactions(item.activation_id)" class="btn btn-outline-secondary" style="margin-right: 10px;">Synchronize Transactions</button> -->															
				  						<!-- <button type="button" ng-click="resendActivationInfo(item.activation_id)" class="btn btn-secondary" style="margin-right: 10px;">Resend Activation ID</button> -->
				  						<button type="button" ng-click="reactivateDevice(item.activation_id)" class="btn btn-secondary" style="margin-right: 10px;">Reactivate ECPOS</button>
				  						<button type="button" ng-click="terminateDevice(item.activation_id)" class="btn btn-danger">Terminate ECPOS</button>
			  						</div>				  				
								</div>	
								<div class="d-flex justify-content-between" ng-show="item.device_role_lookup_id==2" role="toolbar" aria-label="Toolbar with button groups">
									<button type="button" ng-click="convertToMaster(item.activation_id)" class="btn btn-primary">Make Master</button>
									<div class="btn-toolbar">							
				  						<!-- <button type="button" ng-click="syncTransactions(item.activation_id)" class="btn btn-outline-secondary" style="margin-right: 10px;">Synchronize Transactions</button> -->															
				  						<!-- <button type="button" ng-click="resendActivationInfo(item.activation_id)" class="btn btn-secondary" style="margin-right: 10px;">Resend Activation ID</button> -->
				  						<button type="button" ng-click="reactivateDevice(item.activation_id)" class="btn btn-secondary" style="margin-right: 10px;">Reactivate ECPOS</button>
				  						<button type="button" ng-click="terminateDevice(item.activation_id)" class="btn btn-danger">Terminate ECPOS</button>
			  						</div>				  				
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
	
	<!-- Create ECPOS Modal -->
	<div class="modal fade" id="ecposModal" tabindex="-1" role="dialog" aria-labelledby="ecposModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="actionEcpos == 'create'">Create ECPOS</h5>
		        <h5 ng-show="actionEcpos == 'update'">Edit ECPOS</h5>		
		        <button type="button" class="close" ng-click="resetEcposModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Name</label>
								<input class="form-control" name="ecposName" ng-model="ecpos.name" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">URL</label>
								<input class="form-control" name="ecposUrl" ng-model="ecpos.url" type="text" required> 
							</div>
						</div>
					</div>
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
	      	  	<button class="btn btn-primary" ng-show="actionEcpos == 'create'" type="submit" ng-click="addDevice()"> Submit</button>
	      	  	<button class="btn btn-primary" ng-show="actionEcpos == 'update'" type="submit" ng-click="submitEditEcpos()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Staff List Modal -->
	<div class="modal fade" id="staffListModal" role="dialog" aria-labelledby="staffListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Staff List</h5>	
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="staffList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Name</th>
												<th>Username</th>
												<th>Status</th>
												<th>Action</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#staffModal" ng-click="modalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Staff
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Staff QR Modal -->
	<div class="modal fade" id="staffQRModal" tabindex="-1" role="dialog" aria-labelledby="staffQRModal" aria-hidden="true">
		<div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5>Staff QR</h5>
		        <button type="button" class="close" ng-click="closeQRModel()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body text-center">
		        <img class="img-fluid" alt="Responsive image" data-ng-src="data:image/png;base64,{{qrImgData}}">				  				 									
		      </div> 	      
		      <div>
		      	<button class="pull-right btn" ng-click="displayQRPdf()"><i class="fa fa-qrcode"></i> Pdf</button>
		      </div>     
		    </div>
		</div>
	</div>
	
	<!-- Create Staff Modal -->
	<div class="modal fade" id="staffModal" tabindex="-1" role="dialog" aria-labelledby="staffModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="staffForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="action=='create'">Create Staff</h5>
		        <h5 ng-show="action=='update'">Edit Staff</h5>		
		        <button type="button" class="close" ng-click="resetModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Name</label>
								<input class="form-control" name="staffName" placeholder="Name" ng-model="staff.name" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Email</label>
								<input class="form-control" name="staffEmail" placeholder="johndoe@email.com" ng-model="staff.email" type="email" required> 
							</div>
						</div>
					</div>	
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Mobile Phone</label>
								<input class="form-control" name="staffMobilePhone" placeholder="60161234567" ng-model="staff.mobilePhone" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Role</label>
								<select class="form-control" ng-model="staff.role" ng-options="x.role_name for x in roleList track by x.id" required>
								<option value="" disabled>Please choose a role</option>
								</select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Username</label>
								<input class="form-control" name="staffUsername" placeholder="Username" ng-model="staff.username" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Password</label>
								<input class="form-control" name="staffPassword" placeholder="Password" ng-model="staff.password" type="password" required> 
							</div>
						</div>					
					</div>
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="action=='create'" type="submit" ng-click="submitStaff()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="action=='update'" type="submit" ng-click="submitStaff()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Table List Modal -->
	<div class="modal fade" id="tableListModal" role="dialog" aria-labelledby="tableListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="tableListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Table List</h5>	
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="tableList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Table Name</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#tableModal" ng-click="tableModalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Table
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Create Table Modal -->
	<div class="modal fade" id="tableModal" tabindex="-1" role="dialog" aria-labelledby="tableModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="tableForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5 ng-show="actionTable == 'create'">Create Table</h5>
		        <h5 ng-show="actionTable == 'update'">Edit Table</h5>		
		        <button type="button" class="close" ng-click="resetTableModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Name</label>
								<input class="form-control" name="tableName" placeholder="Name" ng-model="table.name" type="text" required> 
							</div>
						</div>
					</div>	
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="actionTable == 'create'" type="submit" ng-click="submitTable()"> Submit</button>
		      	<button class="btn btn-primary" ng-show="actionTable == 'update'" type="submit" ng-click="submitTable()"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Loading Modal [START] -->
	<div class="modal" id="loading_modal" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-dialog-centered modal-sm" role="document">
		<div class="modal-content">
			<div class="modal-body">
				<div class="text-center">
					<img style="width:75%" src="${pageContext.request.contextPath}/assets/images/byodadmin/gif/loading.gif"><br>
						<span>Loading...</span>
				</div>
			</div>
		</div>
		</div>
	</div>
	<!-- Loading Modal [END] -->
	
	<!-- Room List Modal [START] -->
	<div class="modal fade" id="roomListModal" role="dialog" aria-labelledby="roomListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="tableListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Room List</h5>	
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="roomList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Room Number</th>
												<th>Floor</th>
												<th>Type</th>
												<th>Category</th>
												<th>Price/Night</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
				<button type="button" class="btn btn-social btn-danger bg-aqua" data-toggle="modal" data-target="#roomTypeListModal" ng-click="roomTypeListModalType()">
					<span class="btn-label"><i class="fa fa-edit"></i></span> Room Type
				</button>
				<button type="button" class="btn btn-social btn-warning bg-aqua" data-toggle="modal" data-target="#roomCategoryListModal" ng-click="roomCategoryListModalType()">
					<span class="btn-label"><i class="fa fa-edit"></i></span> Room Category
				</button>
				<button type="button" class="btn btn-social ml-auto btn-primary bg-aqua" data-toggle="modal" data-target="#roomModal" ng-click="roomModalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Room
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	<!-- Room List Modal [END] -->
	
	<!-- Room Type List Modal [START] -->
	<div class="modal fade" id="roomTypeListModal" role="dialog" aria-labelledby="roomTypeListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="tableListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Room Type List</h5>	
		        <button type="button" class="close" ng-click="resetRoomTypeListModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="roomTypeList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Room Type Name</th>
												<th>Base Price</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#roomTypeModal" ng-click="roomTypeModalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Room Type
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	<!-- Room Type List Modal [END] -->
	
	<!-- Room Category List Modal [START] -->
	<div class="modal fade" id="roomCategoryListModal" role="dialog" aria-labelledby="roomCategoryListModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="tableListForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin">
		      <div class="modal-header">
		        <h5>Room Category List</h5>	
		        <button type="button" class="close" ng-click="resetRoomCategoryListModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      		<div class="table-responsive">
									<table id="roomCategoryList_dtable" class="table table-bordered table-hover display" style="width:100%">
										<thead>
											<tr>
												<th>ID</th>
												<th>Room Category Name</th>
											</tr>
										</thead>										
										<tbody>									
										</tbody>
									</table>
								</div>			       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" data-toggle="modal" data-target="#roomCategoryModal" ng-click="roomCategoryModalType('create')">
					<span class="btn-label"><i class="fa fa-plus"></i></span> Add Room Category
				</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	<!-- Room Category List Modal [END] -->
	
	<!-- Create Room Modal -->
	<div class="modal fade" id="roomModal" tabindex="-1" role="dialog" aria-labelledby="roomModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="roomForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin" ng-submit="submitRoom()">
		      <div class="modal-header">
		        <h5 ng-show="actionRoom == 'create'">Create Room</h5>
		        <h5 ng-show="actionRoom == 'update'">Edit Room</h5>		
		        <button type="button" class="close" ng-click="resetRoomModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Room Number</label>
								<input class="form-control" name="roomNumber" placeholder="Room Number" ng-model="room.number" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Room Type</label>
								<select class="form-control" ng-model="room.type" 
								ng-options="type.id as type.name for type in room_types" required>
									<option value="" disabled>-- SELECT --</option>
								</select> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Floor Number</label>
								<input class="form-control" name="floor" placeholder="Floor Number" ng-model="room.floor" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Room Category</label>
								<select class="form-control" ng-model="room.category" 
								ng-options="category.id as category.name for category in room_categories" required>
									<option value="" disabled>-- SELECT --</option>
								</select> 
							</div>
						</div>
					</div>	
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="actionRoom == 'create'" type="submit"> Submit</button>
		      	<button class="btn btn-primary" ng-show="actionRoom == 'update'" type="submit"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Create Room Type Modal -->
	<div class="modal fade" id="roomTypeModal" tabindex="-1" role="dialog" aria-labelledby="roomTypeModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="roomForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin" ng-submit="submitRoomType()">
		      <div class="modal-header">
		        <h5 ng-show="actionRoomType == 'create'">Create Room Type</h5>
		        <h5 ng-show="actionRoomType == 'update'">Edit Room Type</h5>		
		        <button type="button" class="close" ng-click="resetRoomTypeModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Room Type Name</label>
								<input class="form-control" name="roomTypeName" placeholder="Name" ng-model="roomType.name" type="text" required> 
							</div>
						</div>
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Room Price</label>
								<input class="form-control" name="roomPrice" placeholder="Name" ng-model="roomType.base_price" type="text" required> 
							</div>
						</div>
					</div>
					<div class="row">
						 <div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
						 	<div class="form-group">
						 		<img style="max-width:100%;max-height:100%;" ng-show="roomType.image_path!=null"  ng-src="{{roomType.image_path}}"/>
						 	</div>									
							<div class="form-group">																	
								<label class="login-label">Image <i class="fa fa-info-circle ii" data-toggle="tooltip" data-placement="top" title="Image cannot exceed 150kb, image aspect ratio is within 1:1 to 2:1, minimum width of 300px and minimum height of 200px."></i></label> 
								<input id="roomTypeImage" type="file" accept="image/*"/> 
							</div>
						</div>
					</div>	
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="actionRoomType == 'create'" type="submit"> Submit</button>
		      	<button class="btn btn-primary" ng-show="actionRoomType == 'update'" type="submit"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
	
	<!-- Create Room Category Modal -->
	<div class="modal fade" id="roomCategoryModal" tabindex="-1" role="dialog" aria-labelledby="roomCategoryModal" aria-hidden="true">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <form id="roomForm" method="POST" accept-charset="UTF-8" role="form" class="form-signin" ng-submit="submitRoomCategory()">
		      <div class="modal-header">
		        <h5 ng-show="actionRoomCategory == 'create'">Create Room Category</h5>
		        <h5 ng-show="actionRoomCategory == 'update'">Edit Room Category</h5>		
		        <button type="button" class="close" ng-click="resetRoomCategoryModal()" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body">
		      	<div class="form-section">
					<div class="row">
						<div class="col-6 col-sm-6 col-md-6 col-lg-6 col-xl-6">
							<div class="form-group">
								<label class="login-label">Room Category Name</label>
								<input class="form-control" name="roomCategoryName" placeholder="Name" ng-model="roomCategory.name" type="text" required> 
							</div>
						</div>
					</div>
				</div>				       									  				 									
		      </div>
		      <div class="modal-footer">
		      	<button class="btn btn-primary" ng-show="actionRoomCategory == 'create'" type="submit"> Submit</button>
		      	<button class="btn btn-primary" ng-show="actionRoomCategory == 'update'" type="submit"> Update</button>
		      </div>
		       </form>		      
		    </div>
		</div>
	</div>
</div>
</body>
</html>