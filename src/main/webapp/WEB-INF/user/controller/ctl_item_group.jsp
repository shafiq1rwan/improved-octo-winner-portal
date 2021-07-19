<html>
<script>
	app.controller('ctl_item_group', function($scope, $http, $compile) {
		$scope.menu_item_group = {};
		$scope.action = '';
		
		$(document).ready(function() {
			$scope.refreshItemGroupTable();
		});
		
		$scope.setModalType = function(action_type){
			$scope.action = action_type;
		}
		
		$scope.resetModal = function(){
			$scope.menu_item_group = {};
			$scope.action = '';
		}
		
		$scope.performModifierGroupOperations = function(action_type){
			
			if($scope.menu_item_group.menu_item_group_name == '' || $scope.menu_item_group.menu_item_group_name == null){
			}else{
				var jsonData = JSON.stringify({
					"id" : $scope.menu_item_group.id || null,
					"menu_item_group_name" : $scope.menu_item_group.menu_item_group_name,
					"is_active": $scope.menu_item_group.is_active || null
				});
				
				var menu_item_group_url = '${pageContext.request.contextPath}/menu/item_group/';
				
				if(action_type === 'create'){
					menu_item_group_url += 'create_item_group';
				} else if(action_type === 'update'){
					menu_item_group_url += 'edit_item_group';
				}

				console.log(jsonData);
							
				$http
				.post(
						menu_item_group_url,
						jsonData)
				.then(
						function(response) {		
							$scope.resetModal();	
							$('#createItemGroupModal').modal('toggle');
							
							if(response.status == 200){
								$scope.refreshItemGroupTable();
							}
							else if(response.status == 400){
								alert("Operations Failed To Perform!");
							}
							else if(response.status == 409){
								alert("Item Group Duplication Found!");
							}
						},
						function(response) {
							
							$scope.resetModal();	
							$('#createItemGroupModal').modal('toggle');
							
							alert("Session TIME OUT");
							$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
						});
				
				menu_item_group_url = '';
			}
			
			
			
		}
		
		$scope.removeItemGroup = function(id){			
			$http
			.delete('${pageContext.request.contextPath}/menu/item_group/delete_item_group?id=' + id)
			.then(
					function(response) {							
						if(response.status == 200){
							$scope.refreshItemGroupTable();
						}
						else if(response.stauts == 400){
							alert("Operations Failed To Perform!");
						}
					},
					function(response) {		
						alert("Session TIME OUT");
						$(location)
								.attr('href',
										'${pageContext.request.contextPath}/user');
					});
			
		}	
		
		$scope.refreshItemGroupTable = function(){
			var table = $('#itemGroup_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/item_group/get_all_item_group",
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
					{"data" : "id", "width": "4%"},
					{"data" : "menu_item_group_name"},
					{"data" : "is_active", "width": "15%"},
					{"data": "id", "width": "20%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						    return '<div class="d-flex justify-content-start"><button ng-click="getModifierGroupMenuItem('+ id +')" type="button" data-toggle="modal" data-backdrop="static" data-keyboard="false" data-target="#menuItemModal" class="btn btn-outline-primary ml-1 mr-1 custom-fontsize"><i class="far fa-list-ul"></i> Manage Items</button><button ng-click="removeItemGroup('+ id +')" class="btn btn-outline-danger ml-1 mr-1 custom-fontsize"><i class="far fa-minus-circle"></i> Remove</button></div>'			   
					 }
					}
					],
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			});
			
			$('#itemGroup_dtable tbody').off('click', 'tr td:nth-child(-n+3)');
			$('#itemGroup_dtable tbody').on('click', 'tr td:nth-child(-n+3)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/item_group/get_item_group_by_id?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find item group detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.menu_item_group.id = response.data.id;
						$scope.menu_item_group.menu_item_group_name = response.data.menu_item_group_name;
						$scope.menu_item_group.is_active = response.data.is_active;
						$scope.action = 'update';
						$('#createItemGroupModal').modal({backdrop: 'static', keyboard: false});					
					}
				});						
			});
			
		}
		
		
		/* Start assign items modal */
		$scope.filterList = [{id:1, name:'Filter by Name'},
			{id:2, name:'Filter by Backend ID'}]
		
		// initialize first selected
		$scope.filterSelected = {id: 1};
		$scope.itemList = [];
		$scope.selectedItemList = [];
		
		$scope.getModifierGroupMenuItem = function(menu_item_group_id){
			$scope.menu_item_group_id = menu_item_group_id;
			
			$http.get('${pageContext.request.contextPath}/menu/item_group/get_assigned_menu_item_list?menu_item_group_id='+ menu_item_group_id).then(
					function(response) {					
						if(response.status == 200){					
							if(response.data.length == 0){
								$scope.assign_item_action = 'New';
							} else {			
								$scope.assign_item_action = 'Edit';					
								$scope.selectedItemList = response.data;			
								
								for(var a=0; a<response.data.length;a++){
									$scope.selectedItemList[a].menu_item_image_path = "${pageContext.request.contextPath}" + response.data[a].menu_item_image_path;
								}
								
								//Keep Track old data
								for(var i =0; i<response.data.length; i++){
									$scope.oldItemList.push(response.data[i]);
								}
							}
						}
					},
					function(response) {
						if(response.status == 400){
							alert(response.data.response_message);
						} else {
							console.log(response.data);
							alert("Session TIME OUT");
			 				$(location).attr('href', '${pageContext.request.contextPath}/user');
						}
					});
			
			  // drag and drop list
			   var sortable = Sortable.create($('#sortableList')[0], {
			   		handle: ".fa-arrows-alt",
			   		scroll: true,
			   		// Element is chosen
			   		onChoose: function (/**Event*/evt) {
	
			   		},
			   		// Element dragging started
			   		onStart: function (/**Event*/evt) {
	
			   		},
			   		// Element dragging ended
			   		onEnd: function (evt) {
			   			//changeElementPositionInArray(evt.oldIndex, evt.newIndex);
			   			move($scope.selectedItemList,evt.oldIndex,evt.newIndex);
			   		}
		   		});
		   	// end drag and drop list	
		}
		
		//Assigned Item Related Operations
		$scope.submitAssignedItems = function(action_type){		
				var json_data = JSON.stringify({
					'menu_item_group_id' : $scope.menu_item_group_id,
					'item_list' : $scope.selectedItemList
				});

				if(action_type === 'New'){
				 	$http
				 	.post('${pageContext.request.contextPath}/menu/item_group/assign_menu_items', json_data)
					.then(
						function(response) {
							if(response.status == 200){				
								alert("Successfully assigned menu items.")
								$scope.getModifierGroupMenuItem($scope.menu_item_group_id);
								$scope.closeMenuItemModal();
							}
						},
						function(response) {									
							if(response.status == 400){
								alert("Unable to assign menu item.");
							} else {
								alert("Session TIME OUT");
				 				$(location)
								.attr('href',
										'${pageContext.request.contextPath}/user');
							}
						}); 
				} else if(action_type === 'Edit'){
					$http
					.post('${pageContext.request.contextPath}/menu/item_group/reassign_menu_items', json_data)
					.then(
							function(response) {
								if(response.status == 200){		
									console.log("Success");
									$scope.closeMenuItemModal();
								}
							},
							function(response) {									
								if(response.status == 400){
									alert("Unable to reassign menu item.");
								} else {
					 				alert("Session TIME OUT");
					 				$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
								}
							});

					
				}

		}
			
		$scope.assignItems = function(){	
			for(var a=0; a<$scope.itemList.length; a++){
				if($scope.itemList[a].check){
					$scope.selectedItemList.push(
						$scope.itemList[a]
					);
					
					$scope.itemList[a].check = false;
					$scope.itemList[a].isAssigned = true;
				}
			}
			
			$scope.emptyList = true;
			for(var a=0; a<$scope.itemList.length; a++){
				if(!$scope.itemList[a].isAssigned)
					$scope.emptyList = false;
			}
			
			$('#assignItemsModal').modal('toggle');
			$('#menuItemModal').modal('toggle');
		}
		
		$scope.unassignItem = function(item_id){
			for(var a=0; a<$scope.selectedItemList.length; a++){
					if($scope.selectedItemList[a].id === item_id){			
						
						for(var e=0; e<$scope.itemList.length;e++){
							if($scope.itemList[e].id === $scope.selectedItemList[a].id){
								$scope.itemList[e].isAssigned = false;
							}
						}
						
						var filteredAry = $scope.selectedItemList.filter(function(e) { return e.id !== $scope.selectedItemList[a].id })
						// deep copy
						$scope.selectedItemList = angular.copy(filteredAry);						
						//$scope.selectedItemList.splice($scope.selectedItemList.indexOf($scope.selectedItemList[a]),1);		
					}
				}
		}
		
		$scope.unassignAll = function(){
			$scope.selectedItemList = [];
		}

		$scope.openAssignItemsModal = function(){
			$('#menuItemModal').modal('toggle');
			$('#assignItemsModal').modal({backdrop: 'static', keyboard: false});
			
			if($scope.itemList.length == 0){
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : ('${pageContext.request.contextPath}/menu/menuItem/getAllMenuItemByType?menuItemType=0')
				})
				.then(function(response) {
					if(response.status == "200") {
						$scope.itemList = response.data;
						for(var i=0;i<$scope.itemList.length;i++){
							$scope.itemList[i].isAssigned = false;
							$scope.itemList[i].menu_item_image_path = "${pageContext.request.contextPath}" + $scope.itemList[i].menu_item_image_path;
						}
						
						isEdit($scope.itemList);
						
						$scope.emptyList = true;
						for(var a=0; a<$scope.itemList.length; a++){
							if(!$scope.itemList[a].isAssigned)
								$scope.emptyList = false;
						}
						
					}
					else {
						alert("Cannot Retrieve Item List");
					}
				});
			}
		}
		
		function isEdit(item_list) {
			if($scope.assign_item_action === 'Edit'){
				for(var i=0; i<item_list.length;i++){
					
					for(var j=0;j<$scope.selectedItemList.length;j++){				
						if(item_list[i].id === $scope.selectedItemList[j].id){
							item_list[i].isAssigned = true;
						}
						
					}

				}
			}
		}
		
		$scope.closeAssignItemsModal = function(){	
			$('#menuItemModal').modal('toggle');
			$('#assignItemsModal').modal('toggle');
			
			$scope.filterSelected = {id: 1};
			$('#searchbox-input').val('');
			
			for(var i=0;i<$scope.itemList.length;i++){
				$scope.itemList[i].check = false;
			}
		}
		
		$scope.clearAssignItemModal = function(){
			$scope.filterSelected = {id: 1};
			$('#searchbox-input').val('');
			
			$scope.closeMenuItemModal();
		}
		
		$scope.closeMenuItemModal = function(){	 	
			$('#menuItemModal').modal('hide');
			
			$scope.oldItemList = [];
			$scope.itemList = [];
			$scope.selectedItemList = [];
			$scope.menu_item_group_id = 0;
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
		
		// case insensitive
		$.expr[":"].contains = $.expr.createPseudo(function(arg) {
		    return function( elem ) {
		        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
		    };
		});
		
		// search input on change
		$('#searchbox-input').on('input',function(e){				
		    var filter = $(this).val(); // get the value of the input, which we filter on
		    //console.log($('.card-container').find(".card-title").innerText());
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