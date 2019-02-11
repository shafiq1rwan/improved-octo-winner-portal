<html>
<script>
	app.controller('ctl_user', function($scope, $http, $compile) {
		
		$scope.action = '';
		$scope.store = {
				ecpos:true
		};
		$scope.uploadImage = false;
		
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
			}
			else if($scope.action=='create' && $scope.store.imagePath == null || $scope.action=='create' && $scope.store.imagePath==''){
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
				else if(publish==0){
					// click unpublish button
					swal({
						  title: "Are you sure?",
						  text: "Do you want to unpublish store?",
						  icon: "warning",
						  buttons: true,
						  dangerMode: true,
						})
						.then((willCreate) => {
						  if (willCreate) {
								$scope.store.isPublish = false;
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
					store_logo_path: $scope.uploadImage?$scope.store.imagePath: null,
					store_start_operating_time: $scope.store.operatingStartTime,
					store_end_operating_time: $scope.store.operatingEndTime,
					store_ecpos : $scope.store.ecpos
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
					if($scope.action=='create'){
						swal("The store is created", {
							icon: "success",
						});
					}
					else if($scope.action=='update'){
						swal("The store is updated", {
							icon: "success",
						});
					}
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
			$scope.store = {
					ecpos:true
			};
			// reset image
			/* $('#previewImage').attr('src', "");
			previewDefault = ''; */
			$('#storeImage').val('');
			$scope.uploadImage = false;
			
			$('#operatingStartTime').datetimepicker('clear');
			$('#operatingStartTime').datetimepicker('destroy');
			
			$('#operatingEndTime').datetimepicker('clear');
			$('#operatingEndTime').datetimepicker('destroy') ;
		}	
		
		var previewDefault;
		
		$scope.refreshTable = function(){
			var table = $('#user_dtable').DataTable();
			/* var table = $('#user_dtable').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/menu/userList",
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
				"scrollX": true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "id", "width": "5%"}, 
					{"data" : "username", "width": "15%"},
					{"data" : "name"},
					{"data" : "enabled_status"}
				]
			}); */
			/* var table = $('#store_dtable').DataTable({
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
				"scrollX": true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "id", "width": "5%"}, 
					{"data" : "backend_id", "width": "15%"},
					{"data" : "store_name"},
					{"data" : "location.store_country"},
					{"data" : "is_publish", "width": "13%"},
					{"data": "id", "width": "25%",
						 "render": function ( data, type, full, meta ) {
							 	var id = full.id;
							 	var ecpos = full.store_ecpos;
							    return '<div class="btn-toolbar justify-content-start"><a ng-href="${pageContext.request.contextPath}/user/#!Router_store_byod/'+id+'" class="btn btn-outline-info p-1 custom-fontsize"><i class="fa fa-edit"></i> BYOD</a><a ng-href="${pageContext.request.contextPath}/user/#!Router_store_kiosk/'+id+'" class="btn btn-outline-info p-1 custom-fontsize"><i class="fa fa-edit"></i> KIOSK</a><a ng-show='+ecpos+' ng-href="${pageContext.request.contextPath}/user/#!Router_store_ecpos/'+id+'" class="btn btn-outline-info p-1 custom-fontsize"><i class="fa fa-edit"></i> ECPOS</a></div>'
		  						//return '<div class="btn-group"><button ng-click="openComboSetting('+ id +')" class="btn btn-info p-1 custom-fontsize"><b><i class="fa fa-edit"></i> Combo Setting</b></button><a ng-href="${pageContext.request.contextPath}/user/#!Router_combo/'+ id +'" class="btn btn-danger p-1 custom-fontsize"><b><i class="fa fa-bars"></i> Manage Tier</b></a></div>'
						 }
					}
				],			
				"createdRow": function ( row, data, index ) {
			        $compile(row)($scope);  //add this to compile the DOM
			    }
				
			});	
			
			$('#store_dtable tbody').off('click', 'tr td:nth-child(-n+5)');
			$('#store_dtable tbody').on('click', 'tr td:nth-child(-n+5)', function(evt) {			
				if ( $(evt.target).is("a") ) {
					// skip action column
			        return;
			    }
				
				$scope.action = 'update';	
				$scope.store.id = table.row(this).data().id;
				$http({
					method : 'GET',
					headers : {'Content-Type' : 'application/json'},
					url : '${pageContext.request.contextPath}/menu/store/storeById?id='+$scope.store.id		
				})
				.then(function(response) {
					if (response.status == "404") {
						alert("Unable to find store detail");
					} else if(response.status == "200") {
						console.log(response.data);
						$scope.store.name = response.data.store_name;
						$scope.store.imagePath = "${pageContext.request.contextPath}" + response.data.store_logo_path;					
						$scope.store.address = response.data.location.store_address;
						$scope.store.country = response.data.location.store_country;
						$scope.store.longitude = response.data.location.store_longitude;
						$scope.store.latitude = response.data.location.store_latitude;
						$scope.store.currency = response.data.store_currency;
						$scope.store.tableCount = response.data.store_table_count;
						$scope.store.isPublish = response.data.is_publish;
						$scope.store.operatingStartTime = response.data.store_start_operating_time;
						$scope.store.operatingEndTime = response.data.store_end_operating_time;
						$scope.store.ecpos = response.data.store_ecpos;
						
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
			}); */
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
				var _URL = window.URL || window.webkitURL;
				var file = this.files[0];
				verifyFileUpload(file);
			});
			
			function verifyFileUpload(file){  
				if (file) {
			        var img = new Image(),
			        	msg, 
			        	errorFlag = false;
			        img.src = window.URL.createObjectURL(file);
			        img.onload = function() {
		            	var width = img.naturalWidth,
			                height = img.naturalHeight,
			                aspectRatio = width/height,
			                extension = $('#storeImage').val().split('.').pop();
		            	      
			        	if (['png', 'jpg', 'jpeg'].indexOf(extension) == -1) {
							msg = 'Make sure that the image is in png / jpg / jpeg format.';
							errorFlag = true;
					 	}		          
			        	else if (file.size > 150000) {
			        		msg = 'Image file must not exceed 150kb.';
			        		errorFlag = true;
						}		          
			        	else if(aspectRatio<1 || aspectRatio>2){
			        		msg = 'Make sure that the image aspect ratio is within 1:1 to 2:1.';
			        		errorFlag = true;
						}	
			        	else if(width < 300){
			        		msg = 'Make sure that the image has minimum width of 300px.';
			        		errorFlag = true;
						}
						else if(height < 200){
							msg = 'Make sure that the image has minimum height of 200px.';
							errorFlag = true;
						}
			          	
			        	if(errorFlag){
			        		$('#storeImage').val('');
				        	swal({
								  title: "Error",
								  text: msg,
								  icon: "warning",
								  dangerMode: true,
								});
							focus($('#storeImage'));
							return false;
			        	}
			        	
			          	// if successful validation
			        	var reader = new FileReader();
			        	reader.readAsDataURL(file); 
			        	reader.onloadend = function() {
				       	  	$scope.store.imagePath = reader.result;  
				        	$scope.$apply();
				        	$scope.uploadImage = true;
			        	}		          
			        };
			        
			        img.onerror = function() {
			        	msg = 'Invalid image file.';			        	
			        	$('#storeImage').val('');
			        	swal({
							  title: "Error",
							  text: msg,
							  icon: "warning",
							  dangerMode: true,
							});
						focus($('#storeImage'));
						return false;
			        };
			    }
			}

		} ); 

	});
	
</script>
</html>