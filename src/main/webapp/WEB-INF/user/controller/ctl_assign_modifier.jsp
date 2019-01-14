<html>
<script>
	app.controller('ctl_assign_modifier', function($scope, $http, $compile, $routeParams) {
		var menu_item_id = $routeParams.id;
		var table;
		var modifier_group_selection_table;
		
		$scope.assinged_modifiers = [];
		$scope.modifier_groups = [];
		
		$(document).ready(function() {	
			$scope.refreshAssignModifierTable(menu_item_id);
		});
		
		//Important
		$scope.refreshAssignModifierTable = function(menu_item_id){
				table = $('#assignedModifierGroup_dtable').DataTable({
					"ajax" : {
					    "processing": true,
					    "serverSide": true,
						"url" : "${pageContext.request.contextPath}/menu/modifier_group/get_assigned_modifier_group_by_menu_item_id?menuItemId=" + menu_item_id,
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
					  "dom": 'Bfrtip',
					   "buttons": [
					        {
					            text: 'Save',
					            enabled: false,
					            action: function ( e, dt, node, config ) {
					            	$scope.editAssignedModifierGroupSequence(getAssignedModifierGroupIds());
					            	//table.column(5).visible(true);
					            }
					        },
					        {
					        	text: 'Reorder',
					            action: function ( e, dt, node, config ) {        	
					            	table.button(0).enable();
					            	table.rowReorder.enable();
					            	//table.column(5).visible(false);
					            }
					        }
					    ],
					"destroy" : true,
			 		"rowReorder": { "selector": 'td:nth-child(2)',"enable": false,"dataSrc":'sequence', "update": true},
				    "scrollY": 180,
			        "paging": false,
			        "searching": false, 
					"order" : [ [ 0, "asc" ] ] ,
					"columns" : [ 
						{"data" : "sequence", "width":"5%"},
						{"data" : "name"},
						{"data": "id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
								var id = full.id;
							 	return '<div class="btn-toolbar justify-content-between"><button ng-click="removeAssignedModifierGroup('+ menu_item_id +','+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>';	
						 }
						}
						],
						"createdRow": function ( row, data, index ) {
					        $compile(row)($scope);
					    }			
				});
		}
		
		function getAssignedModifierGroupIds(){
			
			var items = table.rows().data();
			
	 		var item_holder = [];

			for (var i = 0; i < items.count(); i++) {			
				var json_data = {
						'sequence': items[i].sequence,
						'id': items[i].id
				};

				item_holder.push(json_data);
			}
			return item_holder; 
		}
		
		$scope.editAssignedModifierGroupSequence = function(assigned_modifier_groups){		
			
			var json_data = JSON.stringify({
				'menu_item_id': menu_item_id,
				'modifier_group_list':assigned_modifier_groups
			});
			
			$http
			.post(
				'${pageContext.request.contextPath}/menu/modifier_group/reorder_assigned_modifier_group',json_data)
			.then(
				function(response) {
					console.log("Success");
					$scope.refreshAssignModifierTable(menu_item_id);
				},
				function(response) {
					alert("Cannot Reorder Modifier Group Sequence!");
			}); 
		}
	
		$scope.removeAssignedModifierGroup = function(menu_item_id, modifier_group_id) {
			$http
			.delete(
				'${pageContext.request.contextPath}/menu/modifier_group/delete_assigned_modifier/?modifierGroupId=' + modifier_group_id +'&menuItemId=' +menu_item_id )
			.then(
				function(response) {
					$scope.refreshAssignModifierTable(menu_item_id);
				},
				function(response) {
					alert("Cannot Unassigned Modifier Group");
			});  
		}
		

		//Select Modifier Group to be added
		$scope.refreshModifierGroupSelectionTable = function(modifier_groups){
			
			modifier_group_selection_table = $('#modifierGroup_dtable').DataTable({
				"data" : modifier_groups,
				"scrollY": '25vh',
				"select": true,
				"destroy" : true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
				  	{
				  		"className": 'select-checkbox',
						"searchable": false,
		                "orderable":  false,
		                "data": null,
		                "defaultContent":''
		            },
					{"data" : "id", "width": "5%"}, 
					{"data" : "name"}
					],
					select: {
						style: 'multi'
					},
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			}); 
		}
		
		$scope.assignModifierGroups = function(){
			var modifier_group_list = getSelectedModifierGroup();
			console.log("You have select " + modifier_group_list);
			
 	 		var json_data = JSON.stringify({
 	 			'menu_item_id': menu_item_id,
 	 			'modifier_group_list':modifier_group_list
 	 		});
 	 		
 	 		console.log("Your data :" + json_data);

			$http
			.post(
				'${pageContext.request.contextPath}/menu/modifier_group/assign_modifier_group', json_data)
			.then(
				function(response) {			
					$scope.resetModal();	
					$('#modifierGroupSelectionModal').modal('toggle');
					$scope.refreshAssignModifierTable(menu_item_id);
				},
				function(response) {
					alert("Cannot Assign Modifier Group");
			});  
		}
		
		function getSelectedModifierGroup(){
			var selectModifierGroups = modifier_group_selection_table.rows({
				selected: true
			}).data();
			
	 		var item_holder = [];

			for (var i = 0; i < selectModifierGroups.count(); i++) {
				var json_data = {
					'id' : selectModifierGroups[i].id
				};
				
				item_holder.push(json_data);
			}

			return item_holder; 
		}
		
	 	$scope.getUnassignedModifierGroup = function(){
			$http
			.get(
				'${pageContext.request.contextPath}/menu/modifier_group/get_unassigned_modifier_groups?menuItemId=' + menu_item_id)
			.then(
				function(response) {			
					console.log("Hello Unassign Groups: " + response.data);
					$scope.modifier_groups = response.data;
					$scope.refreshModifierGroupSelectionTable($scope.modifier_groups);
				},
				function(response) {
					alert("Cannot Obtain Modifier Group");
				});  
	 	}
	 	
	 	$scope.resetModal = function(){		
	 		modifier_group_selection_table.rows().deselect().draw();
		} 
	 	
		
});
	
</script>
</html>