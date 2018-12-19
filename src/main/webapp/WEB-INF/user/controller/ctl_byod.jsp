<html>
<script>
	app.controller('ctl_byod', function($scope, $http, $compile, $routeParams) {
		
		$scope.store = {};
		
		$scope.byod = [{id:1}, {id:2}];
		
		$http({
			method : 'GET',
			headers : {'Content-Type' : 'application/json'},
			url : '${pageContext.request.contextPath}/menu/store/'+$routeParams.id+'/ecpos'	
		})
		.then(function(response) {
			if (response.status == "404") {
				alert("Unable to find store detail");
			} else {
				$scope.store.id = response.data.id;
				$scope.store.name = response.data.store_name;
				$scope.store.backend_id = response.data.backend_id;
				console.log($scope.store);
			}
		});
		
		/* $scope.refreshTable = function(){
			var table = $('#employee_dtable').DataTable();
		}
		
		$(document).ready(function() {	
			$scope.refreshTable();
		}); */
	});
	
</script>
</html>