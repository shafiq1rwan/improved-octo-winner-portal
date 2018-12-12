<html>
<script>
	app.controller('ctl_store', function($scope, $http) {
		$scope.action = '';
		$scope.store = {};
		
		$scope.publishStore = function(){
			var confirmation = confirm("Some of the store detail cannot be modified after publish. Kindly confirm.");
			if(confirmation){
				$scope.store.isPublish = true;
				$scope.createStore();
			}
		}
		
		$scope.createStore = function(){					
			var postdata = {
				store_name : $scope.store.name,
				store_logo_path : $scope.store.imagePath,
				location : {
					store_address : $scope.store.address,
					store_country : $scope.store.country,
					store_longitude : parseFloat($scope.store.longitude).toFixed(6),
					store_latitude : parseFloat($scope.store.latitude).toFixed(6)
				},
				store_currency : $scope.store.currency,
				store_table_count: $scope.store.tableCount, 
				is_publish: $scope.store.isPublish==null?false:$scope.store.isPublish
			}
			
			console.log(postdata);
			
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/menu/store/create',
				data : postdata
			})
			.then(function(response) {

				if (response.status == "403") {
					alert("Session TIME OUT");
					$(location).attr('href','${pageContext.request.contextPath}/admin');			
				} else if(response.status == "200") {
					// ok
					alert("Store is created successfully")
					$scope.resetModal();
					$('#createStoreModal').modal('toggle');
					$scope.refreshTable();
				}
			});
		}
		
		$scope.setPrecision = function($event, value){
			$event.target.value = parseFloat($event.target.value).toFixed(6);
			value =  $event.target.value;
			console.log(value);
		}
		
		// reset modal
		$scope.resetModal = function(){
			$scope.store = {};
		}
		
		$scope.refreshTable = function(){
			var table = $('#store_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/store/",
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
					{"data" : "backend_id", "width": "15%"},
					{"data" : "store_name"},
					{"data" : "store_logo_path"},
					{"data" : "location.store_country"},
					{"data" : "is_publish", "width": "13%"},
					{"data": "id", "width": "25%",
						 "render": function ( data, type, full, meta ) {
							 	var groupid = full.id;
							    return '<div class="btn-toolbar justify-content-between"><button type="button" class="btn btn-outline-info border-0 p-0 custom-fontsize"><b><i class="fa fa-edit"></i> ECPOS</b></button>&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-outline-info border-0 p-0 custom-fontsize"><b><i class="fa fa-edit"></i> BYOD</b></button>&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-outline-info border-0 p-0 custom-fontsize"><b><i class="fa fa-edit"></i> KIOSK</b></button></div>'
		  						
						 }
					}
					],
				
			});
			
			$('#store_dtable tbody').off('click', 'tr');
			$('#store_dtable tbody').on('click', 'tr', function() {
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/storebyid?id='+table.row(this).data().id			
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find store detail");
					} else if(response.status == "200") {
						alert(JSON.stringify(response));
						$scope.store.name = response.data.store_name;
						$scope.store.imagePath = response.data.store_logo_path;
						$scope.store.address = response.data.location.store_address;
						$scope.store.country = response.data.location.store_country;
						$scope.store.longitude = response.data.location.store_longitude;
						$scope.store.latitude = response.data.location.store_latitude;
						$scope.store.currency = response.data.store_currency;
						$scope.store.tableCount = response.data.store_table_count;
						$scope.store.isPublish = response.data.is_publish;

						$('#createStoreModal').modal('toggle');
					}
				});
			});
		}
		
		$(document).ready(function() {
			$scope.refreshTable();
			/* $('#createStoreForm').parsley(); */
			
			$('input[type=file]').change(function(event) {
				var element = event.target.id;
				var reader = new FileReader();
				var _URL = window.URL || window.webkitURL;
				var file = this.files[0];

				reader.readAsDataURL(file);
				reader.onload = function() {
					if (element === "storeImage")
						$scope.store.imagePath = reader.result;
				}
				reader.onerror = function(error) {
				}
			});
			
			//Example 2
		    $('#storeImage').filer({
		        limit: 1,
		        maxSize: 1,
		        extensions: ['jpg', 'png'],
		        templates: {
					box: '<ul class="jFiler-items-list jFiler-items-grid"></ul>',
					item: '<li class="jFiler-item">\
								<div class="jFiler-item-container">\
									<div class="jFiler-item-inner">\
										<div class="jFiler-item-thumb">\
											<div class="jFiler-item-status"></div>\
											<div class="jFiler-item-thumb-overlay">\
												<div class="jFiler-item-info">\
													<div style="display:table-cell;vertical-align: middle;">\
														<span class="jFiler-item-title"><b title="{{fi-name}}">{{fi-name}}</b></span>\
														<span class="jFiler-item-others">{{fi-size2}}</span>\
													</div>\
												</div>\
											</div>\
											{{fi-image}}\
										</div>\
										<div class="jFiler-item-assets jFiler-row">\
											<ul class="list-inline pull-left">\
												<li>{{fi-progressBar}}</li>\
											</ul>\
											<ul class="list-inline pull-right">\
												<li><a class="icon-jfi-trash jFiler-item-trash-action"></a></li>\
											</ul>\
										</div>\
									</div>\
								</div>\
							</li>',
					itemAppend: '<li class="jFiler-item">\
									<div class="jFiler-item-container">\
										<div class="jFiler-item-inner">\
											<div class="jFiler-item-thumb">\
												<div class="jFiler-item-status"></div>\
												<div class="jFiler-item-thumb-overlay">\
													<div class="jFiler-item-info">\
														<div style="display:table-cell;vertical-align: middle;">\
															<span class="jFiler-item-title"><b title="{{fi-name}}">{{fi-name}}</b></span>\
															<span class="jFiler-item-others">{{fi-size2}}</span>\
														</div>\
													</div>\
												</div>\
												{{fi-image}}\
											</div>\
											<div class="jFiler-item-assets jFiler-row">\
												<ul class="list-inline pull-left">\
													<li><span class="jFiler-item-others">{{fi-icon}}</span></li>\
												</ul>\
												<ul class="list-inline pull-right">\
													<li><a class="icon-jfi-trash jFiler-item-trash-action"></a></li>\
												</ul>\
											</div>\
										</div>\
									</div>\
								</li>',
					progressBar: '<div class="bar"></div>',
					itemAppendToEnd: false,
					canvasImage: true,
					removeConfirmation: true,
					_selectors: {
						list: '.jFiler-items-list',
						item: '.jFiler-item',
						progressBar: '.bar',
						remove: '.jFiler-item-trash-action'
					}
				},
		        changeInput: true,
		        showThumbs: true
		    });
		} );

	});
	
</script>
</html>