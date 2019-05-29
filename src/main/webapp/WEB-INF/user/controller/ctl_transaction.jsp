<html>
<script>
	app.controller('ctl_transaction', function($scope, $http, $compile, $routeParams) {
		
		$scope.store = {id : $routeParams.id};
		
		$scope.getTransactionsList = function() {
			var table = $('#datatable_transactions').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/transaction",
					"data" : {
						id: $scope.store.id
					},
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
				"columns" : [
					{"data" : "checkNumber"},
					{"data" : "staffName"}, 
					{"data" : "transactionType"},
					{"data" : "paymentMethod"},
					{"data" : "paymentType"},
					{"data" : "transactionAmount"},
					{"data" : "transactionStatus"},
					{"data" : "transactionDate"},
				 	{"data" : "id", "visible": false, "searchable": false}
					]
			});
		}
		
		$(document).ready(function() {	
			$scope.getTransactionsList();
		});
	});
	
</script>
</html>