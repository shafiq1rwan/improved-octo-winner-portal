<html>
<script>
	app.controller('ctl_group_category', function($scope, $http, $compile) {
	
		$scope.group_category = {};
		$scope.stores = [];
		$scope.action = "";
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
			
			console.log(store_array);
		 	if($scope.group_category.group_category_name == '' || $scope.group_category.group_category_name == null){
			}else{
				
				var jsonData = JSON.stringify({
					"id" : $scope.group_category.id,
					"group_category_name" : $scope.group_category.group_category_name,
					"stores": store_array
				});
				
				console.log(jsonData);
							
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
		}
		
		$scope.resetModal = function(){
			$scope.group_category = {};
			$scope.stores = [];
			$scope.action = "";
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
					{"data": "id", "width": "25%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						    return '<div class="btn-toolbar justify-content-between"><a ng-href= "${pageContext.request.contextPath}/user/#!Router_group_category_category/'+id+'" class="btn btn-outline-info border-0 p-0 custom-fontsize"><b><i class="fa fa-edit"></i> Edit Categories</b></a></div>'			   
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
						$('#createGroupCategoryModal').modal('toggle');
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
		

		
		
		
		

	});
</script>
</html>