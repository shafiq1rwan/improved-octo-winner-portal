<html>
<script>
	app.controller('ctl_user', function($scope, $http, $compile) {
		
		$scope.user = {};
		$scope.action = '';
		var table;
		
		$(document).ready(function() {
			refreshUserTable();
		});
		
		var refreshUserTable = function(){
			table = $('#users_dtable').DataTable({
				'ajax' : {
					'url' : '${pageContext.request.contextPath}/users/',
					'dataSrc': function ( json ) {
		                return json;
		            },  
					'statusCode' : {
						403 : function() {
							alert('Session TIME OUT');
							$(location).attr('href', '${pageContext.request.contextPath}/logout');
						}
					}
				},
				"destroy" : true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "id", "visible": false, "searchable": false},
					{"data" : "email"},
					{"data" : "name"},
					{"data" : "mobileNumber"},
					{"data" : "username"},
					{"data": "id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button ng-click="promptEditUserModal('+ id +')" type="button" class="btn btn-primary custom-fontsize"><b><i class="fa fa-wrench"></i>Edit</b></button></div>'	
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }
			});
		}
		
		$scope.promptEditUserModal = function(id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/users/'+ id
			})
			.then(
				function(response) {			
					$scope.user = response.data; 
					$scope.setModalType('update');
					$('#userModal').modal('toggle');
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.resetModal = function(){
			$scope.user = {};
			$scope.action = '';
		}
		
		$scope.setModalType = function(action){
			$scope.action = action;
			console.log($scope.action);
		}
		
		$scope.submitUser = function(){
			if($scope.user.name == null || $scope.user.name == '' 
					|| $scope.user.email == null || $scope.user.email == '' 
					|| $scope.user.mobileNumber == null || $scope.user.mobileNumber == '' 
					|| $scope.user.address == null || $scope.user.address == ''
					|| $scope.user.username == null || $scope.user.username == '' 
					|| $scope.user.password == null || $scope.user.password == ''){
			}
			else {
				var	data = {
						'id' : $scope.action == 'update'?$scope.user.id:0,
						'name' : $scope.user.name,
						'email' : $scope.user.email,
						'mobileNumber' : $scope.user.mobileNumber,
						'address' : $scope.user.address,
						'username' : $scope.user.username,
						'password' : $scope.user.password,
						'enabled' : $scope.user.enabled || false,
						'role' : $scope.action == 'create'?'ROLE_ADMIN':null
				};
				var postData = JSON.stringify(data);
					
				console.log("Submit User");
				console.log($scope.action);
				console.log(postData);
				
				var userUrl = $scope.action == 'create'?'signup':'edit';
				
 				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/users/' + userUrl,
					data : postData
				})
				.then(
					function(response) {			
						$scope.resetModal();
						$('#userModal').modal('toggle');
						refreshUserTable();
					},
					function(response) {			
						alert(response.data);
						$scope.resetModal();
						$('#userModal').modal('toggle');
				});
				
			}
			
			

		}
		
	
		

	});
</script>
</html>