<html>
<script>
	app.controller('ctl_store', function($scope, $http, $compile) {
		
		$scope.action = '';
		$scope.store = {};
		
		$scope.modalType = function(action){
			$scope.action = action;	
			
			$('#operatingStartTime').datetimepicker({
                format: 'LT'
            });
			
			$('#operatingEndTime').datetimepicker({
                format: 'LT'
            });
		}
		
		// validation
		$scope.submitStore = function(publish){	
			if($scope.store.name == null || $scope.store.name=='' ||
				$scope.store.currency == null || $scope.store.currency=='' ||
					$scope.store.address == null || $scope.store.address=='' ||
						$scope.store.tableCount == null || $scope.store.tableCount=='' ||
							$scope.store.longitude == null || $scope.store.longitude=='' ||
								$scope.store.latitude == null || $scope.store.latitude=='' ||
									$scope.store.country == null || $scope.store.country=='' || 
										$scope.store.operatingStartTime == null || $scope.store.operatingStartTime=='' || 
											$scope.store.operatingEndTime == null || $scope.store.operatingEndTime==''){
				alert('hihi');
			}
			else if($scope.store.imagePath == null || $scope.store.imagePath==''){
				swal({
					  title: "Error",
					  text: "Please upload an image",
					  icon: "warning",
					  dangerMode: true,
					});
				focus($('#storeImage'));
			}
			else{
				if(publish==1){
					// click publish button
					swal({
						  title: "Are you sure?",
						  text: "Once publish, you will not be able to modify some of the store detail",
						  icon: "warning",
						  buttons: true,
						  dangerMode: true,
						})
						.then((willCreate) => {
						  if (willCreate) {
								$scope.store.isPublish = true;
								$scope.postRequest();
						}					 
					});
				}
				else{
					$scope.postRequest();
				}
			}
			
		}
		
		// submit request
		$scope.postRequest = function(){
			var postdata = {
					id: $scope.action=='create' ? undefined: $scope.store.id ,
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
					is_publish: $scope.store.isPublish==null?false:$scope.store.isPublish,
					store_logo_path: $scope.store.imagePath,
					store_start_operating_time: $scope.store.operatingStartTime,
					store_end_operating_time: $scope.store.operatingEndTime					
				}
				
			console.log(postdata);
			
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : $scope.action=='create'?'${pageContext.request.contextPath}/menu/store/create':'${pageContext.request.contextPath}/menu/store/edit',
				data : postdata
			})
			.then(function(response) {

				if (response.status == "403") {
					alert("Session TIME OUT");
					$(location).attr('href','${pageContext.request.contextPath}/admin');			
				} else if(response.status == "200") {
					// ok
					swal("The store has been published", {
						icon: "success",
					});
					$scope.resetModal();
					$('#storeModal').modal('toggle');
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
			$scope.action = '';
			$scope.store = {};
			// reset image
			var filerKit = $("#storeImage").prop("jFiler");
			filerKit.reset();
			
			$('#operatingStartTime').datetimepicker('clear');
			$('#operatingStartTime').datetimepicker('destroy');
			
			$('#operatingEndTime').datetimepicker('clear');
			$('#operatingEndTime').datetimepicker('destroy') ;
		}	
		
		$scope.refreshTable = function(){
			var table = $('#store_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/store",
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
					{"data" : "store_logo_path", "width": "15%"},
					{"data" : "location.store_country"},
					{"data" : "is_publish", "width": "13%"},
					{"data": "id", "width": "25%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							    return '<div class="btn-toolbar justify-content-between"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store_ecpos/'+id+'" class="btn btn-outline-info border-0 p-0 custom-fontsize"><i class="fa fa-edit"></i> ECPOS</a>&nbsp;&nbsp;&nbsp;&nbsp;<a ng-href="${pageContext.request.contextPath}/user/#!Router_store_byod/'+id+'" class="btn btn-outline-info border-0 p-0 custom-fontsize"><i class="fa fa-edit"></i> BYOD</a>&nbsp;&nbsp;&nbsp;&nbsp;<a ng-href="${pageContext.request.contextPath}/user/#!Router_store_kiosk/'+id+'" class="btn btn-outline-info border-0 p-0 custom-fontsize"><i class="fa fa-edit"></i> KIOSK</a></div>'
		  						
						 }
					}
				],			
				"createdRow": function ( row, data, index ) {
			        $compile(row)($scope);  //add this to compile the DOM
			    }
				
			});	
			
			$('#store_dtable tbody').off('click', 'tr');
			$('#store_dtable tbody').on('click', 'tr', function(evt) {			
				if ( $(evt.target).is("a") ) {
					// skip action column
			        return;
			    }
				
				$scope.action = 'update';	
				$scope.store.id = table.row(this).data().id;
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/store/storebyid?id='+$scope.store.id		
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find store detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.store.name = response.data.store_name;
						$scope.store.imagePath = response.data.store_logo_path;
						$scope.store.address = response.data.location.store_address;
						$scope.store.country = response.data.location.store_country;
						$scope.store.longitude = response.data.location.store_longitude;
						$scope.store.latitude = response.data.location.store_latitude;
						$scope.store.currency = response.data.store_currency;
						$scope.store.tableCount = response.data.store_table_count;
						$scope.store.isPublish = response.data.is_publish;
						$scope.store.operatingStartTime = response.data.store_start_operating_time;
						$scope.store.operatingEndTime = response.data.store_end_operating_time;
						console.log(moment($scope.store.operatingStartTime, "HH:mm:ss").format('HH:mm'));
						 $('#operatingStartTime').datetimepicker({
							    defaultDate: moment($scope.store.operatingStartTime, "HH:mm:ss"),
							    format: 'LT'
							  });
						 $('#operatingEndTime').datetimepicker({
							    defaultDate: moment($scope.store.operatingEndTime, "HH:mm:ss"),
							    format: 'LT'
							  });
						$('#storeModal').modal('toggle');
					}
				});
			});
		}
		
		$('#operatingStartTime').on("change.datetimepicker", function (e) {			
			$scope.store.operatingStartTime = moment(e.date);
			//$scope.store.operatingStartTime = moment(e.date).format('HH:mm');
		});
		
		$('#operatingEndTime').on("change.datetimepicker", function (e) {		
			$scope.store.operatingEndTime = moment(e.date);
			//$scope.store.operatingEndTime = moment(e.date).format('HH:mm');
		}); 
		
		$(document).ready(function() {				
			$scope.refreshTable();		

			
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