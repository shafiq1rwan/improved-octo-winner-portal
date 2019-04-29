<html>
<script>
	app.controller('ctl_category', function($scope, $http, $routeParams, $compile) {
		var table;
		var previewDefault;
		
		$scope.category_id = 0;
		$scope.category = {};
		$scope.upload_image = false;
		$scope.action = '';
		
		$scope.oldItemList = [];
		$scope.itemList = [];
		$scope.selectedItemList = [];
		$scope.assign_item_action = '';
		
		$scope.filterList = [{id:1, name:'Filter by Name'},
			{id:2, name:'Filter by Backend ID'}]
		
		$scope.filterSelected = {id: 1};
		
		var group_category_id = $routeParams.id;
		
/* 		$scope.setImage = function(param){			
			$scope.category.category_image_path = param
			$scope.category.default_preview = param
		} */
		
		$(document).ready(function() {
			$scope.refreshCategoryTable();
			
			$("textarea").keydown(function(e){
				if (e.keyCode == 13)
				{
				    // prevent default behavior
				    e.preventDefault();
				}
			});
			
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
		                extension = $('#categoryImage').val().split('.').pop();
	            	      
		        	if (['png', 'jpg', 'jpeg'].indexOf(extension) == -1) {
						msg = 'Make sure that the image is in png / jpg / jpeg format.';
						errorFlag = true;
				 	}		          
		        	else if (file.size > 150000) {
		        		msg = 'Image file must not exceed 150kb.';
		        		errorFlag = true;
					}		          
		        	else if(aspectRatio<1 || aspectRatio>2){
		        		msg = 'Make sure that the image aspect ratio is within 1:1 to 2:1.';
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
		        		$('#categoryImage').val('');
		        		alert(msg);		        		
		        		return false;
		        	}
		        	
		          	// if successful validation
		        	var reader = new FileReader();
		        	reader.readAsDataURL(file); 
		        	reader.onloadend = function() {
			       	  	$scope.category.image_path = reader.result;  
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
		
		$scope.createCategory = function(){			
			
			if($scope.category.category_name == null 
					|| $scope.category.category_name == '' 
					){
			} else if($scope.category.image_path == null || $scope.category.image_path == '') {
				swal({
					  title: "Error",
					  text: "Please upload an image",
					  icon: "warning",
					  dangerMode: true,
					});
				focus($('#categoryImage'));
			}
			else {
				var postdata = JSON.stringify ({
					group_category_id : group_category_id,
					category_name : $scope.category.category_name,
					category_description : $scope.category.category_description || null,
					category_image_path : $scope.category.image_path || null,
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
						
						if(response.status == '403'){
							alert("Session TIME OUT");
							$(location).attr('href','${pageContext.request.contextPath}/admin');	
						} else {
							alert(response.data);
						}
				});
			}

		}

		
		$scope.refreshCategoryTable = function(){		
			table = $('#category_dtable').DataTable({
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
				  "dom": 'Blrtip',
				   "buttons": [
				        {
				            text: 'Save',
				            enabled: false,
				            action: function ( e, dt, node, config ) {
				            	console.log(getAllCategoryWithSequence());
				            	$scope.updateCategorySequence(getAllCategoryWithSequence());
				            	//table.column(4).visible(true);
				            }
				        },
				        {
				        	text: 'Reorder',
				            action: function ( e, dt, node, config ) {        	
				            	table.button(0).enable();
				            	table.rowReorder.enable();
				            	table.column(4).visible(false);
				            }
				        }
				    ],
				destroy : true,
		 		"rowReorder": { "selector": 'td:nth-child(-n+2)',"enable": false,"dataSrc":"category_sequence", "update": true},
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "category_sequence", "visible": false, "searchable": false}, 
					{"data" : "id", "visible": false, "searchable": false},
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
							    return '<div class="d-flex justify-content-start"><button ng-click="getCategoryMenuItem('+ id +')" type="button" data-toggle="modal" data-backdrop="static" data-keyboard="false" data-target="#menuItemModal" class="btn btn-outline-primary ml-1 mr-1 custom-fontsize"><i class="far fa-list-ul"></i> Manage Items</button><button ng-click="removeCategory('+ id +')" type="button" class="btn btn-outline-danger ml-1 mr-1 custom-fontsize"><i class="far fa-minus-circle"></i> Remove</button></div>'	
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
						alert(response.data);
					} else if(response.status == "200") {
						$scope.category.group_category_id = response.data.group_category_id;
						$scope.category.id = response.data.id;
						$scope.category.category_name = response.data.category_name;
						$scope.category.category_description = response.data.category_description;
						$scope.category.image_path = "${pageContext.request.contextPath}" + response.data.category_image_path;
						$scope.category.is_active = response.data.is_active;
						$scope.action = 'update';					
						$('#createCategoryModal').modal('toggle');
					}
				});
			});
			
		}
		
		$scope.updateCategorySequence = function(category_list){
			$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/category/edit_category_sequence',
					data : category_list
				})
				.then(function(response) {
						$scope.refreshCategoryTable();
				}, function(response) {
						alert('Session TIME OUT');
						$(location).attr('href','${pageContext.request.contextPath}/admin');	
						
				});
		}
		
		function getAllCategoryWithSequence(){
			
			var categories = table.rows().data();	
	 		var category_holder = [];

			for (var i = 0; i < categories.count(); i++) {
				var json_data = {
					'id': categories[i].id
				};
				
				category_holder.push(json_data);
			}
			var result = {
					group_category_id: group_category_id,
					array: category_holder
					}
			
			return result; 
		}
		
		$scope.updateCategory = function(){
			if($scope.category.category_name == '' || $scope.category.category_name == null){	
			} else {
				var postdata = JSON.stringify ({
					id : $scope.category.id,
					category_name : $scope.category.category_name,
					category_description : $scope.category.category_description || null,
					category_image_path : $scope.upload_image?$scope.category.image_path:null,
					is_active : $scope.category.is_active,
					group_category_id : $scope.category.group_category_id
				});
				
				console.log("Update Category Data: " + postdata);
				
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
							
							if(response.status == '403' || response.status == '500'){
								alert("Session TIME OUT");
								$(location).attr('href','${pageContext.request.contextPath}/admin');	
							} else {
								alert(response.data);
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
						if(resposne.status == '400'){
							alert(response.data);
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
			
			$('#categoryImage').val('');
			
			$scope.upload_image = false;
		}
		
		//Assigned Item Related Operations
		$scope.submitAssignedItems = function(action_type){		
				var json_data = JSON.stringify({
					'group_category_id' : group_category_id,
					'category_id' : $scope.category_id,
					'item_list' : $scope.selectedItemList
				});

				if(action_type === 'New'){

				 	$http
					.post(
							'${pageContext.request.contextPath}/menu/category/assign_menu_item_to_category', json_data)
					.then(
							function(response) {			
									alert("Successfully assigned menu items.")
									$scope.getCategoryMenuItem($scope.category_id);
									$scope.closeMenuItemModal();
							},
							function(response) {									
									alert("Session TIME OUT");
					 				$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
							}); 
				} else if(action_type === 'Edit'){
	
					$http
					.post(
							'${pageContext.request.contextPath}/menu/category/reassign_menu_item_to_category', json_data)
					.then(
							function(response) {	
									console.log("Success");
									$scope.closeMenuItemModal();
							},
							function(response) {									
								if(response.status == '400'){
									alert(response.data);
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
							console.log($scope.itemList[i]);
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
						if(response.status == '200'){					
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
							console.log(response.data);
							alert("Session TIME OUT");
			 				$(location)
							.attr('href',
									'${pageContext.request.contextPath}/user');
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