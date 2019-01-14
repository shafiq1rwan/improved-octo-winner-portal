<html>
<script>
	app.controller('ctl_menu_item', function($scope, $http, $compile) {
		
		$scope.menu_item = {};
		$scope.action = '';
		$scope.menu_item_types = [];
		$scope.modifier_groups = [];
		$scope.tierItems = [];
		$scope.menu_item_id;
		$scope.tier_action = '';
		$scope.reordering_action = false; 
		$scope.extra_tier_items = [];
		$scope.upload_image = false;
		
		$scope.assinged_modifier_groups = [];
		
		// ==== Add, Modifier Tier Operations ====
		$scope.tier_item = {};
		
		$scope.addNewTier = function(){	
			$('#comboSettingModal').modal('toggle');
			$('#tierModal').modal({backdrop: 'static', keyboard: false});
			
			console.log("hry "+ $scope.menu_item_id);
		}

		$scope.createNewTier = function(){
			
			if($scope.tier_item.name == null 
					|| $scope.tier_item.quantity == null 
					|| $scope.tier_item.name == '' 
					|| $scope.tier_item.quantity == '') {
			} else {
				var json_data = JSON.stringify({
					'menu_item_id': $scope.menu_item_id,
					'combo_detail_name': $scope.tier_item.name,
					'combo_detail_quantity': $scope.tier_item.quantity
				});
				
				$http
				.post(
					'${pageContext.request.contextPath}/menu/combo/createComboDetail', json_data)
				.then(
					function(response) {	
						if(response.status == '200'){
							$scope.resetNewTierModal();
							$('#tierModal').modal('toggle');		
							$scope.openComboSetting($scope.menu_item_id);
						} else if(response.status == '400'){
							alert("Cannot Add Tier Item");
						}			
					},
					function(response) {
						alert("Cannot Add Tier Item");
				});
			}
		}
		
		$scope.resetNewTierModal = function(){
			$scope.tier_item = {};
			$scope.tier_action = '';
			
			//$('#tierModal').modal('toggle');
			//$('#comboSettingModal').modal('toggle');
		}
		
		$scope.openEditTier = function(action_type, id){
			$scope.tier_action = action_type;

		 	$http
			.get(
				'${pageContext.request.contextPath}/menu/combo/getComboDetailById?id='+ id)
			.then(
				function(response) {	
					if(response.status == '200'){		
						$scope.tier_item = response.data;
						
						$('#comboSettingModal').modal('toggle');
						$('#tierModal').modal('toggle');
						
					} else if(response.status == '400'){
						alert("Cannot Retrieve Existing Tier Data");
					}			
				},
				function(response) {
					alert("Cannot Retrieve Existing Tier Data");
			});	
		}
		
		$scope.editExistingTier = function(id){			
			if($scope.tier_item.quantity == null ||  $scope.tier_item.quantity == 0 ||  $scope.tier_item.quantity == ''){			
			} else {
				var json_data = JSON.stringify({
					'id':id,	
					'combo_detail_name': $scope.tier_item.name,
					'combo_detail_quantity': $scope.tier_item.quantity
				});
				
				$http
				.post(
					'${pageContext.request.contextPath}/menu/combo/editComboDetail', json_data)
				.then(
					function(response) {	
						if(response.status == '200'){
							$scope.resetNewTierModal();
							$('#tierModal').modal('toggle');		
							$scope.openComboSetting($scope.menu_item_id);
						} else if(response.status == '400'){
							alert("Cannot Edit Existing Tier Item");
						}			
					},
					function(response) {
						alert("Cannot Edit Existing Tier Item");
				});
			}
		}
		
		$scope.removeExistingTier = function(id){
			$http
			.delete(
				'${pageContext.request.contextPath}/menu/combo/deleteComboDetail?id='+id)
			.then(
				function(response) {	
					if(response.status == '200'){	
						$scope.openComboSetting($scope.menu_item_id);
					} else if(response.status == '400'){
						alert("Cannot Delete Tier Item");
					}			
				},
				function(response) {
					alert("Cannot Delete Existing Tier Item");
			});
		}
		
		//Reordering Operations for Tier
		$scope.reorderTierItems = function(){
			$scope.reordering_action = true;
			
			//copy the existing field
/* 			for(var i=0; i<$scope.tierItems.length;i++){
				$scope.extra_tier_items.push($scope.tierItems[i]);
			} */
		}
		
		$scope.saveReordering = function(){
			
			var json_data = JSON.stringify({
				'menu_item_id': $scope.menu_item_id,
				'tier_items' : $scope.tierItems
			});
			
			console.log(json_data);
			
	 		$http
			.post(
				'${pageContext.request.contextPath}/menu/combo/editComboDetailSequence', json_data)
			.then(
				function(response) {		
					$scope.extra_tier_items = [];
					$scope.reordering_action = false;	
			
					if(response.status == '200'){				
						$scope.openComboSetting($scope.menu_item_id);
					} else if(response.status == '400'){
						alert("Cannot Reorder Tier Item");
					}			
				},
				function(response) {
					$scope.extra_tier_items = [];
					$scope.reordering_action = false;
					alert("Cannot Reorder Tier Item");
			});
		}

		$scope.cancelReordering = function(){
			
	/* 		$scope.tierItems = [];
			for(var i=0; i<$scope.extra_tier_items.length;i++){
				$scope.tierItems.push($scope.extra_tier_items[i]);
			} */
			
			$scope.extra_tier_items = [];
			$scope.reordering_action = false;
			$scope.openComboSetting($scope.menu_item_id);
		}
		
			
/* 		 $scope.updateTier = function() {
			
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
			      console.log($scope.tierItems);

			}
			$scope.oldTierNumber = $scope.tierNumber;	
	    };  */
	    
	 	// open combo setting modal
		$scope.openComboSetting = function(menu_item_id){
			$scope.menu_item_id = menu_item_id;
			
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
					if(response.status == '200'){
						$scope.tierItems = response.data;
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
	    
	/*     $scope.createNewTier = function(){
	    	
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
	    } */
	    
	    
/*  	    function createTierData(tierItemList){	    	
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
					},
					function(response) {
						alert("Session TIME OUT");
						$(location)
								.attr('href',
										'${pageContext.request.contextPath}/user');
					}); 
	    }  */
	    
	/*     $scope.editExistingTier = function(){
	    	
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
	    	
	    } */

		$(document).ready(function() {					
			$scope.refreshItemMenuGroupTable();

			$("#modifierGroup").select2({
				  placeholder: 'Select an option',
				  selectOnClose: false,
				  width: '100%'
			}).on('select2:select', function(e){
/*  		 	      var id = e.params.data.id;
			      var option = $(e.target).children('[value='+id+']');
			      option.detach();
			      $(e.target).append(option).change(); */
			      
			     var json_data = {
					id : e.params.data.id,
					text : e.params.data.text
			     }

				$scope.assinged_modifier_groups.push(json_data);
			    $scope.$apply();
			}).on('select2:unselect', function(e){
			    
				var index = $scope.assinged_modifier_groups.map(x => {
					  return x.id;
				}).indexOf(e.params.data.id);

			    $scope.assinged_modifier_groups.splice(index, 1);
			    $scope.$apply();
			}).on("select2:selecting", function(e) { 
			}).on("change", function(e) { 
			});
			
			var el = document.getElementById("modifierGroupSequence");
			console.log("Hi Element : " + el);

			$('input[type=file]').change(function(event) {
				var element = event.target.id;			
				var _URL = window.URL || window.webkitURL;
				var file = this.files[0];
				fileCheck(file);	
			});
		    
			function fileCheck(file) {	
				if(file)
					{
				     var img = new Image(),
			        	msg, 
			        	errorFlag = false;
			        img.src = window.URL.createObjectURL(file);
			        img.onload = function() {
		            	var width = img.naturalWidth,
			                height = img.naturalHeight,
			                aspectRatio = width/height,
			                extension = $('#menuItemImage').val().split('.').pop();
		            	      
			        	if (['png', 'jpg', 'jpeg'].indexOf(extension) == -1) {
							msg = 'Make sure that the image is in png / jpg / jpeg format.';
							errorFlag = true;
					 	}		          
			        	else if (file.size > 150000) {
			        		msg = 'Image file must not exceed 150kb.';
			        		errorFlag = true;
						}		          
			        	else if(aspectRatio<1 || aspectRatio>1.5){
			        		msg = 'Make sure that the image aspect ratio is within 1:1 to 3:2.';
			        		errorFlag = true;
						}	
			        	else if(width < 300){
			        		msg = 'Make sure that the image has minimum width of 300px.';
			        		errorFlag = true;
						}
						else if(height < 200){
							msg = 'Make sure that the image has minimum height of 200px.';
							errorFlag = true;
						}
			          	
			        	if(errorFlag){        		
			        		$('#menuItemImage').val('');
			        		alert(msg);		        		
			        		return false;
			        	}
			        	
			          	// if successful validation
			        	var reader = new FileReader();
			        	reader.readAsDataURL(file); 
			        	reader.onloadend = function() {
				       	  	$scope.menu_item.image_path = reader.result;  
				        	$scope.$apply();
				        	$scope.upload_image = true;
			        	}		          
			        };
			        
			        img.onerror = function() {
			        	msg = 'Invalid image file.';			        	
			        	alert(msg);
						return false;
			        };
				}				
				
			}
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
			
			$scope.reordering_action = false; 
			$scope.extra_tier_items = [];
			
			$('#menuItemImage').val('');
			
			$scope.disableInputs = false;
			$scope.upload_image = false;
			
			$("#modifierGroup").val([]).trigger("change");
			console.log("End");
			console.log($("#modifierGroup").val());
			
			$scope.assinged_modifier_groups = [];
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
							 		return '<div class="btn-group"><a ng-href="${pageContext.request.contextPath}/user/#!Router_assign_modifier/'+ id +'" class="btn btn-info p-1 custom-fontsize"><b><i class="fa fa-bars"></i> Modifier Groups</b></a></div>';	 
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
				    }/* ,
				    "drawCallback": function( settings ) {
				    	 var api = this.api();
				    } */
			});
			
			$('#menuItem_dtable tbody').off('click', 'tr td:nth-child(-n+5)');
			$('#menuItem_dtable tbody').on('click', 'tr td:nth-child(-n+5)', function() {
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
						$scope.menu_item.backend_id = response.data.backend_id;
						$scope.menu_item.menu_item_name = response.data.menu_item_name;
						$scope.menu_item.menu_item_description = response.data.menu_item_description;
						$scope.menu_item.image_path = "${pageContext.request.contextPath}" + response.data.menu_item_image_path;
						$scope.menu_item.menu_item_base_price = response.data.menu_item_base_price;
						$scope.menu_item.menu_item_type = response.data.menu_item_type;
						$scope.menu_item.is_taxable = response.data.is_taxable;			
						$scope.menu_item.is_discountable = response.data.is_discountable;
						$scope.menu_item.is_active = response.data.is_active;
						$scope.action = 'update';
						$scope.disableInputs = ($scope.menu_item.menu_item_type == 2);
						
						var el = document.getElementById("modifierGroupSequence");
						console.log("Hi Element : " + el);
						
						//getAssignedModifierGroup(response.data.id);
						
						console.log($scope.menu_item);
						$('#createMenuItemModal').modal('toggle');

					}
				});
			});
		}
		
		function getAssignedModifierGroup(menu_item_id){
			$http
			.get(
				'${pageContext.request.contextPath}/menu/modifier_group/get_assigne_modifier_groups_by_item_id?menuItemId='+menu_item_id)
			.then(
				function(response) {		
					//$scope.modifier_groups = response.data;
					
			 	 	for(var i=0;i<response.data.length;i++){
				 		//$scope.assinged_modifier_groups[i] = $scope.modifier_groups[i].id;
				 		if(response.data[i].sequence_id != 9999){
				 			$scope.assinged_modifier_groups[i] = response.data[i].id;
				 		}
					} 
				 	console.log($scope.assinged_modifier_groups.map(String));
					$("#modifierGroup").val($scope.assinged_modifier_groups.map(String)).trigger("change");

					console.log("Ge: " + $scope.modifier_groups);
				},
				function(response) {
					console.log("Cannot Retrive Modifier Group!");
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
			
	 		console.log("Selected List");
	 		console.log($("#modifierGroup").val()); 
	 		
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
					"menu_item_name": $scope.menu_item.menu_item_name,
					"menu_item_backend_id" : $scope.menu_item.backend_id,
					"menu_item_description": $scope.menu_item.menu_item_description || null,
					"menu_item_image_path": $scope.upload_image?$scope.menu_item.image_path: null,
					"menu_item_base_price": $scope.menu_item.menu_item_base_price || 0.00,
					"menu_item_type": $scope.menu_item.menu_item_type || 0,
					"is_taxable" : $scope.menu_item.is_taxable || false,
					"is_discountable": $scope.menu_item.is_discountable || false,
					"assigned_modifier_group" : $("#modifierGroup").val()
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