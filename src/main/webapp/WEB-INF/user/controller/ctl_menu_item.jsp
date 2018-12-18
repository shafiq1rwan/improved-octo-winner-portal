<html>
<script>
	app.controller('ctl_menu_item', function($scope, $http, $compile) {
		
		$scope.menu_item = {};
		$scope.action = '';
		$scope.menu_item_types = [];
		$scope.modifier_groups = [];
		
		$(document).ready(function() {
			$scope.refreshItemMenuGroupTable();
		});
		
		$scope.setModalType = function(action_type){
			$scope.action = action_type;
		}
		
		$scope.resetModal = function(){
			$scope.menu_item = {};
			$scope.action = '';
		}
		
		//get menu item type
		$http
		.get(
			'${pageContext.request.contextPath}/menu/menuItem/getMenuItemType')
		.then(
			function(response) {			
			/* 		$scope.menu_item_types = response.data.filter(function (el) {
						  return el.menu_item_type_id != 1;
					}); */	
				$scope.menu_item_types = response.data;
			},
			function(response) {
				alert("Cannot Retrive Menu Item Type!");
		});
		
		//get modifier group
		$http
		.get(
			'${pageContext.request.contextPath}/menu/modifier_group/get_all_modifier_group')
		.then(
			function(response) {			
				$scope.modifier_groups = response.data;
				console.log($scope.modifier_groups);
			},
			function(response) {
				alert("Cannot Retrive Modifier Group!");
		});
		
				
		$scope.refreshItemMenuGroupTable = function(){
			
			var table = $('#menuItem_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/menuItem/getAllMenuItem",
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
					{"data" : "backend_id"},
					{"data" : "menu_item_name"},
					{"data" : "menu_item_image_path", "defaultContent": "<i>Not set</i>"},
					{"data" : "menu_item_base_price"},
					{"data" : "menu_item_type", 
					 "render": function(data, type, full, meta){
						 var menu_item_type = full.menu_item_type;
						 var result = $scope.menu_item_types.find(obj => obj.menu_item_type_id === menu_item_type);
						 return result.menu_item_type_name;
					 }
					},		
					{"data" : "is_taxable", 
					 "render": function(data, type, full, meta){
						 	var is_taxable = full.is_taxable;
						 	if(is_taxable == 1)
						 		return 'Yes';
						 	else if(is_taxable == 0)
						 		return 'No';  
					 }},
					{"data" : "is_discountable", 
					 "render": function(data, type, full, meta){
						 	var is_discountable = full.is_discountable;
						 	if(is_discountable == 1)
						 		return 'Yes';
						 	else if(is_discountable == 0)
						 		return 'No';  
					 }},		
					{"data": "id", "width": "20%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						 	var menu_item_type = full.menu_item_type;
						 	switch(menu_item_type){
							 	case 0:
							 	case 2:
							 	 	return '<div class="btn-toolbar justify-content-between"><button ng-click="removeMenuItem('+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>';	 
							 		break;
							 	case 1:
							 		return '<div class="btn-toolbar justify-content-between"><button ng-click="removeMenuItem('+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button><button ng-click="" class="btn btn-primary custom-fontsize"><b><i class="fa fa-bars"></i>Combo</b></button></div>';	
						 	}
					 }
					}
					],
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			});
			
			$('#menuItem_dtable tbody').off('click', 'tr td:nth-child(-n+8)');
			$('#menuItem_dtable tbody').on('click', 'tr td:nth-child(-n+8)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/menuItem/getMenuItemById?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find menu group detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.menu_item.id = response.data.id;
						$scope.menu_item.modifier_group_id = (response.data.modifier_group_id === 0)?undefined: response.data.modifier_group_id;
						$scope.menu_item.backend_id = response.data.backend_id;
						$scope.menu_item.menu_item_name = response.data.menu_item_name;
						$scope.menu_item.menu_item_description = response.data.menu_item_description;
						$scope.menu_item.menu_item_image_path = response.data.menu_item_image_path;
						$scope.menu_item.menu_item_base_price = response.data.menu_item_base_price;
						$scope.menu_item.menu_item_type = response.data.menu_item_type;
						$scope.menu_item.is_taxable = response.data.is_taxable;			
						$scope.menu_item.is_discountable = response.data.is_discountable;
						$scope.menu_item.is_active = response.data.is_active;	
						$scope.action = 'update';
						$scope.disableInputs = ($scope.menu_item.menu_item_type == 2);
						
						console.log($scope.menu_item);
						$('#createMenuItemModal').modal('toggle');
					}
				});
			});
		}
		
	 	$scope.performMenuItemOperations = function(action_type){	
			if($scope.menu_item.menu_item_name == '' || $scope.menu_item.menu_item_name == null || 
					$scope.menu_item.menu_item_description == '' || $scope.menu_item.menu_item_description == null ||
					$scope.menu_item.menu_item_base_price == '' || $scope.menu_item.menu_item_base_price == null){
			} else{			
				var menu_item_url = '${pageContext.request.contextPath}/menu/menuItem/';
				
				if(action_type === 'create')
					menu_item_url += 'createMenuItem';
				else if(action_type === 'update')
					menu_item_url += 'editMenuItem';
				
				if($scope.menu_item.menu_item_type === 2){
					$scope.menu_item.is_taxable = false;
					$scope.menu_item.is_discountable = false;
				}
				
				var json_data = JSON.stringify({
					"id": $scope.menu_item.id || null,
					"modifier_group_id" : $scope.menu_item.modifier_group_id || 0,
					"menu_item_name": $scope.menu_item.menu_item_name,
					"menu_item_description": $scope.menu_item.menu_item_description,
					"menu_item_image_path": $scope.menu_item.menu_item_image_path || null,
					"menu_item_base_price": $scope.menu_item.menu_item_base_price || 0.00,
					"menu_item_type": $scope.menu_item.menu_item_type || 0,
					"is_taxable" : $scope.menu_item.is_taxable || false,
					"is_discountable": $scope.menu_item.is_discountable || false
				});
				
				console.log(json_data);
								
				$http
				.post(
						menu_item_url, json_data)
				.then(
						function(response) {			
							$scope.resetModal();	
							$('#createMenuItemModal').modal('toggle');
							
							if(response.status == 200)
								$scope.refreshItemMenuGroupTable();
							else if(response.status == 409)
								alert("Duplication Found");
							else if(response.status == 404)
								alert("Menu Item Id not Found!");
							else if(resposne.status == 400)
								alert(response.data);
						},
						function(response) {
							alert("Session TIME OUT");
							$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
						});

			}
		} 
		
		
		
		
		$scope.removeMenuItem = function(id){
			$http
			.delete(
				'${pageContext.request.contextPath}/menu/menuItem/deleteMenuItem?id='+id)
			.then(
				function(response) {			
					if(response.status == 200){
						$scope.refreshItemMenuGroupTable();
					} else if(response.status == 400){
						alert("Cannot Remove menu Item");
					}
				},
				function(response) {
					alert("Session TIME OUT");
					$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user');
			});
		}
		
		$scope.addComboDetail = function(){
			
			
			
			
		}
		
	});
</script>
</html>