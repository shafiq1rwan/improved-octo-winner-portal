<html>
<script>
	app.controller('ctl_brand', function($scope, $http, $compile) {
				
		$scope.brands = [];
		$scope.role = '';
		$scope.brand = {};
		$scope.action = '';
		var table;
		
		$(document).ready(function() {
			getBrandInfo();
		});
		
		var getBrandInfo = function(){	
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/brands/user'
			})
			.then(
				function(response) {
					$scope.role = response.data.role;
					$scope.brands = response.data.brands;
					refreshBrandTable($scope.brands);
				},
				function(response) {
					alert(response.data);
			});
		}
		
		//Datatable
		var refreshBrandTable = function(brands){
			table = $('#brands_dtable').DataTable({
				'data': brands,
	/* 			'ajax' : {
					'url' : '${pageContext.request.contextPath}/users/brandsByUser',
					'dataSrc': function ( json ) {
						roleInfo = json.role;
		                return json.brands;
		            },  
					'statusCode' : {
						403 : function() {
							alert('Session TIME OUT');
							$(location).attr('href', '${pageContext.request.contextPath}/logout');
						}
					}
				}, */
				"destroy" : true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "name"},
					{"data": "id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							 	var html;
							 	
							 	if($scope.role === 'ROLE_SUPER_ADMIN'){
							 		html = '<div class="btn-toolbar justify-content-between"><button ng-click="redirectToBrand('+ id +')" type="button" class="btn btn-primary custom-fontsize"><b><i class="fa fa-plug"></i>Connect</b></button><button ng-click="promptEditBrandModal('+ id +')" type="button" class="btn btn-danger custom-fontsize"><b><i class="fa fa-plug"></i>Edit</b></button></div>'
							 	} 
							 	else {
							 		html = '<div class="btn-toolbar justify-content-between"><button ng-click="redirectToBrand('+ id +')" type="button" class="btn btn-primary custom-fontsize"><b><i class="fa fa-plug"></i>Connect</b></button></div>';
							 	}
							    return 	html;
						 }
					}
					],			
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }
			});
		}
		
		$scope.redirectToBrand = function(id){
				//var obj = $scope.brands.find(o => o.id === $scope.selectedBrand);
		 		var postData = JSON.stringify({'id': id });

				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/brands/redirect',
					data : postData
				})
				.then(
					function(response) {			
						console.log("Success");
						$(location)
						.attr('href',
								'${pageContext.request.contextPath}/user');
					},
					function(response) {
						alert(response.data);
				});
		}
		
		$scope.promptEditBrandModal = function(id){
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/brands?id=' + id,
			})
			.then(
				function(response) {			
					$scope.brand = response.data;
					$scope.setModalType('update');
					$('#brandModal').modal('toggle');
				},
				function(response) {
					alert(response.data);
			});
		}
		
		$scope.setModalType = function(action){
			$scope.action = action;
		}
		
		$scope.resetModal = function(){
			$scope.brand = {};
			$scope.action = '';
		}
		
		$scope.submitBrand = function(){
			if($scope.brand.name == null || $scope.brand.name == ''){
			} else {
				
				var postData = JSON.stringify({	
					'id' : $scope.action === 'update' ? $scope.brand.id : 0,
					'name': $scope.brand.name,
					'db_domain' : $scope.brand.dbDomain,
					'db_name' : $scope.brand.dbName,
					'db_user': $scope.brand.dbUsername,
					'db_password' : $scope.brand.dbPassword,
					'db_port' : $scope.brand.dbPort
				});
				
				var requestUrl = $scope.action === 'create' ? 'create' : 'edit';
				
				console.log(postData);
				
				$http({
					method : 'POST',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/brands/' + requestUrl,
					data : postData
				})
				.then(
					function(response) {			
						$scope.resetModal();
						$('#brandModal').modal('toggle');
						getBrandInfo();
					},
					function(response) {
						alert(response.data);
				});
			}
		}
		
/* 		$scope.assignedUserToBrand = function(){
			
			
			
			
			
			
			
			
		} */
		

	});
</script>
</html>