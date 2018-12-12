<html>
<script>
	app.controller('ctl_category', function($scope, $http) {
		
		$scope.category = {};

		$scope.createCategory = function(){					
			var postdata = JSON.stringify ({
				group_category_id : $scope.category.group_category_id,
				category_name : $scope.category.name,
				
				
			});
		
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/category/create_category',
				data : postdata
			})
			.then(function(response) {
				if (response.status == "400") {
					alert("Session TIME OUT");
					$(location).attr('href','${pageContext.request.contextPath}/admin');			
				} else if(response.status == "200") {
					alert("Category is created successfully");
					$scope.resetModal();
					$('#createCategoryModal').modal('toggle');
					$scope.refreshCategoryTable();
				}
			}, function(response) {
					alert("Cannot create Category!");
					$scope.resetModal();
					$('#createCategoryModal').modal('toggle');		
			});
		}
		
		
		
		
		
		
		$scope.refreshCategoryTable = function(){
			
			
			
			
		}
		
		
		$scope.resetModal = function(){
			
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
	});
</script>
</html>