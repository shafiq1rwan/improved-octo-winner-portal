<%
	String username = (String)session.getAttribute("username");
%>
<html>
<script>
	app.controller('ctl_changepassword', function($scope, $http, $compile) {
		
		$scope.submitChangePassword = function(userId){
			
			var userName = '<%=username%>';
			/* console.log(userName);
			console.log($scope.currentPassword);
			console.log($scope.newPassword);
			console.log($scope.confirmPassword); */
			
			var	data = {
					'userName' : userName,
					'currentPassword' : $scope.currentPassword,
					'newPassword' : $scope.newPassword,
					'confirmPassword' : $scope.confirmPassword,
			};
			var postData = JSON.stringify(data);
			
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/change-password/update',
				data : postData
			})
			.then(
				function(response) {
					alert(response.data);
			});
		}
		
	});
</script>
</html>