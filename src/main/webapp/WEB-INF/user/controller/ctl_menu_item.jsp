<html>
<script>
	app.controller('ctl_menu_item', function($scope, $http, $compile) {
		
		$scope.menu_item = {};
		$scope.action = '';
		$scope.menu_item_types = [];
		$scope.modifier_groups = [];
		$scope.tierNumber;
		$scope.oldTierNumber=0;
		$scope.tierItems = [];
		$scope.menu_item_id;
		$scope.tier_action = '';
		
		$scope.addNewTier = function(){
			
			
			
			
			
		}
		
		
		
		
			
		$scope.updateTier = function() {
			
			if($scope.tierNumber==null || $scope.tierNumber==0 || $scope.tierNumber==''){
				alert('Tier Number cannot be zero');
				$scope.tierNumber = 0;
		 		$scope.tierItems = [];
				$scope.oldTierNumber = 0;
			}
			else{	
				var count = 0;
				if($scope.tierNumber>$scope.oldTierNumber){
					count= $scope.tierNumber - $scope.oldTierNumber;
					for(var a=$scope.oldTierNumber; a< $scope.oldTierNumber+count; a++){
				    	  var item = {
				    			  id : null,
				    			  name:"test"+a,
				    			  quantity: 0
				    	  }
				    	  $scope.tierItems.push(item);
				    }
				}
				else if($scope.tierNumber < $scope.oldTierNumber){
					count = $scope.oldTierNumber - $scope.tierNumber;
					for(var a=0; a< count; a++){
				    	  $scope.tierItems.splice($scope.tierNumber, count);
				    }
				}
					//$scope.tierItems = [];
					
/* 					$('#switch').show();
					$("#switchToggle").prop('checked', false); */
					
			     /*  for(var a=0; a< count; a++){
			    	  var item = {
			    			  name:"test"+a,
			    			  order: a
			    	  }
			    	  $scope.tierItems.push(item);
			      } */
			      console.log($scope.tierItems);

			}
			$scope.oldTierNumber = $scope.tierNumber;	
	    };
	    
	 	// open combo setting modal
		$scope.openComboSetting = function(menu_item_id){
			$scope.menu_item_id = menu_item_id;
			var i =0;
			
		     var sortable = Sortable.create($('#sortableList')[0], {
			   		handle: ".fa-reorder",
			   		scroll: true,
			   		// Element is chosen
			   		onChoose: function (/**Event*/evt) {
	
			   		},
			   		// Element dragging started
			   		onStart: function (/**Event*/evt) {
	
			   		},
			   		// Element dragging ended
			   		onEnd: function (evt) {
			   			move($scope.tierItems,evt.oldIndex,evt.newIndex);
			   			console.log("My List: ");
			   			console.log($scope.tierItems);
			   		}
		   	});
			
			$http
			.get(
				'${pageContext.request.contextPath}/menu/combo/getComboDetailByMenuItemId?menuItemId='+ menu_item_id)
			.then(
				function(response) {			
					if(response.data.length>0){
						$scope.tier_action = 'update',
						$scope.tierNumber = response.data.length;
						$scope.oldTierNumber = $scope.tierNumber;
						$scope.tierItems = response.data;
					} else {
						$scope.tier_action = 'create';	
						//$scope.oldTierNumber = $scope.tierNumber;
					}
				},
				function(response) {
					alert("Cannot Retrive Combo Detail!");
			});

			$('#comboSettingModal').modal({backdrop: 'static', keyboard: false});
		}
	    
		function move(arr, old_index, new_index) {
		    while (old_index < 0) {
		        old_index += arr.length;
		    }
		    while (new_index < 0) {
		        new_index += arr.length;
		    }
		    if (new_index >= arr.length) {
		        var k = new_index - arr.length;
		        while ((k--) + 1) {
		            arr.push(undefined);
		        }
		    }
		     arr.splice(new_index, 0, arr.splice(old_index, 1)[0]); 
		}
	    
	    $scope.createNewTier = function(){
	    	
	    	if($scope.tierNumber == null || $scope.tierNumber == '' || $scope.tierNumber <= 0){

	    	} else {
	    		var listItems = $("#sortableList li");
		    	var tierItemList = [];
		    	listItems.each(function(idx, li) {
		    	    var product = $(li);
					console.log(product.attr('id'));
				
		    	    // and the rest of your code
		    	    var tierItem = {
		    	    		"menu_item_id":$scope.menu_item_id,
		    	    		"combo_detail_name": $scope.tierItems[idx].name,
		    	    		"combo_detail_quantity": $scope.tierItems[idx].quantity
		    	    }; 	    
		    	    console.log(tierItem);    	    
		    	    tierItemList.push(tierItem);  
		    	});    	
		    	
		    	if(tierItemList.length >0){
			    	createTierData(tierItemList);
		    	} else {
		    		alert("Please fill in Tier Info");
		    	}

	    	}
	    }
	    
	    
	    function createTierData(tierItemList){	    	
	    	var json_data = JSON.stringify(
	    			tierItemList
			);
			
			console.log(json_data);
							
		 	$http
			.post(
				'${pageContext.request.contextPath}/menu/combo/createComboDetail', json_data)
			.then(
					function(response) {			
						$scope.resetModal();	
						$('#comboSettingModal').modal('toggle');

				/* 		if(response.status == 400)
							alert(response.data); */
					},
					function(response) {
						alert("Session TIME OUT");
						$(location)
								.attr('href',
										'${pageContext.request.contextPath}/user');
					}); 
	    }
	    
	    $scope.editExistingTier = function(){
	    	
 	    	var json_data = JSON.stringify({
	    		'menu_item_id': $scope.menu_item_id,
	    		'tier_items': $scope.tierItems
	    	}); 
	    		    	
	    	console.log(json_data);
	    	
	      	$http
			.post(
				'${pageContext.request.contextPath}/menu/combo/editComboDetail', json_data)
			.then(
					function(response) {			
						$scope.resetModal();	
						$('#comboSettingModal').modal('toggle');

				 		if(response.status == '400')
							alert(response.data);
					},
					function(response) {
						alert("Session TIME OUT");
						$(location)
								.attr('href',
										'${pageContext.request.contextPath}/user');
					}); 
	    	
	    }

		$(document).ready(function() {					
			$scope.refreshItemMenuGroupTable();
		});
		
		$scope.setModalType = function(action_type){
			$scope.action = action_type;
		}
		
		$scope.resetModal = function(){
			$scope.menu_item = {};
			
			$scope.tierNumber = undefined;
			$scope.oldTierNumber = 0;
			
			$scope.tierItems = [];
			
			$scope.menu_item_id = undefined;
			$scope.action = '';
			
			console.log($scope.tierNumber);
			//$scope.tier_action = '';
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
					{"data" : "menu_item_base_price", 
						"render": function(data, type, full, meta){
							return parseFloat(data).toFixed(2);
						}
					},
					{"data" : "menu_item_type", 
					 "render": function(data, type, full, meta){
						 var menu_item_type = full.menu_item_type;
						 var result = $scope.menu_item_types.find(obj => obj.menu_item_type_id === menu_item_type);
						 return result.menu_item_type_name;
					 }
					},
					{"data" : "is_active", "width" : "5%", 
					 "render": function(data, type, full, meta){		 
						 var id = full.id;				 
			 		  	 if(full.is_active)
			 				$('.switch>#'+id).prop("checked", true);
				 		 else
					 		$('.switch>#'+id).prop("checked", false);
		 
						 return '<div class="input-group"><label class="switch"><input ng-click="updateMenuItemStatus('+ full.is_active +','+id +')" type="checkbox" class="info" id="'+ id +'"><span class="slider round"></span></label></div>';
					 } 
					},		
					{"data": "id", "width": "20%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						 	var menu_item_type = full.menu_item_type;
						 	switch(menu_item_type){
							 	case 0:
							 	case 2:
							 	 	return '';	 
							 		break;
							 	case 1:
							 		return '<div class="btn-group"><button ng-click="openComboSetting('+ id +')" class="btn btn-info p-1 custom-fontsize"><b><i class="fa fa-edit"></i> Combo Setting</b></button><a ng-href="${pageContext.request.contextPath}/user/#!Router_combo/'+ id +'" class="btn btn-danger p-1 custom-fontsize"><b><i class="fa fa-bars"></i> Manage Tier</b></a></div>';	
						 	}
					 }
					}
					],
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			});
			
			$('#menuItem_dtable tbody').off('click', 'tr td:nth-child(-n+6)');
			$('#menuItem_dtable tbody').on('click', 'tr td:nth-child(-n+6)', function() {
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
		
		$scope.updateMenuItemStatus = function(status, id){
			
			var reply = confirm("Do you want to change the menu item status ?");
			
			if(reply){	
				var post_data = JSON.stringify({
					'id': id,
					'active_status' : status
				});
				
				$http
				.post(
					'${pageContext.request.contextPath}/menu/menuItem/updateMenuItemActiveStatus',post_data)
				.then(
					function(response) {
						
						if(response.status == '200'){
							$scope.refreshItemMenuGroupTable();
						}
						else if(response.status == '404'){
							alert('Missing data for updating status!');
							$('.switch>#'+id).prop("checked", status);
						}
						else if(response.status == '400'){
							alert('Fail to change the menu item status!');
							$('.switch>#'+id).prop("checked", status);
						}
					},
					function(response) {
						alert('Fail to change the menu item status!');
						$('.switch>#'+id).prop("checked", status);
				}); 
			} else {
				$('.switch>#'+id).prop("checked", status);
			}
		}
		
	 	$scope.performMenuItemOperations = function(action_type){	
			if($scope.menu_item.menu_item_name == null || 
					$scope.menu_item.menu_item_base_price == null ||
					$scope.menu_item.backend_id == null ||
					$scope.menu_item.menu_item_name == '' ||
					$scope.menu_item.menu_item_base_price < 0 || $scope.menu_item.backend_id == ''){
			} else{			
				var menu_item_url = '${pageContext.request.contextPath}/menu/menuItem/';
				
				if(action_type === 'create')
					menu_item_url += 'createMenuItem';
				else if(action_type === 'update')
					menu_item_url += 'editMenuItem';
				
				if($scope.menu_item.menu_item_type === 2){
					$scope.menu_item.is_taxable = false;
					//$scope.menu_item.is_discountable = false;
				}
				
				var json_data = JSON.stringify({
					"id": $scope.menu_item.id || null,
					"modifier_group_id" : $scope.menu_item.modifier_group_id || 0,
					"menu_item_name": $scope.menu_item.menu_item_name,
					"menu_item_backend_id" : $scope.menu_item.backend_id,
					"menu_item_description": $scope.menu_item.menu_item_description || null,
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
		
		
	});
</script>
</html>