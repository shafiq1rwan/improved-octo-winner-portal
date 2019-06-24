<html>
<script>
	app.controller('ctl_ecpos', function($scope, $http, $compile, $routeParams) {
		
		$scope.action = '';
		$scope.actionTable = '';
		$scope.actionEcpos = '';
		$scope.store = {id : $routeParams.id};
		$scope.staff = {};
		$scope.table = {};
		$scope.roleList = [];
		$scope.ecpos = {}
		//$scope.tableCount = 0;
		$scope.qrImgData = "";
		
		$scope.modalType = function(action){
			$scope.action = action;
			// close staff list modal
			$('#staffListModal').modal('toggle');
		}
		
		$scope.getDeviceInfo = function(){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/store/ecposByStoreId?store_id='+$scope.store.id	
			})
			.then(function(response) {
				$scope.store.brand_id = response.data.brand_id;
				$scope.store.name = response.data.store_name;
				$scope.store.backend_id = response.data.backend_id;
				$scope.store.ecpos = response.data.ecpos;
				$scope.store.ecpos_count = $scope.store.ecpos.length;
				console.log($scope.store);		
			});
		}
		
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
		
		$scope.ecposModalType = function(action, index){
			$scope.actionEcpos = action;
			$('#ecposModal').modal('toggle');
			
			if(action=='update'){
				$scope.ecpos.name = $scope.store.ecpos[index].device_name;
				$scope.ecpos.url  = $scope.store.ecpos[index].device_url;
				$scope.ecpos.activation_id = $scope.store.ecpos[index].activation_id;
			}
		}
		
		$scope.resetEcposModal = function(){
			$scope.actionEcpos = '';
			$scope.ecpos = {};
			$('#ecposModal').modal('toggle');
		}
		
		$scope.submitEditEcpos = function(){
			swal({
				  title: "Are you sure?",
				  text: "Do you want perform update on ECPOS?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  $scope.isLoadingEnded = false;
					  $('#loading_modal').modal({backdrop: 'static', keyboard: false});
					  var postdata = {
					  	store_id : $scope.store.id,
					  	ecpos_name : $scope.ecpos.name,
					  	activation_id : $scope.ecpos.activation_id
					  };
					  console.log(postdata);
					  $http({
							method : 'POST',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/ecpos/edit',
							data : postdata
						})
						.then(function successCallback(response) {
							$scope.hideLoading();
							$scope.resetEcposModal();
							if(response.status==200){
								swal("ECPOS is updated", {
									icon: "success",
								});
								
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							$scope.hideLoading();
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
		
		$scope.convertToMaster = function(activationId){
			swal({
				  title: "Are you sure?",
				  text: "Do you want to change the selected ECPOS to Master?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  $scope.isLoadingEnded = false;
					  $('#loading_modal').modal({backdrop: 'static', keyboard: false});
					  $http({
							method : 'POST',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/ecpos/convertToMaster?store_id='+$scope.store.id+'&activation_id='+activationId 	
						})
						.then(function successCallback(response) {
							$scope.hideLoading();
							if(response.status==200){
								swal("Successfully changed new Master ECPOS.", {
									icon: "success",
								});
								
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							$scope.hideLoading();
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
		
		$scope.addDevice = function(){
			if($scope.ecpos.name == null || $scope.ecpos.name=='' ||
					$scope.ecpos.url == null || $scope.staff.url==''){
			}
			else{
				swal({
					  title: "Are you sure?",
					  text: "Do you want to generate Activation ID for new ECPOS?",
					  icon: "warning",
					  buttons: true,
					  dangerMode: true,
					})
					.then((willCreate) => {
					  if (willCreate) {
						  $scope.isLoadingEnded = false;
						  $('#loading_modal').modal({backdrop: 'static', keyboard: false});
						  var postdata = {
						  	store_id : $scope.store.id,
						  	ecpos_name : $scope.ecpos.name,
						  	ecpos_url : $scope.ecpos.url
						  };
						  console.log(postdata);
						  $http({
								method : 'POST',
								headers : {'Content-Type' : 'application/json'},
								url : '${pageContext.request.contextPath}/menu/store/ecpos/activate',
								data : postdata
							})
							.then(function successCallback(response) {
								$scope.hideLoading();
								$scope.resetEcposModal();
								if(response.status==200){
									swal("Activation ID is generated", {
										icon: "success",
									});
									
									$scope.getDeviceInfo();
								}
							 }, function errorCallback(response) {
								$scope.hideLoading();
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
		}
		
		// validation
		$scope.submitStaff = function(){
			if($scope.staff.name == null || $scope.staff.name=='' ||
					$scope.staff.email == null || $scope.staff.email=='' ||
						$scope.staff.mobilePhone == null || $scope.staff.mobilePhone=='' ||
							$scope.staff.username == null || $scope.staff.username=='' ||
								$scope.staff.password == null || $scope.staff.password=='' ||
									$scope.staff.role == null || $scope.staff.role==''){
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
					$(location).attr('href','${pageContext.request.contextPath}/user');			
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
			}, function(response){
				console.log(response);
				swal({
				  title: "Error",
				  text: response.data,
				  icon: "warning",
				  dangerMode: true,
				});
			});
		}
		
		$scope.resetModal = function(){
			$scope.action = '';
			$scope.staff = {};
			$('#staffModal').modal('toggle');
			$('#staffListModal').modal('toggle');
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
					 }},
					 {"data" : "username",
						 "render": function ( data, type, full, meta ) {
							 return '<button class="btn btn-primary" ng-click="generateStaffQR('+full.id+')">Show QR</button>';
					 }}
				],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);  //add this to compile the DOM
				}
			});
			
			$scope.generateStaffQR = function(staffId) {
				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/store/ecpos/generateStaffQR',
					data : {
						staff_id: staffId
					}
				})
				.then(function(response) {
					if (response.status == "403") {
						alert("Session TIME OUT");
						$(location).attr('href','${pageContext.request.contextPath}/user');			
					} else if(response.status == "200") {
						$scope.qrImgData = response.data.qrImg;
						
						$('#staffListModal').modal('hide');
						$('#staffQRModal').modal({
						    backdrop: 'static'
						})
						$('#staffQRModal').modal('show');
					}
				}, function(response){
					console.log(response);
					swal({
					  title: "Error",
					  text: response.data,
					  icon: "warning",
					  dangerMode: true,
					});
				});
			}
			$scope.closeQRModel = function() {
				$('#staffQRModal').modal('hide');
				$('#staffListModal').modal('show');
			}
			
			$('#staffList_dtable tbody').off('click', 'tr');
			$('#staffList_dtable tbody').on('click', 'tr', function(evt) {
				if($(evt.target).is('.btn')) {
					return;
				}
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
						
						$('#staffListModal').modal('toggle');
						$('#staffModal').modal('toggle');
					}
				});
			});
		}
		
		$scope.terminateDevice = function(activationId){
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
							url : '${pageContext.request.contextPath}/menu/store/ecpos/terminate?store_id=' + $scope.store.id+'&activation_id='+activationId 	
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
		
		$scope.resendActivationInfo = function(activationId){		
			swal({
				  title: "Are you sure?",
				  text: "Do you want to resend ECPOS activation email?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  $scope.isLoadingEnded = false;
					  $('#loading_modal').modal({backdrop: 'static', keyboard: false});
					  $http({
							method : 'POST',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/resendAct?store_id='+$scope.store.id+'&activation_id='+activationId
						})
						.then(function successCallback(response) {
							console.log(response);
							$scope.hideLoading();
							if(response.status==200){
								//console.log(response);
								swal("Successfully resend ECPOS activation email.", {
									icon: "success",
								});
							}
						 }, function errorCallback(response) {
							 console.log(response);
							 $scope.hideLoading();
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
		
		$scope.syncTransactions = function(activationId){		
			swal({
				  title: "Are you sure?",
				  text: "Do you want to synchronize ECPOS transactions to cloud?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  $scope.isLoadingEnded = false;
					  $('#loading_modal').modal({backdrop: 'static', keyboard: false});
					  $http({
							method : 'POST',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/ecpos/syncTrans?store_id='+$scope.store.id+'&activation_id='+activationId
						})
						.then(function successCallback(response) {
							console.log(response);
							$scope.hideLoading();
							if(response.status==200){
								//console.log(response);
								swal("Successfully synchronize ECPOS transactions to cloud.", {
									icon: "success",
								});
							}
						 }, function errorCallback(response) {
							 console.log(response);
							 $scope.hideLoading();
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
		
		$scope.hideLoading = function() {
			$scope.isLoadingEnded = true;
			$('#loading_modal').modal('hide');
		}
		$('#loading_modal').on('shown.bs.modal', function (event) {
			if ($scope.isLoadingEnded) {
				$('#loading_modal').modal('hide');
			}
		});
		
		$scope.reactivateDevice = function(activationId){		
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
							url : '${pageContext.request.contextPath}/menu/store/ecpos/reactivate?store_id='+$scope.store.id+'&activation_id='+activationId 	
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
		
		// table modal setting
		$scope.resetTableModal = function(){
			$scope.actionTable = '';
			$scope.table = {};
			$('#tableModal').modal('toggle');
			$('#tableListModal').modal('toggle');
		}
		
		$scope.tableModalType = function(action){
			$scope.actionTable = action;
			// close table list modal
			$('#tableListModal').modal('toggle');
		}
		
		// validation
		$scope.submitTable = function(){
			if($scope.table.name == null || $scope.table.name==''){
			}
			else{
				if($scope.actionTable=='create'){
					swal({
						  title: "Are you sure?",
						  text: "Please confirm to add a table",
						  icon: "warning",
						  buttons: true,
						  dangerMode: true,
						})
						.then((willCreate) => {
						  if (willCreate) {
							  $scope.postRequestTable();
						}
					});
				}
				else if($scope.actionTable=='update'){
					 $scope.postRequestTable();
				}				
			}
		}

		$scope.postRequestTable = function(){
			var postdata = {
					id : $scope.actionTable=='create'?undefined:$scope.table.id,
					store_id: $scope.store.id,
					tableName : $scope.table.name
				}
				
				console.log(postdata);
				
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : $scope.actionTable=='create'?'${pageContext.request.contextPath}/menu/store/ecpos/createTable':'${pageContext.request.contextPath}/menu/store/ecpos/updateTable',
				data : postdata
			})
			.then(function(response) {
				if (response.status == "403") {
					alert("Session TIME OUT");
					$(location).attr('href','${pageContext.request.contextPath}/user');			
				} else if(response.status == "200") {
					// ok
					if($scope.actionTable=='create'){
						swal("The table has been created", {
							icon: "success",
						});
					}
					else if($scope.actionTable=='update'){
						swal("The table has been updated", {
							icon: "success",
						});
					}
					$scope.resetTableModal();
					$('#tableModal').modal('toggle');
					$('#tableListModal').modal('toggle');
					$scope.refreshTable2();
				}
			}, function(response){
				console.log(response);
				swal({
				  title: "Error",
				  text: response.data,
				  icon: "warning",
				  dangerMode: true,
				});
			});
		}
		
		$scope.refreshTable2 = function(){
			var table = $('#tableList_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/store/ecpos/getAllTables?store_id="+$scope.store.id,
					"dataSrc": function ( json ) {
						/* $scope.$apply(function() {
							$scope.tableCount = JSON.stringify(json.tableCount);
						}); */
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
					{"data" : "tableName"}
				]			
			});
			
			$('#tableList_dtable tbody').off('click', 'tr');
			$('#tableList_dtable tbody').on('click', 'tr', function(evt) {				
				$scope.actionTable = 'update';
				$scope.table = {}
				$scope.table.id = table.row(this).data().id;
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/store/ecpos/tableById?store_id='+$scope.store.id + '&id=' + $scope.table.id		
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find table detail");
					} else{
						$scope.table.name = response.data.tableName;
						
						$('#tableListModal').modal('toggle');
						$('#tableModal').modal('toggle');
					}
				});
			});
		}
		
		$scope.getDeviceInfo();
		
		$(document).ready(function() {
			$scope.refreshTable();
			$scope.refreshTable2();
		});
	});
	
</script>
</html>