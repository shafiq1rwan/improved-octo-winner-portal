<html>
<script>
	app.controller('ctl_category', function($scope, $http, $routeParams, $compile) {
		
		$scope.category_id = 0;
		$scope.category = {};
		$scope.action = '';
		
		$scope.oldItemList = [];
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
			
			if($scope.category.category_name == null || $scope.category.category_name == ''){
				
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
				  "dom": 'Bfrtip',
				   "buttons": [
				        {
				            text: 'Save',
				            enabled: false,
				            action: function ( e, dt, node, config ) {
				            	
				            	//table.column(3).visible(true);
				            }
				        },
				        {
				        	text: 'Reorder',
				            action: function ( e, dt, node, config ) {        	
				            	table.button(0).enable();
				            	table.rowReorder.enable();
				            	//table.column(3).visible(false);
				            }
				        }
				    ],
				destroy : true,
		 		"rowReorder": { "selector": 'td:nth-child(2)',"enable": false,"dataSrc":"category_sequence", "update": true},
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "category_sequence", "visible": false, "searchable": false}, 
					{"data" : "category_name"},
					{"data" : "is_active", "width": "10%", 
						"render": function(data, type, full, meta ){
							var status = full.is_active;
							console.log(status);
							if(status)
								return 'Yes';
							 else 
								return 'No';		
						}
					},
					{"data": "id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button ng-click="removeCategory('+ id +')" type="button" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button><button ng-click="getCategoryMenuItem('+ id +')" type="button" data-toggle="modal" data-backdrop="static" data-keyboard="false" data-target="#menuItemModal" class="btn btn-default custom-fontsize"><b><i class="fa fa-trash"></i>Items</b></button></div>'	
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);  //add this to compile the DOM
				    }
			});
			
			$('#category_dtable tbody').off('click', 'tr td:nth-child(1)');
			$('#category_dtable tbody').on('click', 'tr td:nth-child(1)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/category/get_category_by_id?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find category detail");
					} else if(response.status == "200") {
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
		
		
		//Assigned Item Related Operations
		$scope.submitAssignedItems = function(action_type){		
				var json_data = JSON.stringify({
					'category_id' : $scope.category_id,
					'item_list' : $scope.selectedItemList
				});

				if(action_type === 'New'){

				 	$http
					.post(
							'${pageContext.request.contextPath}/menu/category/assign_menu_item_to_category', json_data)
					.then(
							function(response) {
								if(response.status == 200){				
									alert("Successfully assigned menu items.")
									$scope.getCategoryMenuItem($scope.category_id);
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
					.post(
							'${pageContext.request.contextPath}/menu/category/reassign_menu_item_to_category', json_data)
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
						
						$scope.selectedItemList.splice($scope.selectedItemList.indexOf($scope.selectedItemList[a]),1);		
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
					url : ('${pageContext.request.contextPath}/menu/menuItem/getAllMenuItemByType?menuItemType=-1')
				})
				.then(function(response) {
					if(response.status == "200") {
						$scope.itemList = response.data;
						for(var i=0;i<$scope.itemList.length;i++){
							$scope.itemList[i].isAssigned = false;
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
			$scope.category_id = 0;
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
			$scope.category_id = category_id;
			
			$http
			.get(
					'${pageContext.request.contextPath}/menu/menuItem/getMenuItemByCategory?categoryId='+ category_id)
			.then(
					function(response) {
						
						if(response.status == 200){					
							if(response.data.length == 0){
								$scope.assign_item_action = 'New';
							} else {			
								$scope.assign_item_action = 'Edit';					
								$scope.selectedItemList = response.data;
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
			 				$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user');
						}
					});
			
			  // drag and drop list
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
			   			//changeElementPositionInArray(evt.oldIndex, evt.newIndex);
			   			move($scope.selectedItemList,evt.oldIndex,evt.newIndex);
			   			console.log("My List: ");
			   			console.log($scope.selectedItemList);
			   		}
		   		});
		   	// end drag and drop list	
		}
		
/* 		function changeElementPositionInArray (old_index, new_index) {
			if (old_index > -1){	
				var temp = $scope.selectedItemList[old_index];
				$scope.selectedItemList[old_index] = $scope.selectedItemList[new_index];
				$scope.selectedItemList[new_index] = temp;	
				
				console.log($scope.selectedItemList[new_index]);
				console.log($scope.selectedItemList[old_index]);
				
			}
		} */
		
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