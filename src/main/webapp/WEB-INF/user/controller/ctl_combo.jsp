<html>
<script>
	app.controller('ctl_combo', function($scope, $http, $compile, $routeParams) {
		var combo_id = $routeParams.id;
		$scope.combo_details = [];
		var table;
		var combo_item_table;

		$(document).ready(function() {				
			
			$http
			.get(
				'${pageContext.request.contextPath}/menu/combo/getComboDetailByMenuItemId?menuItemId='+ combo_id)
			.then(
				function(response) {
					$scope.combo_details = response.data;
					console.log("Combo Details: " + $scope.combo_details[0].id);
					$scope.refreshComboDetailTable($scope.combo_details[0].id);
				},
				function(response) {
					alert("Cannot Retrive Combo Item!");
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
		
		
		$scope.refreshComboDetailTable = function(combo_detail_id){
				table = $('#combo_dtable').DataTable({
					"ajax" : {
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
					destroy : true,
					"order" : [ [ 0, "asc" ] ] ,
					"columns" : [ 
					  	{
			                "orderable":      false,
			                "data":           null,
			                "defaultContent": "",
			                "className": function(data,type,row){if(row.type != 'item') return "details-control";}
			            },
						{"data" : "id", "width": "5%"}, 
						{"data" : "name"},
						{"data" : "type"},
						{"data" : "price", "defaultContent": ""},
						{"data": "combo_item_detail_id", "width": "20%",
						 "render": function ( data, type, full, meta ) {
								var id = full.combo_item_detail_id;
							 	return '<div class="btn-toolbar justify-content-between"><button ng-click="removeTableData('+ id +')" class="btn btn-danger custom-fontsize"><b><i class="fa fa-trash"></i>Remove</b></button></div>';	
						 }
						},
						{"data" : "menu_items", "defaultContent": "", "visible": false}
						],
						"createdRow": function ( row, data, index ) {
					        $compile(row)($scope);
					    }			
				});
				
				
				  // Add event listener for opening and closing details
			    $('#combo_dtable tbody').on('click', 'td.details-control', function () {
			        var tr = $(this).closest('tr');
			        var row = table.row( tr );
			 
			        if ( row.child.isShown() ) {
			            // This row is already open - close it
			            row.child.hide();
			            tr.removeClass('shown');
			        }
			        else {
			            // Open this row
			            row.child(format(row.menu_items)).show();
			            tr.addClass('shown');
			        }
			    } );
		}
	
	 	$scope.changeTableData = function(id) {
	 		$scope.refreshComboDetailTable(id);
		}
		
		$scope.removeTableData = function(id) {
			
			
			
		}
		
		$scope.changeComboItemType = function(type){			
			var combo_item_url = '';
			
			if(type === 'MI'){
				
			} else if(type === 'G'){
				
			}
			
			
			
			
		}
		
		$scope.refreshComboItemTable = function(combo_items){
			 
			
			
		}

	});
	
</script>
</html>