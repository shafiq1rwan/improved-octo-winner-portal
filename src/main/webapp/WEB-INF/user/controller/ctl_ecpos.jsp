<html>
<script>
	app.controller('ctl_ecpos', function($scope, $http, $compile, $routeParams) {
		
		$scope.action = '';
		$scope.store = {id : $routeParams.id};
		$scope.staff = {}
		$scope.roleList = [];
		$scope.ecpos = {}
		$scope.showActivation = false;
		
		$scope.modalType = function(action){
			$scope.action = action;
			// close staff list modal
			$('#staffListModal').modal('toggle');
		}
		
		// get store info
		$http({
			method : 'GET',
			headers : {'Content-Type' : 'application/json'},
			url : '${pageContext.request.contextPath}/menu/store/storeById?id='+$scope.store.id	
		})
		.then(function(response) {
			$scope.store.name = response.data.store_name;
			$scope.store.backend_id = response.data.backend_id;
			console.log($scope.store);		
		});
		
		
		// get role list
		$http({
			method : 'GET',
			headers : {'Content-Type' : 'application/json'},
			url : '${pageContext.request.contextPath}/menu/store/ecpos/getStaffRole'	
		})
		.then(function(response) {
			$scope.roleList = response.data;
			console.log($scope.roleList);
		});
		
		// validation
		$scope.submitStaff = function(){
			if($scope.staff.name == null || $scope.staff.name=='' ||
					$scope.staff.email == null || $scope.staff.email=='' ||
						$scope.staff.mobilePhone == null || $scope.staff.mobilePhone=='' ||
							$scope.staff.username == null || $scope.staff.username=='' ||
								$scope.staff.password == null || $scope.staff.password=='' ||
									$scope.staff.role.id == null || $scope.staff.role.id==''){
			}
			else{
				if($scope.action=='create'){
					swal({
						  title: "Are you sure?",
						  text: "Once created, you will not be able to remove staff",
						  icon: "warning",
						  buttons: true,
						  dangerMode: true,
						})
						.then((willCreate) => {
						  if (willCreate) {
							  $scope.postRequest();
						}
					});
				}
				else if($scope.action=='update'){
					 $scope.postRequest();
				}				
			}
		}
		
		// submit request
		$scope.postRequest = function(){
			var postdata = {
					staff_id : $scope.action=='create'?undefined:$scope.staff.id,
					store_id: $scope.store.id,
					name : $scope.staff.name,
					email : $scope.staff.email,
					mobilePhone : $scope.staff.mobilePhone,
					username : $scope.staff.username,
					password: $scope.staff.password,
					role_id: $scope.staff.role.id,
					isActive: $scope.action=='create'?undefined:$scope.staff.isActive
				}
				
				console.log(postdata);
				
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : $scope.action=='create'?'${pageContext.request.contextPath}/menu/store/ecpos/createStaff':'${pageContext.request.contextPath}/menu/store/ecpos/updateStaff',
				data : postdata
			})
			.then(function(response) {

				if (response.status == "403") {
					alert("Session TIME OUT");
					$(location).attr('href','${pageContext.request.contextPath}/admin');			
				} else if(response.status == "200") {
					// ok
					if($scope.action=='create'){
						swal("The staff has been created", {
							icon: "success",
						});
					}
					else if($scope.action=='update'){
						swal("The staff has been updated", {
							icon: "success",
						});
					}
					$scope.resetModal();
					$('#staffModal').modal('toggle');
					$('#staffListModal').modal('toggle');
					$scope.refreshTable();
				}
			});
		}
		
		$scope.resetModal = function(){
			$scope.action = {};
			$scope.staff = {	
};
		}
		
		$scope.refreshTable = function(){
			var table = $('#staffList_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/store/ecpos/getAllStaff?store_id="+$scope.store.id,
					"dataSrc": function ( json ) {
		                return json.data;
		            },
					"statusCode" : {
						403 : function() {
							alert("Session TIME OUT");
							$(location).attr('href', '${pageContext.request.contextPath}/user');
						}
					}
				},
				destroy : true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "id", "width": "5%"}, 
					{"data" : "name"},
					{"data" : "username"},
					{"data" : "isActive", "width": "10%",
						"render": function ( data, type, full, meta ) {
						 	var isActive = full.isActive;
						 	var status;
						 	if(isActive==1){
						 		status = 'Active';
						 	}
						 	else if(isActive==0){
						 		status = 'Inactive';
						 	}
						    return status;	  						
					 }}
				]
				
			});
			
			$('#staffList_dtable tbody').off('click', 'tr');
			$('#staffList_dtable tbody').on('click', 'tr', function(evt) {				
				$scope.action = 'update';
				$scope.staff = {
						role:{}
				}
				$scope.staff.id = table.row(this).data().id;
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/store/ecpos/staffById?store_id='+$scope.store.id + '&id=' + $scope.staff.id		
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find staff detail");
					} else{
						$scope.staff.name = response.data.name;
						$scope.staff.email = response.data.email;
						$scope.staff.mobilePhone = response.data.mobilePhone;
						$scope.staff.role.id = response.data.role_id;
						$scope.staff.username = response.data.username;
						$scope.staff.password = response.data.password;
						$scope.staff.isActive = response.data.isActive;

						$('#staffModal').modal('toggle');
					}
				});
			});
		}
		
		$scope.generateActivation = function(){
			swal({
				  title: "Are you sure?",
				  text: "Do you want to generate Activation ID?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/ecpos/activate?store_id='+$scope.store.id 	
						})
						.then(function successCallback(response) {
							if(response.status==200){
								swal("Activation ID is generated", {
									icon: "success",
								});
								
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {	
					    	swal({
								  title: "Error",
								  text:  response.data,
								  icon: "warning",
								  dangerMode: true,
							});						    
						});
				}					 
			});
		}
		
		$scope.terminateDevice = function(){
			swal({
				  title: "Are you sure?",
				  text: "Do you want to terminate ECPOS?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {				 
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/ecpos/terminate?store_id='+$scope.store.id 	
						})
						.then(function successCallback(response) {
							console.log(response);
							if(response.status==200){
								//console.log(response);
								swal("Successfully terminate ECPOS.", {
									icon: "success",
								});
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							 console.log(response);
							swal({
								  title: "Error",
								  text: response.data,
								  icon: "warning",
								  dangerMode: true,
							});					
						});				 
				}					 
			});
		}
		
		$scope.reactivateDevice = function(){		
			swal({
				  title: "Are you sure?",
				  text: "Do you want to reactivate ECPOS?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {				 
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/ecpos/reactivate?store_id='+$scope.store.id 	
						})
						.then(function successCallback(response) {
							console.log(response);
							if(response.status==200){
								//console.log(response);
								swal("Successfully reactivate ECPOS.", {
									icon: "success",
								});
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							 console.log(response);
							 swal({
								  title: "Error",
								  text: response.data,
								  icon: "warning",
								  dangerMode: true,
							});		
						});		 
				}					 
			});
		}
		
		$scope.getDeviceInfo = function(){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/store/ecpos/getInfo?store_id='+$scope.store.id 	
			})
			.then(function successCallback(response) {
				if(response.status==200){
					console.log(response.data);
					$scope.showActivation = false;
					$scope.ecpos.activation_id = response.data.activation_id;
					$scope.ecpos.activation_key = response.data.activation_key;
					$scope.ecpos.created_date = response.data.created_date;
					$scope.ecpos.status = response.data.status;
					console.log($scope.ecpos);
				}
			 }, function errorCallback(response) {	
			    if(response.status==400){
			    	$scope.showActivation = true;
			    }
			});
		}
		
		$scope.getDeviceInfo();
		
		$(document).ready(function() {	
			$scope.refreshTable();
		});
	});
	
</script>
</html>