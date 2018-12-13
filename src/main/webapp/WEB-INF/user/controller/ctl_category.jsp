<html>
<script>
	app.controller('ctl_category', function($scope, $http) {
		
		$scope.category = {};
		
		$(document).ready(function() {		
			$scope.refreshCategoryTable();
			
		});
		

		$scope.createCategory = function(){		
			
			$scope.category.group_category_id = 1;
			
			
			
			
			var postdata = JSON.stringify ({
				group_category_id : $scope.category.group_category_id,
				category_name : $scope.category.name,
				category_description : $scope.category.description,
				category_image_path : $scope.category.image_path,
				is_active : $scope.category.is_active
			});
			
			console.log(postdata);
		
			/* $http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/category/create_category',
				data : postdata
			})
			.then(function(response) {
				if (response.status == "400") {
					alert("Session TIME OUT");
					$(location).attr('href','${pageContext.request.contextPath}/admin');			
				} else if(response.status = "409") {
					alert(response.data.response_message);
				}
				else if(response.status == "200") {
					alert("Category is created successfully");
					$scope.resetModal();
					$('#createCategoryModal').modal('toggle');
					$scope.refreshCategoryTable();
				}
			}, function(response) {
					alert("Cannot create Category!");
					$scope.resetModal();
					$('#createCategoryModal').modal('toggle');		
			}); */
		}

		
		$scope.refreshCategoryTable = function(){		
			var table = $('#category_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/category/get_all_category",
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
							 	var groupid = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button type="button" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>'	
						 }
					}
					],
				
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
						$('#createCategoryModal').modal('toggle');
					}
				});
			});
			
			
			
		}
		
		
		$scope.resetModal = function(){
			$scope.category = {};
		}
		
		
	});
</script>
</html>