<html>
<script>
	app.controller('ctl_combo', function($scope, $http, $compile, $routeParams) {
		var combo_id = $routeParams.id;
		var table;
		var combo_item_table;
		
		$scope.combo_details = [];
		$scope.combo_items = [];
		$scope.detail_id = 0;
		
		$(document).ready(function() {							
			//Start the first tab on entered
			$http
			.get(
				'${pageContext.request.contextPath}/menu/combo/getComboDetailByMenuItemId?menuItemId='+ combo_id)
			.then(
				function(response) {
					$scope.combo_details = response.data;
					$scope.detail_id = $scope.combo_details[0].id;
					$scope.refreshComboDetailTable($scope.combo_details[0].id);
				},
				function(response) {
					alert(response.data);
			}); 
		});

		function format (menu_items) {
			 var html = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
			 	html += '<tr><th>Name</th><th>Price</th></tr>'
			    for (var menu_item in menu_items){
			        html += '<tr>'+
			                   '<td>' + menu_item.name  +'</td>'+
			                   '<td>' + menu_item.price +'</td>'+
			                '</tr>';
			    }        
			    return html += '</table>'; 
		}
		
		//Main Datatable for displaying Combo Item Detail
		$scope.refreshComboDetailTable = function(combo_detail_id){
				
				console.log("My detail data: " + combo_detail_id);
				
				table = $('#combo_dtable').DataTable({
					"ajax" : {
					    "processing": true,
					    "serverSide": true,
						"url" : "${pageContext.request.contextPath}/menu/combo/getMenuItemAndItemGroupInCombo?comboDetailId=" + combo_detail_id,
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
					            	$scope.editComboItemSequence(getAllComboDetailItemWithSequence());
					            	//table.column(5).visible(true);
					            }
					        },
					        {
					        	text: 'Reorder',
					            action: function ( e, dt, node, config ) {        	
					            	table.button(0).enable();
					            	table.rowReorder.enable();
					            	table.column(4).visible(false);
					            }
					        }
					    ], 
					"destroy" : true,
			 		"rowReorder": { "selector": 'td:nth-child(-n+3)',"enable": false,"dataSrc":'sequence', "update": true},
				    "scrollY": 180,
			        "paging": false,
			        "searching": false, 
					"order" : [ [ 0, "asc" ] ] ,
					"columns" : [ 
						{"data" : "sequence", "visible": false},
						{"data" : "name"},
						{"data" : "type", "defaultContent": ""},
						{"data" : "price", "defaultContent": "N/A", 
						 "render": function (data, type, full) {
					         	return parseFloat(data).toFixed(2);
					    	}
						},
						{"data": "combo_item_detail_id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
								var id = full.combo_item_detail_id;
							 	return '<div class="btn-toolbar justify-content-between"><button ng-click="removeComboDetail('+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>';	
						 }
						}
						],
						"createdRow": function ( row, data, index ) {
					        $compile(row)($scope);
					    }
				});
		}
		
		$scope.editComboItemSequence = function(items){
			$http
			.post(
				'${pageContext.request.contextPath}/menu/combo/editComboItemDetail',items)
			.then(
				function(response) {
					$scope.refreshComboDetailTable($scope.detail_id);
				},
				function(response) {
					alert(response.data);
			}); 
		}

	 	$scope.changeTableData = function(id) {
	 		$scope.detail_id = id;
	 		$scope.refreshComboDetailTable(id);
		}
		
		$scope.removeComboDetail = function(id) {
			$http
			.delete(
				'${pageContext.request.contextPath}/menu/combo/deleteComboItemDetail?id='+ id)
			.then(
				function(response) {
					$scope.refreshComboDetailTable($scope.detail_id);
				},
				function(response) {
					alert(response.data);
			});  
		}
		
		//Refresh Combo Item or Menu Item Group Table
		$scope.refreshComboItemTable = function(combo_items){
			
			console.log(combo_items);
			
	 		combo_item_table = $('#comboItem_dtable').DataTable({
				"data" : combo_items.data,
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
					{"data" : "name"},
					{"data" : "price", "defaultContent": "N/A"},
					{"data" : "type", "visible": false}
					],
					select: {
						style: 'multi'
					},
					"createdRow": function ( row, data, index ) {
				        $compile(row)($scope);
				    }			
			}); 
	
		}
		
		$scope.changeComboItemType = function(type){			
			var combo_item_url = '${pageContext.request.contextPath}/menu';
			
			if(type === 'MI')
				combo_item_url += '/menuItem/getAllAlaCartMenuItem'
			else if(type === 'G')
				combo_item_url += '/item_group/get_all_item_group_for_combo'
			
			$http
			.get(
					combo_item_url
				)
			.then(
				function(response) {
					$scope.combo_items = response.data;
					console.log("data: "+ $scope.combo_items);
					$scope.refreshComboItemTable($scope.combo_items);
				},
				function(response) {
					alert(response.data);
			}); 
		}

		/* $scope.changeComboItemType = function(type){			
			var combo_item_url = '${pageContext.request.contextPath}/menu';
			
			if(type === 'MI'){
				combo_item_url += '/menuItem/getAllAlaCartMenuItem'
			} else if(type === 'G'){
				combo_item_url += ''
			}
			
			var comboTable = $('#comboItem_dtable').DataTable({
				"ajax" : {
					"url" : combo_item_url
				},
				destroy : true,
				"order" : [[0, "asc"]],
				"columns" : [
					{
		                "orderable":      false,
		                "data":           null,
		                "defaultContent": ""
		            },
					{"data" : "id", "width": "5%"}, 
					{"data" : "name"},
					{"data" : "price", "defaultContent": "N/A"}
				],
				"createdRow": function ( row, data, index ) {
			        $compile(row)($scope);
			    }
			});
		} */
		
		$scope.addItemOrGroup = function(){
			var item_group_list = getSelectedMenuItem();
			console.log(item_group_list);
			
 	 		var json_data = JSON.stringify({
 	 			'combo_detail_id': $scope.detail_id,
 	 			'item_arrays':item_group_list
 	 		});
 	 		
 	 		console.log('submitted data: ' + json_data);

			$http
			.post(
				'${pageContext.request.contextPath}/menu/combo/createComboItemDetail', json_data)
			.then(
				function(response) {			
					$scope.resetModal();	
					$('#comboItemSelectionModal').modal('toggle');

					$scope.refreshComboDetailTable($scope.detail_id);
				},
				function(response) {
					//var response_data = response.data || 'Error occured when adding item or group';
					alert(response.data);
			});  
		}
		
		function getSelectedMenuItem(){
			var selectItems = combo_item_table.rows({
				selected: true
			}).data();
			
	 		var item_holder = [];

			for (var i = 0; i < selectItems.count(); i++) {
				var json_data = {
					'id' : selectItems[i].id,
					'type' : selectItems[i].type
				};
				
				item_holder.push(json_data);
			}
			return item_holder; 
		}
		
		function getAllComboDetailItemWithSequence(){	
			var items = table.rows().data();
	 		var item_holder = [];

			for (var i = 0; i < items.count(); i++) {
				var json_data = {
					'sequence': items[i].sequence,
					'id': items[i].combo_item_detail_id
				};		
				item_holder.push(json_data);
			}
			console.log("My Overall Item: " + item_holder);
			return item_holder; 
		}

	 	$scope.resetModal = function(){	 
	 		$('.nav-item a[href="#mi"]').tab('show');
	 		combo_item_table.rows().deselect().draw();
		} 
		
	});
	
</script>
</html>