<html>
<script>
	app.controller('ctl_modifier_group', function($scope, $http, $compile) {
	
		$scope.modifier_group = {};
		$scope.action = '';
		
		$(document).ready(function() {
			$scope.refreshModifierGroupTable();
		});
		
		
		$scope.setModalType = function(action_type){
			$scope.action = action_type;
		}
		
		$scope.resetModal = function(){
			$scope.modifier_group = {};
			$scope.action = '';
		}
		
		$scope.performModifierGroupOperations = function(action_type){
			
			if($scope.modifier_group.modifier_group_name == '' || $scope.modifier_group.modifier_group_name == null){
			}else{
				var jsonData = JSON.stringify({
					"id" : $scope.modifier_group.id || null,
					"modifier_group_name" : $scope.modifier_group.modifier_group_name,
					"is_active": $scope.modifier_group.is_active || null
				});
				
				var modifier_group_url = '${pageContext.request.contextPath}/menu/modifier_group/';
				
				if(action_type === 'create'){
					modifier_group_url += 'create_modifier_group';
				} else if(action_type === 'update'){
					modifier_group_url += 'edit_modifier_group';
				}

				console.log(jsonData);
							
				$http
				.post(
						modifier_group_url,
						jsonData)
				.then(
						function(response) {		
							$scope.resetModal();	
							$('#createModifierGroupModal').modal('toggle');
							
							if(response.status == 200){
								$scope.refreshModifierGroupTable();
							}
							else if(response.stauts == 400){
								alert("Operations Failed To Perform!");
							}
							else if(response.status == 409){
								alert("Modifier Group Duplication Found!");
							}
						},
						function(response) {
							
							$scope.resetModal();	
							$('#createModifierGroupModal').modal('toggle');
							
							alert("Session TIME OUT");
							$(location)
									.attr('href',
											'${pageContext.request.contextPath}/user');
						});
				
				modifier_group_url = '';
			}
			
			
			
		}
		
		$scope.removeModifierGroup = function(id){			
			$http
			.delete('${pageContext.request.contextPath}/menu/modifier_group/delete_modifier_group?id=' + id)
			.then(
					function(response) {							
						if(response.status == 200){
							$scope.refreshModifierGroupTable();
						}
						else if(response.stauts == 400){
							alert("Operations Failed To Perform!");
						}
					},
					function(response) {		
						alert("Session TIME OUT");
						$(location)
								.attr('href',
										'${pageContext.request.contextPath}/user');
					});
			
		}
		
		
		//TODO modified the existing menu_item (modifier item type)
		$scope.refreshModifierGroupTable = function(){
			var table = $('#modifierGroup_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/modifier_group/get_all_modifier_group",
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
					{"data" : "id", "width": "4%"}, 
					{"data" : "backend_id", "width": "10%"},
					{"data" : "modifier_group_name","width": "15%"},
					{"data" : "is_active", "width": "10%"},
					{"data": "id", "width": "15%",
					 "render": function ( data, type, full, meta ) {
						 	var id = full.id;
						    return '<div class="btn-toolbar justify-content-between"><button ng-click="removeModifierGroup('+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>'			   
					 }
					}
					],
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			});
			
			$('#modifierGroup_dtable tbody').off('click', 'tr td:nth-child(3)');
			$('#modifierGroup_dtable tbody').on('click', 'tr td:nth-child(3)', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/modifier_group/get_modifier_group_by_id?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find modifier group detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.modifier_group.id = response.data.id;
						$scope.modifier_group.modifier_group_name = response.data.modifier_group_name;
						$scope.modifier_group.is_active = response.data.is_active;
						$scope.action = 'update';
						$('#createModifierGroupModal').modal('toggle');
					}
				});
			});
			
		}
		
		
		
		
		
		

	});
	
</script>
</html>