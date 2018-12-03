<html>
<script>
	app.controller('ctl_menu', function($scope, $http, $window) {
		
		$scope.children = [{name:"test 1", grandChildren:[{name:"store 1"}, {name:"store 2"}]},{name:"test 2", grandChildren:[{name:"store 33"}, {name:"store 44"}]},
			{name:"test 123", grandChildren:[{name:"store 21"}, {name:"store 22"}]}];
		
		$scope.addMenu = function(){
			$window.location.href = "${pageContext.request.contextPath}/user/#!Router_add_menu";
		}
	});
</script>
</html>