<html>
<script>
	app.controller('ctl_category', function($scope, $http, $routeParams, $compile) {
		
		$scope.category = {};
		$scope.action = '';
		$scope.category_menu_item = [];
		
		$scope.itemList = [];
		$scope.selectedItemList = [];
		$scope.assign_item_action = '';
		
		$scope.filterList = [{id:1, name:'Filter by Name'},
			{id:2, name:'Filter by Backend ID'}]
		
		$scope.filterSelected = {id: 1};
		
		var group_category_id = $routeParams.id;
		
		$(document).ready(function() {		
			$scope.refreshCategoryTable();
		});
		
		$scope.setModalType = function(action_type){
			$scope.action = action_type;
		}
		
		$scope.createCategory = function(){			
			
			if($scope.category.category_name == '' || $scope.category.category_name == null){
				
			} else {

				var postdata = JSON.stringify ({
					group_category_id : group_category_id,
					category_name : $scope.category.category_name,
					category_description : $scope.category.category_description || null,
					category_image_path : $scope.category.category_image_path || null,
					is_active : $scope.category.is_active
				});

				console.log(postdata);
				
				 $http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/category/create_category',
					data : postdata
				})
				.then(function(response) {
						$scope.resetModal();
						$('#createCategoryModal').modal('toggle');
						$scope.refreshCategoryTable();
				}, function(response) {
						$scope.resetModal();
						$('#createCategoryModal').modal('toggle');
						
						if(response.status == 409){
							alert("Session TIME OUT");
							$(location).attr('href','${pageContext.request.contextPath}/admin');	
						}
				});
			}

		}

		
		$scope.refreshCategoryTable = function(){		
			var table = $('#category_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/category/get_all_category_by_group_category_id?group_category_id="+ group_category_id,
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
					{"data" : "backend_id", "width": "15%"},
					{"data" : "category_name"},
					{"data" : "category_image_path", "defaultContent": "<i>Not set</i>"},
					{"data" : "is_active", "width": "10%", 
						"render": function(data, type, full, meta ){
							var status = full.is_active;
							if(status)
								return 'Yes';
							 else 
								return 'No';		
						}
					},
					{"data": "id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button ng-click="removeCategory('+ id +')" type="button" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button><button ng-click="getCategoryMenuItem('+ id +')" type="button" data-toggle="modal" data-target="#menuItemModal" class="btn btn-default custom-fontsize"><b><i class="fa fa-trash"></i>Items</b></button></div>'	
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);  //add this to compile the DOM
				    }
				
			});
			
			$('#category_dtable tbody').off('click', 'tr td:nth-child(-n+4)');
			$('#category_dtable tbody').on('click', 'tr td:nth-child(-n+4)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/category/get_category_by_id?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find category detail");
					} else if(response.status == "200") {
						//alert(JSON.stringify(response));
						$scope.category.id = response.data.id;
						$scope.category.category_name = response.data.category_name;
						$scope.category.category_description = response.data.category_description;
						$scope.category.category_image_path = response.data.category_image_path;
						$scope.category.is_active = response.data.is_active;
						$scope.action = 'update';
						$('#createCategoryModal').modal('toggle');
					}
				});
			});
			
			
			
		}
		
		$scope.updateCategory = function(){
			if($scope.category.category_name == '' || $scope.category.category_name == null){	
			} else {
				var postdata = JSON.stringify ({
					id : $scope.category.id,
					category_name : $scope.category.category_name,
					category_description : $scope.category.category_description || null,
					category_image_path : $scope.category.category_image_path || null,
					is_active : $scope.category.is_active
				});
				
				console.log(postdata);
				
				 $http({
						method : 'POST',
						headers : {'Content-Type' : 'application/json'},
						url : '${pageContext.request.contextPath}/menu/category/edit_category',
						data : postdata
					})
					.then(function(response) {
							$scope.resetModal();
							$('#createCategoryModal').modal('toggle');
							$scope.refreshCategoryTable(); 
					}, function(response) {
							$scope.resetModal();
							$('#createCategoryModal').modal('toggle');
							
							if(response.status == 409){
								alert("Session TIME OUT");
								$(location).attr('href','${pageContext.request.contextPath}/admin');	
							}
					});
			}
		}
		
		$scope.removeCategory = function(id){		
			$http
			.delete(
					'${pageContext.request.contextPath}/menu/category/delete_category?id='+id)
			.then(
					function(response) {
							$scope.refreshCategoryTable();
					},
					function(response) {									
						if(resposne.status == 400){
							alert(response.data.response_message);
						} else {
							alert("Session TIME OUT");
							$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user');
						}
					});
		}
		
		$scope.resetModal = function(){
			$scope.category = {};
			$scope.action = '';
			$scope.category_menu_item = [];
		}
		
		$scope.submitAssignItems = function(){	
			for(var a=0; a<$scope.itemList.length; a++){
				if($scope.itemList[a].check){
					$scope.selectedItemList.push($scope.itemList[a]);
				}
			}
			
			var json_data = JSON.stringify({
					'item_list' : $scope.selectedItemList
			});
			
			console.log(json_data);
			
			// assign menu item			
		/* 	$http
			.post(
					'${pageContext.request.contextPath}/menu//', json_data)
			.then(
					function(response) {
						if(response.status == 200){				
							alert("Successfully assigned menu items.")
							$scope.getCategoryMenuItem();
							$scope.closeAssignItemsModal();
						}
					},
					function(response) {									
						if(response.status == 400){
							alert("Unable to assign menu item.");
						} else {
							console.log(response.data);
							alert("Session TIME OUT");
			 				$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user');
						}
					}); */
			
		}
		
		$scope.openAssignItemsModal = function(category_id){
			$('#menuItemModal').modal('toggle');
			$('#assignItemsModal').modal({backdrop: 'static', keyboard: false});
			
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : ($scope.assign_item_action === 'new'? '${pageContext.request.contextPath}/menu/menuItem/getAllMenuItemByType?menuItemType=3':'${pageContext.request.contextPath}/menu/menuItem/getMenuItemWithoutDuplication?categoryId=' + category_id)
			})
			.then(function(response) {
				if(response.status == "200") {
					$scope.itemList = response.data;
				}
				else {
					alert("Cannot Retrieve Item List");
				}
			});
		}
		
		$scope.closeAssignItemsModal = function(){	
			$('#menuItemModal').modal('toggle');
			$('#assignItemsModal').modal('toggle');
			
			$scope.filterSelected = {id: 1};
			$scope.itemList = [];
			$scope.selectedItemList = [];
			$scope.assign_item_action = '';
		}
		
		$scope.selectCard = function(item){
			if(item.check){
				item.check=false;
			}
			else{
				item.check=true;
			}
		}
	
		$scope.getCategoryMenuItem = function(category_id){
			
			console.log(category_id);
			
			$http
			.get(
					'${pageContext.request.contextPath}/menu/menuItem/getMenuItemByCategory?categoryId='+ category_id)
			.then(
					function(response) {
						console.log(response.data);
						if(response.status == 200){
							if(response.data.length == 0){
								$scope.assign_item_action = 'new';
							} else {
								$scope.category_menu_item = response.data;	
							}
						}
					},
					function(response) {									
						if(response.status == 400){
							alert(response.data.response_message);
						} else {
							console.log(response.data);
							alert("Session TIME OUT");
			 				$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user');
						}
					});
		}
		
		// search input on change
		$('#searchbox-input').on('input',function(e){
		    var filter = $(this).val(); // get the value of the input, which we filter on
		   	if($scope.filterSelected.id==1){
			    $('.card-container').find(".card-title:not(:contains(" + filter + "))").parentsUntil('.card-container').css('display','none');
			    $('.card-container').find(".card-title:contains(" + filter + ")").parentsUntil('.card-container').css('display','block');
		   	}
		   	else if($scope.filterSelected.id==2){
		   		$('.card-container').find(".text-info:not(:contains(" + filter + "))").parentsUntil('.card-container').css('display','none');
			    $('.card-container').find(".text-info:contains(" + filter + ")").parentsUntil('.card-container').css('display','block');
		   	}
		});
		
		// filter type on change
		 $scope.filterOnChange = function ($event){	         
	            var filter = $('#searchbox-input').val();
	            if($scope.filterSelected.id==1){
				    $('.card-container').find(".card-title:not(:contains(" + filter + "))").parentsUntil('.card-container').css('display','none');
				    $('.card-container').find(".card-title:contains(" + filter + ")").parentsUntil('.card-container').css('display','block');
			   	}
			   	else if($scope.filterSelected.id==2){
			   		$('.card-container').find(".text-info:not(:contains(" + filter + "))").parentsUntil('.card-container').css('display','none');
				    $('.card-container').find(".text-info:contains(" + filter + ")").parentsUntil('.card-container').css('display','block');
			   	}
		}		
		/* End assign items modal */
		
		
	});
</script>
</html>