<html>
<script>
	app.controller('ctl_category', function($scope, $http, $routeParams, $compile) {
		
		$scope.category = {};
		$scope.action = '';
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
					{"data" : "is_active", "width": "10%"},
					{"data": "id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button ng-click="removeCategory('+ id +')" type="button" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>'	
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);  //add this to compile the DOM
				    }
				
			});
			
			$('#category_dtable tbody').off('click', 'tr');
			$('#category_dtable tbody').on('click', 'tr', function() {
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
		}
		
		
	});
</script>
</html>