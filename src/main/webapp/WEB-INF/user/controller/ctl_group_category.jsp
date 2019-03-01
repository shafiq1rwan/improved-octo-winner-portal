<html>
<script>
	app.controller('ctl_group_category', function($scope, $http, $compile) {
	
		$scope.group_category = {};
		$scope.stores = [];
		$scope.action = "";
		var ori_store_array = [];
		var store_array = [];
		var store_with_group_category_array = [];
		
		$(document).ready(function() {
			$scope.refreshGroupCategoryTable();
		});
		
		$scope.setModalType = function(action_type){
			if(action_type === 'create'){
				$scope.getStore('create');
			}
			$scope.action = action_type;
		}
		
		$scope.addStoreIntoGroupCategory = function(store){
			if(store.is_checked){
				store_array.push(store.id);
			} else {
				 var toDel = store_array.indexOf(store.id);
					if (toDel > -1) {
		 					store_array.splice(toDel,1); 
		 			}
			}
		}
		
 		$scope.editStoreIntoGroupCategory = function(store){
			 if(store.is_checked){	
				console.log("Check");
 				store_array.push(store.id);
			} else {
				 console.log("Uncheck");
 				 var toDel = store_array.indexOf(store.id);
	 				if (toDel > -1) {
	 					store_array.splice(toDel,1); 
	 				}
			}
		}
		
		$scope.createGroupCategory = function(){
			
			console.log(store_array);
			if($scope.group_category.group_category_name == '' || $scope.group_category.group_category_name == null){
			}else{
				var jsonData = JSON.stringify({
					"group_category_name" : $scope.group_category.group_category_name,
					"stores": store_array
				});
							
				$http
				.post(
						'${pageContext.request.contextPath}/menu/group_category/create_group_category',
						jsonData)
				.then(
						function(response) {
							if(response.status == 200){
								$scope.resetModal();	
								$('#createGroupCategoryModal').modal('toggle');
								$scope.refreshGroupCategoryTable();
							}
							else if(response.status == 404){
								console.log(response.data.response_message);
							}
						},
						function(response) {
							alert("Session TIME OUT");
							$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
						});
			}
		}
		
		$scope.updateGroupCategory = function() {
			console.log('ori:' + ori_store_array.sort());
			console.log('now:' + store_array.sort());
		 	if($scope.group_category.group_category_name == '' || $scope.group_category.group_category_name == null){
			}else{
				
				var jsonData = JSON.stringify({
					"id" : $scope.group_category.id,
					"group_category_name" : $scope.group_category.group_category_name,
					"stores": store_array
				});
				
				console.log(jsonData);			
				
				//if(ori_store_array.sort().every(function(value, index) { console.log('here');return value === store_array.sort()[index]})){
				if (ori_store_array.every(function(value, index) { return store_array.includes(value)})){
					// only perform assign new stores
					$http
					.post(
							'${pageContext.request.contextPath}/menu/group_category/edit_group_category',
							jsonData)
					.then(
							function(response) {							
								$scope.resetModal();	
								$('#createGroupCategoryModal').modal('toggle');
								$scope.refreshGroupCategoryTable();		
							},
							function(response) {
								$scope.resetModal();	
								$('#createGroupCategoryModal').modal('toggle');
								
								if(response.status == 409)
									{
										alert(response.data.response_message);
									}
								else {
									alert("Session TIME OUT");
							 		$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
								}
					
							}); 
				}
				else{
					// got perform unassign store
					swal({
						  title: "Alert",
						  text: "Unassign store will deactivate all existing activated devices.\nDo you want to proceed?",
						  icon: "warning",
						  buttons: true,
						  dangerMode: true,
						})
						.then((willCreate) => {
						  if (willCreate) {
							  $http
								.post(
										'${pageContext.request.contextPath}/menu/group_category/edit_group_category',
										jsonData)
								.then(
										function(response) {							
											$scope.resetModal();	
											$('#createGroupCategoryModal').modal('toggle');
											$scope.refreshGroupCategoryTable();		
										},
										function(response) {
											$scope.resetModal();	
											$('#createGroupCategoryModal').modal('toggle');
											
											if(response.status == 409)
												{
													alert(response.data.response_message);
												}
											else {
												alert("Session TIME OUT");
										 		$(location)
												.attr('href',
														'${pageContext.request.contextPath}/user');
											}
								
										}); 
						}					 
					});	
				}
			
			} 
		}
		
		$scope.resetModal = function(){
			$scope.group_category = {};
			$scope.stores = [];
			$scope.action = "";
			ori_store_array = [];
			store_array = [];
			store_with_group_category_array = [];
		}
		
		$scope.refreshGroupCategoryTable = function(){
			var table = $('#groupCategory_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/group_category/get_all_group_category",
					"dataSrc": function ( json ) {                
		                return json;
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
					{"data" : "group_category_name"},
					{"data": "id", "width": "27%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						    return '<div class="d-flex justify-content-start"><a ng-href= "${pageContext.request.contextPath}/user/#!Router_group_category_category/'+id+'" class="btn btn-outline-info ml-1 mr-1 custom-fontsize"><i class="far fa-edit"></i> Edit Category</a><button class="btn btn-outline-danger ml-1 mr-1 custom-fontsize" ng-click="publishMenu('+id+')"><i class="far fa-upload"></i> Publish Menu</button></div>'			   
					 }
					}
					],
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			});
			
			$('#groupCategory_dtable tbody').off('click', 'tr td:nth-child(2)');
			$('#groupCategory_dtable tbody').on('click', 'tr td:nth-child(2)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/group_category/get_group_category_by_id?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find group category detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.group_category.id = response.data.id;
						$scope.group_category.group_category_name = response.data.group_category_name;
						$scope.action = 'update';
						$scope.getStore('update');
						//$('#createGroupCategoryModal').modal('toggle');
						$('#createGroupCategoryModal').modal({backdrop: 'static', keyboard: false});	
					}
				});
			});
		}
		
		$scope.getStore = function(operation_type){
			var store_url = '${pageContext.request.contextPath}/menu/group_category/';
			
			if(operation_type === 'create'){
				store_url += 'get_unassigned_store';
			} else if(operation_type === 'update'){
				store_url += 'get_all_store' + '?group_category_id='+ $scope.group_category.id;
			}
			
			console.log(store_url);

			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : store_url
			})
			.then(function(response) {
				if($scope.action === 'create'){
					console.log("Store List");
					console.log(response.data);
					$scope.stores = response.data;
				}
				else if($scope.action === 'update'){
					
					for(var i=0;i<response.data.length;i++){
						if(response.data[i].group_category_id != 0){
							ori_store_array.push(response.data[i].id);
							store_array.push(response.data[i].id);
						}
					}
					$scope.stores = response.data;
					//store_with_group_category_array = response.data;
					console.log("Existing Store with Group Category Id");
					console.log(store_array);
				}
			}, function(response){
				console.log(response.data)
				alert("Unknown Error Occured While Obtaining Store List");
			});
			
			store_url = '';
		}
		
		$scope.publishMenu = function(id){
			
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/group_category/publish_menu?group_category_id='+id	
			})
			.then(function(response) {
				console.log(response.data);
				swal("Successfully published menu", {
					icon: "success",
				});
			}, function(response){
				swal({
					  title: "Failed to publish menu",
					  text: response.data,
					  icon: "warning",
					  dangerMode: true,
					});
			});
		}

		
		
		
		

	});
</script>
</html>