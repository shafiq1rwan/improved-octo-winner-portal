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
		$scope.oldTierItems = [];
		$scope.menu_item_id;
		$scope.tier_action = '';
			
		$scope.updateTier = function() {
	/* 		console.log('old: ' + $scope.oldTierNumber);
			console.log('new: ' + $scope.tierNumber); */
			
			if($scope.tierNumber==0 || $scope.tierNumber=='' || $scope.tierNumber==null){
				alert('Tier Number cannot be zero');
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
				    			  order: a,
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
					
					$('#switch').show();
					$("#switchToggle").prop('checked', false);
					
			     /*  for(var a=0; a< count; a++){
			    	  var item = {
			    			  name:"test"+a,
			    			  order: a
			    	  }
			    	  $scope.tierItems.push(item);
			      } */
			      console.log($scope.tierItems);

		      // drag and drop list
		      var oldContainer;
		      $("#sortableList").sortable({
		        group: 'nested',
		        handle: 'i.fa-reorder',
		        pullPlaceholder: false,
		        // animation on drop
		        onDrop: function  ($item, container, _super) {
		        	container.el.removeClass("active");	         
		        	
		          var $clonedItem = $('<li/>').css({height: 0});
		          $item.before($clonedItem);
		          $clonedItem.animate({'height': $item.height()});
	
		          $item.animate($clonedItem.position(), function  () {
		            $clonedItem.detach();
		            _super($item, container);
		          });     	
		        },
	
		        // set $item relative to cursor position
		        onDragStart: function ($item, container, _super) {
		          var offset = $item.offset(),
		              pointer = container.rootGroup.pointer;
	
		          adjustment = {
		            left: pointer.left - offset.left,
		            top: pointer.top - offset.top
		          };
	
		          _super($item, container);
		        },
		        afterMove: function (placeholder, container) {
			          if(oldContainer != container){
			            if(oldContainer)
			              oldContainer.el.removeClass("active");
			            container.el.addClass("active");
	
			            oldContainer = container;
			          }        
			        },
		        onDrag: function ($item, position) {	        	
		          $item.css({
		            left: position.left - adjustment.left,
		            top: position.top - adjustment.top
		          });
		        }
		      });      
		   		// end drag and drop list	
			}
	      	
			$scope.oldTierNumber = $scope.tierNumber;
			
	    };
	    
	    $scope.createNewTier = function(){
	    	var listItems = $("#sortableList li");
	    	var tierItemList = [];
	    	listItems.each(function(idx, li) {
	    	    var product = $(li);
				console.log(product.attr('id'));
			
	    	    // and the rest of your code
	    	    var tierItem = {
	    	    		"menu_item_id":$scope.menu_item_id,
	    	    		"combo_detail_name": $scope.tierItems[idx].name,
	    	    		"combo_detail_quantity": $scope.tierItems[idx].quantity,
						"combo_detail_sequence": product.attr('id')
	    	    }; 	    
	    	    console.log(tierItem);    	    
	    	    tierItemList.push(tierItem);  
	    	});    	
	    	createTierData(tierItemList);
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
	    		'newTierItems': $scope.tierItems,
	    		'oldTierItems': $scope.oldTierItems
	    	});
	    	
	    	console.log(json_data);
	    	
	      	$http
			.post(
				'${pageContext.request.contextPath}/menu/combo/editComboDetail', json_data)
			.then(
					function(response) {			
						$scope.resetModal();	
						$('#comboSettingModal').modal('toggle');

				 		if(response.status == 400)
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
			
			$("#switchToggle").on("click", function  (e) { 
		    	  var method;
		    	  if($(this).prop( "checked" )){
		    		  method = "enable";
		    		  $( "#sortableList").find( "i.fa-reorder" ).show("fast");
		    	  }
		    	  else{
		    		  method = "disable";
		    		  $( "#sortableList").find( "i.fa-reorder" ).hide("fast");
		    	  }
		    	  
		    	  $("#sortableList").sortable(method);
		      });
			
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
			$scope.oldTierItems = [];
			
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
		
		// open combo setting modal
		$scope.openComboSetting = function(menu_item_id){
			$scope.menu_item_id = menu_item_id;
			var i =0;
			
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
						
						for(i=0;i<response.data.length;i++){
							$scope.oldTierItems.push(response.data[i]);
						}		
						
/* 						$('#switch').show();
						$("#switchToggle").prop('checked', false); */
					} else {
						$scope.tier_action = 'create';	
						//$scope.oldTierNumber = $scope.tierNumber;
					}
				},
				function(response) {
					alert("Cannot Retrive Combo Detail!");
			});

			$('#comboSettingModal').modal('toggle');
		}
				
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
					{"data" : null, "width" : "5%", 
					 "render": function(data, type, full, meta){
						 return '<div class="input-group"><label class="switch "><input type="checkbox" class="info"><span class="slider round"></span></label></div>';
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
		
		
	});
</script>
</html>