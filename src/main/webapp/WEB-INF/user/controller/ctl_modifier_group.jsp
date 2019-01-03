<html>
<script>
	app.controller('ctl_modifier_group', function($scope, $http, $compile) {
		$scope.modifier_group = {};
		$scope.action = '';
		
		$(document).ready(function() {
			$scope.refreshModifierGroupTable();
		});
		
		$scope.setModalType = function(action_type){
			$scope.action = action_type;
		}
		
		$scope.resetModal = function(){
			$scope.modifier_group = {};
			$scope.action = '';
		}
		
		$scope.performModifierGroupOperations = function(action_type){
			
			if($scope.modifier_group.modifier_group_name == '' || $scope.modifier_group.modifier_group_name == null){
			}else{
				var jsonData = JSON.stringify({
					"id" : $scope.modifier_group.id || null,
					"modifier_group_name" : $scope.modifier_group.modifier_group_name,
					"is_active": $scope.modifier_group.is_active || null
				});
				
				var modifier_group_url = '${pageContext.request.contextPath}/menu/modifier_group/';
				
				if(action_type === 'create'){
					modifier_group_url += 'create_modifier_group';
				} else if(action_type === 'update'){
					modifier_group_url += 'edit_modifier_group';
				}

				console.log(jsonData);
							
				$http
				.post(
						modifier_group_url,
						jsonData)
				.then(
						function(response) {		
							$scope.resetModal();	
							$('#createModifierGroupModal').modal('toggle');
							
							if(response.status == 200){
								$scope.refreshModifierGroupTable();
							}
							else if(response.stauts == 400){
								alert("Operations Failed To Perform!");
							}
							else if(response.status == 409){
								alert("Modifier Group Duplication Found!");
							}
						},
						function(response) {
							
							$scope.resetModal();	
							$('#createModifierGroupModal').modal('toggle');
							
							alert("Session TIME OUT");
							$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
						});
				
				modifier_group_url = '';
			}
			
			
			
		}
		
		$scope.removeModifierGroup = function(id){			
			$http
			.delete('${pageContext.request.contextPath}/menu/modifier_group/delete_modifier_group?id=' + id)
			.then(
					function(response) {							
						if(response.status == 200){
							$scope.refreshModifierGroupTable();
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
		
		
		//TODO modified the existing menu_item (modifier item type)
		$scope.refreshModifierGroupTable = function(){
			var table = $('#modifierGroup_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/modifier_group/get_all_modifier_group",
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
					{"data" : "modifier_group_name","width": "15%"},
					{"data" : "is_active", "width": "10%"},
					{"data": "id", "width": "15%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						    return '<div class="btn-toolbar justify-content-between"><button ng-click="removeModifierGroup('+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>'			   
					 }
					}
					],
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			});
			
			$('#modifierGroup_dtable tbody').off('click', 'tr td:nth-child(-n+3)');
			$('#modifierGroup_dtable tbody').on('click', 'tr td:nth-child(-n+3)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/modifier_group/get_modifier_group_by_id?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find modifier group detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.modifier_group.id = response.data.id;
						$scope.modifier_group.modifier_group_name = response.data.modifier_group_name;
						$scope.modifier_group.is_active = response.data.is_active;
						$scope.action = 'update';
						$('#createModifierGroupModal').modal({backdrop: 'static', keyboard: false});
						
						getAssignedItemList();
					}
				});						
			});
			
		}
		
		/* Start assigned item list */
		$scope.assignedItemList = [];
		
		$scope.unassignItem = function(id){
			console.log(id);
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/modifier_group/unassign_menu_item?id='+id	
			})
			.then(function(response) {
				if (response.status == "400") {
					alert("Unable to unassign menu item.");
				} else if(response.status == "200") {
					alert("Successfully unassign menu item.")	
					getAssignedItemList();
				}
			});			
		}
		/* End assigned item list */
		
		
		/* Start assign items modal */
		$scope.filterList = [{id:1, name:'Filter by Name'},
			{id:2, name:'Filter by Backend ID'}]
		
		// initialize first selected
		$scope.filterSelected = {id: 1};
		$scope.itemList = [];
		$scope.selectedItemList = [];
		
		function getAssignedItemList(){
			$scope.assignedItemList = [];
			// get assigned menu item list
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : ($scope.action=='create'?'${pageContext.request.contextPath}/menu/modifier_group/get_assigned_menu_item_list?id=-1':
					'${pageContext.request.contextPath}/menu/modifier_group/get_assigned_menu_item_list?id='+ $scope.modifier_group.id)
			})
			.then(function(response) {
				if (response.status == "404") {
					alert("Unable to find modifier group detail");
				} else if(response.status == "200") {
					console.log(response.data);
					var result = response.data.data;

					for(var a=0; a<result.length; a++){
						$scope.assignedItemList.push(
								{
									id: result[a].id,
									backend_id: result[a].backend_id,
									menu_item_name: result[a].menu_item_name,
									menu_item_type_name: result[a].menu_item_type_name,
									menu_item_image_path: result[a].menu_item_image_path,
								}
						)
					}
				}
			});
		}
		
		$scope.openAssignItemsModal = function(){
			$('#createModifierGroupModal').modal('toggle');
			// open assign items modal
			$('#assignItemsModal').modal({backdrop: 'static', keyboard: false});
			
			// get menu item list
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : ($scope.action=='create'?'${pageContext.request.contextPath}/menu/modifier_group/get_menu_item_list?id=-1':
					'${pageContext.request.contextPath}/menu/modifier_group/get_menu_item_list?id='+ $scope.modifier_group.id)
			})
			.then(function(response) {
				if (response.status == "404") {
					alert("Unable to find modifier group detail");
				} else if(response.status == "200") {
					console.log(response.data);
					var result = response.data.data;
					console.log(result);
					console.log(result[0].backend_id);
					for(var a=0; a<result.length; a++){
						$scope.itemList.push(
								{
									id: result[a].id,
									backend_id: result[a].backend_id,
									menu_item_name: result[a].menu_item_name,
									menu_item_type_name: result[a].menu_item_type_name,
									check: false
								}
						)
					}
				}
			});
		}
		
		$scope.submitAssignItems = function(){
			for(var a=0; a<$scope.itemList.length; a++){
				if($scope.itemList[a].check){
					$scope.selectedItemList.push($scope.itemList[a]);
				}
			}
			
			var postdata = {
					modifier_group_id : $scope.modifier_group.id,
					item_list : $scope.selectedItemList
			}
			console.log(postdata);
			
			// assign menu item
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'text/plain'},
				url : '${pageContext.request.contextPath}/menu/modifier_group/assign_menu_items',
				data : {
					modifier_group_id : $scope.modifier_group.id,
					item_list : $scope.selectedItemList
				}
			})
			.then(function(response) {
				if (response.status == "400") {
					alert("Unable to assign menu item.");
				} else if(response.status == "200") {
					console.log(response.data);
					
					alert("Successfully assigned menu items.")
					getAssignedItemList();
					$scope.closeAssignItemsModal();
				}
			});
			
		}
		
		$scope.closeAssignItemsModal = function(){
			// close assign items modal
			$('#assignItemsModal').modal('toggle');
			$('#createModifierGroupModal').modal('toggle');
			
			// reinitialize
			$scope.filterSelected = {id: 1};
			$scope.itemList = [];
			$scope.selectedItemList = [];
		}
		
		$scope.selectCard = function(item){
			if(item.check){
				item.check=false;
			}
			else{
				item.check=true;
			}
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