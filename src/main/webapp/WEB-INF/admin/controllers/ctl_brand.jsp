<html>
<script>
	app.controller('ctl_brand', function($scope, $http, $compile) {
				
		$scope.brands = [];
		$scope.users = [];
		$scope.role = '';
		$scope.brand = {};
		$scope.action = '';
		$scope.brand_id = 0;
		
		var table;
		var users_array = [];
		
		$(document).ready(function() {
			getBrandInfo();
		});
		
		var getBrandInfo = function(){	
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/brands/user'
			})
			.then(
				function(response) {
					$scope.role = response.data.role;
					$scope.brands = response.data.brands;
					refreshBrandTable($scope.brands);
				},
				function(response) {
					alert(response.data);
			});
		}
	
		//Datatable
		var refreshBrandTable = function(brands){
			table = $('#brands_dtable').DataTable({
				'data': brands,
	/* 			'ajax' : {
					'url' : '${pageContext.request.contextPath}/users/brandsByUser',
					'dataSrc': function ( json ) {
						roleInfo = json.role;
		                return json.brands;
		            },  
					'statusCode' : {
						403 : function() {
							alert('Session TIME OUT');
							$(location).attr('href', '${pageContext.request.contextPath}/logout');
						}
					}
				}, */
				"pageLength": 4,
				"destroy" : true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "name"},
					{"data": "id", "width": "30%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							 	var html;
							 	
							 	if($scope.role === 'ROLE_SUPER_ADMIN'){
							 		html = '<button data-toggle="modal" data-target="#brandUserModal" data-keyboard="false" data-backdrop="static" ng-click="getUsersByBrand('+ id +')" type="button" class="btn btn-default custom-fontsize"><b><i class="fa fa-check-square-o"></i> Assign</b></button> <button ng-click="redirectToBrand('+ id +')" type="button" class="btn btn-primary custom-fontsize"><b><i class="fa fa-plug"></i> Connect</b></button> <button ng-click="promptEditBrandModal('+ id +')" type="button" class="btn btn-danger custom-fontsize"><b><i class="fa fa-pencil"></i> Edit</b></button>'
							 	} 
							 	else {
							 		html = '<div class="btn-toolbar justify-content-between"><button ng-click="redirectToBrand('+ id +')" type="button" class="btn btn-primary custom-fontsize"><b><i class="fa fa-plug"></i>Connect</b></button></div>';
							 	}
							    return 	html;
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }
			});
		}
		
		$scope.getUsersByBrand = function(brand_id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/brands/users-in-brand?brandId='+brand_id
			})
			.then(
				function(response) {
					$scope.brand_id = brand_id;
					$scope.users = response.data;
					checked($scope.users);
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.redirectToBrand = function(id){
				//var obj = $scope.brands.find(o => o.id === $scope.selectedBrand);
		 		var postData = JSON.stringify({'id': id });

				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/brands/redirect',
					data : postData
				})
				.then(
					function(response) {
						if(response.data.role){
	   						$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user/#!Router_store'); 
						} else {
		 				 	$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user/'+decideDefaultRoute(response.data)); 
						}
					},
					function(response) {
						alert(response.data);
				});
		}
		
		function decideDefaultRoute(accessRightJson){
			var accessRights = accessRightJson.accessRights;
			
			for (var key in accessRights) {
			    if (accessRights.hasOwnProperty(key)) {
			        //console.log(key + " -> " + accessRights[key]);
			        if(accessRights[key]){
			        	if(key === "group-category")
			        		return "#!Router_group_category";
			        	else if(key === "report")
			        		return "#!Router_report";
			        	else if(key === "store")
			        		return "#!Router_store";
			        	else if(key === "menu-item")
			        		return 	"#!Router_menu_item";
			        	else if(key === "setting")
			        		return  "#!Router_setting";
			        }
			    }
			}
			return "";
		}
		
		$scope.promptEditBrandModal = function(id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/brands?id=' + id,
			})
			.then(
				function(response) {			
					$scope.brand = response.data;
					$scope.setModalType('update');
					$('#brandModal').modal('toggle');
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.addIntoUserList = function(user){	
			if(user.exist){	
				 	users_array.push(user);
				} else {
	 				 var toDel = users_array.indexOf(user);
		 				if (toDel > -1) {
		 					users_array.splice(toDel,1); 
		 				}
				}
		}
		
		function checked(users){
			for(var i=0;i<users.length;i++){
				if(users[i].exist){
					users_array.push(users[i]);
				}
			}
			console.log("already list "+ users_array);
		}

		$scope.assignUser = function(){
			var post_data = JSON.stringify({
				'brandId' : $scope.brand_id,
				'users' : users_array
			});
			
			console.log(post_data);
			
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/brands/assign-users-to-brand',
				data : post_data
			})
			.then(
				function(response) {
					$scope.resetModal();
					$('#brandUserModal').modal('toggle');
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.setModalType = function(action){
			$scope.action = action;
		}
		
		$scope.resetModal = function(){
			$scope.brand_id = 0;
			$scope.brand = {};
			$scope.users = [];
			$scope.action = '';
			users_array = [];	
		}
		
		$scope.submitBrand = function(){
			if($scope.brand.name == null || $scope.brand.name == ''){
			} else {
				
				var postData = JSON.stringify({	
					'id' : $scope.action === 'update' ? $scope.brand.id : 0,
					'name': $scope.brand.name,
					'db_domain' : $scope.brand.dbDomain,
					'db_name' : $scope.brand.dbName,
					'db_user': $scope.brand.dbUsername,
					'db_password' : $scope.brand.dbPassword,
					'db_port' : $scope.brand.dbPort
				});
				
				var requestUrl = $scope.action === 'create' ? 'create' : 'edit';
				
				console.log(postData);
				
				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/brands/' + requestUrl,
					data : postData
				})
				.then(
					function(response) {			
						$scope.resetModal();
						$('#brandModal').modal('toggle');
						getBrandInfo();
					},
					function(response) {
						alert(response.data);
				});
			}
		}

	});
</script>
</html>