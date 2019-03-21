<html>
<script>
	app.controller('ctl_byod', function($scope, $http, $compile, $routeParams) {
		
		$scope.store = {id : $routeParams.id};
		$scope.byod = [];
		
		$scope.getDeviceInfo = function(){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/store/byodByStoreId?store_id='+$scope.store.id	
			})
			.then(function(response) {
				console.log(response.data);
				$scope.store.brand_id = response.data.brand_id;
				$scope.store.name = response.data.store_name;
				$scope.store.backend_id = response.data.backend_id;
				$scope.store.byod = response.data.byod;
				$scope.store.byod_count = $scope.store.byod.length;
			});
		}
		
		$scope.addDevice = function(){
			swal({
				  title: "Are you sure?",
				  text: "Do you want to generate Activation ID for new BYOD?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  //$('#loading_modal').modal('toggle');
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/byod/activate?store_id='+$scope.store.id 	
						})
						.then(function successCallback(response) {
							if(response.status==200){
								//$('#loading_modal').modal('toggle');
								swal("Activation ID is generated", {
									icon: "success",
								});
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							//$('#loading_modal').modal('toggle');
					    	swal({
								  title: "Error",
								  text: response.data,
								  icon: "warning",
								  dangerMode: true,
							});
						});
				}					 
			});
		}
		
		$scope.terminateDevice = function(activation_id){
			swal({
				  title: "Are you sure?",
				  text: "Do you want to terminate BYOD?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {				 
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/byod/terminate?activation_id='+activation_id 	
						})
						.then(function successCallback(response) {
							console.log(response);
							if(response.status==200){
								//console.log(response);
								swal("Successfully terminate BYOD.", {
									icon: "success",
								});
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							 console.log(response);
							swal({
								  title: "Error",
								  text: response.data,
								  icon: "warning",
								  dangerMode: true,
							});					
						});				 
				}					 
			});
		}
		
		$scope.resendActivationInfo = function(activation_id){		
			swal({
				  title: "Are you sure?",
				  text: "Do you want to resend BYOD activation email?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  $('#loading_modal').modal('toggle');
					  $http({
							method : 'POST',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/resendAct?store_id='+$scope.store.id+'&activation_id='+activation_id
						})
						.then(function successCallback(response) {
							console.log(response);
							$('#loading_modal').modal('toggle');
							if(response.status==200){
								//console.log(response);
								swal("Successfully resend BYOD activation email.", {
									icon: "success",
								});
							}
						 }, function errorCallback(response) {
							 console.log(response);
							 $('#loading_modal').modal('toggle');
							 swal({
								  title: "Error",
								  text: response.data,
								  icon: "warning",
								  dangerMode: true,
							});		
						});		 
				}					 
			});
		}
		
		$scope.reactivateDevice = function(activation_id){		
			swal({
				  title: "Are you sure?",
				  text: "Do you want to reactivate BYOD?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {				 
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/byod/reactivate?activation_id='+activation_id	
						})
						.then(function successCallback(response) {
							console.log(response);
							if(response.status==200){
								//console.log(response);
								swal("Successfully terminate BYOD.", {
									icon: "success",
								});
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {
							 console.log(response);
							 swal({
								  title: "Error",
								  text: response.data,
								  icon: "warning",
								  dangerMode: true,
							});		
						});		 
				}					 
			});
		}
		
		$scope.getDeviceInfo();
		
	});
	
</script>
</html>