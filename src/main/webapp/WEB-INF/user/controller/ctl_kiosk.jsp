<html>
<script>
	app.controller('ctl_kiosk', function($scope, $http, $compile, $routeParams) {
		
		$scope.store = {id : $routeParams.id};
		$scope.kiosk = [];
		
		$scope.getDeviceInfo = function(){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/store/kioskByStoreId?store_id='+$scope.store.id	
			})
			.then(function(response) {
				console.log(response.data);
				$scope.store.brand_id = response.data.brand_id;
				$scope.store.name = response.data.store_name;
				$scope.store.backend_id = response.data.backend_id;
				$scope.store.kiosk = response.data.kiosk;
				$scope.store.kiosk_count = $scope.store.kiosk.length;
			});
		}
		
		$scope.addDevice = function(){
			swal({
				  title: "Are you sure?",
				  text: "Do you want to generate Activation ID for new KIOSK?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {
					  
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/kiosk/activate?store_id='+$scope.store.id 	
						})
						.then(function successCallback(response) {
							if(response.status==200){
								swal("Activation ID is generated", {
									icon: "success",
								});
								
								$scope.getDeviceInfo();
							}
						 }, function errorCallback(response) {	
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
				  text: "Do you want to terminate KIOSK?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {				 
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/kiosk/terminate?activation_id='+activation_id 	
						})
						.then(function successCallback(response) {
							console.log(response);
							if(response.status==200){
								//console.log(response);
								swal("Successfully terminate KIOSK.", {
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
		
		$scope.reactivateDevice = function(activation_id){		
			swal({
				  title: "Are you sure?",
				  text: "Do you want to reactivate KIOSK?",
				  icon: "warning",
				  buttons: true,
				  dangerMode: true,
				})
				.then((willCreate) => {
				  if (willCreate) {				 
					  $http({
							method : 'GET',
							headers : {'Content-Type' : 'application/json'},
							url : '${pageContext.request.contextPath}/menu/store/kiosk/reactivate?activation_id='+activation_id	
						})
						.then(function successCallback(response) {
							console.log(response);
							if(response.status==200){
								//console.log(response);
								swal("Successfully reactivate KIOSK.", {
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