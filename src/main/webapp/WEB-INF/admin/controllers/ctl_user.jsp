<html>
<script>
	app.controller('ctl_user', function($scope, $http, $compile, $sce) {
		
		$scope.users = [];
		$scope.user = {};
		$scope.user_id = 0;
		$scope.brands = [];
		$scope.brand_id = 0;
		
		$scope.selectable_brands = [];
		
		$scope.assigned_brands = [];
		
		$scope.access_rights = [];
		$scope.action = '';
		
		$scope.alertMessage = "";
		
		var table;
		var brands_array = [];
		var access_rights_array = []; 
		//var access_rights_data = {};
		
		$scope.role = '';
		$scope.roles = [];
		var role_array = [];
		
		$(document).ready(function() {
			getUserInfo();
		});
		
		var getUserInfo = function(){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/'
			})
			.then(
				function(response) {
					$scope.role = response.data.role;
					$scope.users = response.data.user_list;
					//$scope.roles = response.data.role_list.filter(r => r!= $scope.role);
					$scope.roles = response.data.role_list;
					$scope.selectable_brands = response.data.brand_list;
					
					refreshUserTable($scope.users);
				},
				function(response) {
					alert(response.data);
			});
		}
		
		var refreshUserTable = function(users){
			table = $('#users_dtable').DataTable({
				'data': users,
				/* 'ajax' : {
					'url' : '${pageContext.request.contextPath}/users/',
					'dataSrc': function ( json ) {
		                return json;
		            },  
					'statusCode' : {
						403 : function() {
							alert('Session TIME OUT');
							$(location).attr('href', '${pageContext.request.contextPath}/logout');
						}
					}
				} */
				"destroy" : true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "id", "visible": false, "searchable": false},
					{"data" : "name"},
					{"data" : "email"},
					{"data" : "mobileNumber"},
					{"data" : "username"},
					{"data": "id",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button ng-click="promptEditUserModal('+ id +')" type="button" class="btn btn-primary custom-fontsize"><b><i class="fa fa-wrench"></i>Edit</b></button><button data-toggle="modal" data-target="#userBrandModal" data-keyboard="false" data-backdrop="static" ng-click="promptAssignBrandModal('+ id +')" type="button" class="btn btn-info custom-fontsize"><b><i class="fa fa-wrench"></i>Assign Brand</b></button>'
							    	+'<button data-toggle="modal" data-target="#assignedBrandModel" data-keyboard="false" data-backdrop="static" ng-click="promptAssignedBrandModal('+ id +')" type="button" class="btn btn-default custom-fontsize"><b><i class="fa fa-wrench"></i>View Brands</b></button></div>'	
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }
			});
		}
		
		$scope.promptEditUserModal = function(id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/'+ id
			})
			.then(
				function(response) {			
					$scope.user = response.data; 
					$scope.setModalType('update');
					$('#userModal').modal('toggle');
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.promptAssignBrandModal = function(id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/'+ id +'/brands'
			})
			.then(
				function(response) {
					$scope.user_id = id;
					$scope.brands = response.data;
					checked($scope.brands);
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.promptAssignedBrandModal = function(id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/assigned-brands?userId='+id
			})
			.then(
				function(response) {
					$scope.user_id = id;
					$scope.assigned_brands = response.data;
				},
				function(response) {
					alert(response.data);
			});
		}

		$scope.promptAccessRightsModal = function(id){
			console.log("Brand Id: " + id);
			console.log("User Id: " + $scope.user_id);
			$('#assignedBrandModel').modal('toggle');

 			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/access-rights?id='+$scope.user_id+'&brandId='+id
			})
			.then(
				function(response) {
					$scope.access_rights = response.data;
					$scope.brand_id = id;
					//checkedExistingList($scope.access_rights, access_rights_array);
				},
				function(response) {
					alert(response.data);
			});
		}

		$scope.addIntoBrandList = function(brand){	
			if(brand.exist){	
				brands_array.push(brand);
			} else {
 				 var toDel = brands_array.indexOf(brand);
	 				if (toDel > -1) {
	 					brands_array.splice(toDel,1); 
	 				}
			}
		}
		
		function checked(brands){
			for(var i=0;i<brands.length;i++){
				if(brands[i].exist){
					brands_array.push(brands[i]);
				}
			}
			console.log("already list "+ brands_array);
		}
		
		function checkedExistingList(sourceList, targetArray){
			for(var i=0;i<sourceList.length;i++){
				targetArray.push(sourceList[i]);
			}
			console.log("target list "+ targetArray);
		}
		
		$scope.resetModal = function(){
			$scope.user = {};
			$scope.user_id = 0;
			$scope.action = '';
			$scope.brands = [];
			brands_array = [];
			$scope.assigned_brands = [];
			$scope.resetAccessRightsModal('reset');
		}
		
		$scope.resetAccessRightsModal = function(type){
			if(type != 'reset'){
				
				$('#assignedBrandModel').modal('toggle');
			} 
			$scope.brand_id = 0;
			$scope.access_rights = [];
			access_rights_array = [];
			//access_rights_data = {};
		}
		
		$scope.setModalType = function(action){
			$scope.action = action;
			console.log($scope.action);
		}
		
	/* 	function filterRoles(role){
			for(var i=0;$scope.roles.length;i++){
				role_array
			}
			
			role_array
			
			
		} */
		
		$scope.submitUser = function(){
			if($scope.user.name == null || $scope.user.name == '' 
					|| $scope.user.email == null || $scope.user.email == '' 
					|| $scope.user.mobileNumber == null || $scope.user.mobileNumber == '' 
					|| $scope.user.address == null || $scope.user.address == ''
					|| $scope.user.username == null || $scope.user.username == '' 
					|| $scope.user.authority == null || $scope.user.authority == ''){
			}
			else {
				var userUrl = $scope.action == 'create'?'signup':'edit';
				
				if(userUrl == 'signup'){
					$('#loading_modal').modal('show');
				}

				var	data = {
						'id' : $scope.action == 'update'?$scope.user.id:0,
						'name' : $scope.user.name,
						'email' : $scope.user.email,
						'mobileNumber' : $scope.user.mobileNumber,
						'address' : $scope.user.address,
						'username' : $scope.user.username,
						'enabled' : $scope.user.enabled || false,
						'role' : $scope.user.authority,
						'brand' : $scope.user.brand || null
				};
				var postData = JSON.stringify(data);
					
				console.log("Submit User");
				console.log(postData);

 				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/users/' + userUrl,
					data : postData
				})
				.then(
					function(response) {
						if(userUrl == 'signup'){
							$('#loading_modal').modal('hide');
						}
						$scope.resetModal();
						$('#userModal').modal('hide');
						getUserInfo();
					},
					function(response) {
	 					if(userUrl == 'signup'){
							setTimeout(function(){
								  $('#loading_modal').modal('hide');
							}, 1000);
						} 

						if(response.status == '409'){
							$scope.alertMessage = $sce.trustAsHtml(response.data.responseMessage);
							$('#alertModal').modal('show');
						} else {
							$scope.alertMessage = $sce.trustAsHtml(response.data);
							$('#alertModal').modal('show');
						}

						//$scope.resetModal();
						//$('#userModal').modal('toggle');
				});
				
			}
		}
		
		$scope.assignPermissions = function(brand_id){
			//access_rights_array = $scope.access_rights;
			
			var post_data = JSON.stringify({
				'user_id' : $scope.user_id,
				'brand_id': brand_id,
				'permissions': $scope.access_rights
			});
			
			console.log("Access Rights data: "+ post_data);

			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/assign-access-rights',
				data : post_data
			})
			.then(
				function(response) {
					$('#accessRightsModal').modal('toggle');
					$scope.resetAccessRightsModal('close');
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.assignUserToBrands = function(){
	 		var post_data = JSON.stringify({
				'userId' :  $scope.user_id,
				'brands' : brands_array,
			});
			
			console.log(post_data);
			
  	 		$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/assign-brands',
				data : post_data
			})
			.then(
				function(response) {
					$scope.resetModal();
					$('#userBrandModal').modal('toggle');
				},
				function(response) {
					alert(response.data);
			});
		}

	});
</script>
</html>