<html>
<script>
	app.controller('ctl_group_category', function($scope, $http) {
	
		$(document).ready(function() {
			$('#groupCategory_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/groupcategory",
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
					{"data" : "id", "width": "5%"}, 
					{"data" : "name"},
					{"data": "id", "width": "25%",
					 "render": function ( data, type, full, meta ) {
						 	var groupid = full.id;
						 	var count = full.id-1;
						    return '<div class="btn-toolbar justify-content-between"><button type="button" class="btn btn-outline-info border-0 p-0 custom-fontsize"><b><i class="fa fa-building"></i> Edit Stores</b></button>&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-outline-info border-0 p-0 custom-fontsize"><b><i class="fa fa-edit"></i> Edit Categories</b></button></div>'
	  						
					 }
					}
					],
				
			});
			
		});

	});
</script>
</html>