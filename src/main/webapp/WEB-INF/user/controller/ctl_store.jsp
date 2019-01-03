<html>
<script>
	app.controller('ctl_store', function($scope, $http, $compile) {
		
		$scope.action = '';
		$scope.store = {
				ecpos:true
		};
		
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
					store_logo_path: $scope.store.imagePath,
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
			$('#previewImage').attr('src', "");
			$('#storeImage').val('');
			previewDefault = '';
			
			$('#operatingStartTime').datetimepicker('clear');
			$('#operatingStartTime').datetimepicker('destroy');
			
			$('#operatingEndTime').datetimepicker('clear');
			$('#operatingEndTime').datetimepicker('destroy') ;
		}	
		
		var previewDefault;
		
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
				"scrollX": true,
				"order" : [ [ 0, "asc" ] ] ,
				"columns" : [ 
					{"data" : "id", "width": "5%"}, 
					{"data" : "backend_id", "width": "15%"},
					{"data" : "store_name"},
					/* {"data" : "store_logo_path",
						 "render": function ( data, type, full, meta ) {
							 	var image = full.store_logo_path;
							    return '<img class="border border-warning rounded" style="min-width:100%;width:90px;height:90px;" src="${pageContext.request.contextPath}'+image+'" />'
						 }
					}, */
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
						//$scope.store.imagePath = response.data.store_logo_path;
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
						
						previewDefault = "${pageContext.request.contextPath}" + response.data.store_logo_path;					
						$('#previewImage').attr('src', previewDefault);
						
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
				var check = fileCheck(file, element);
				if (check == false) {
					return false;
				}
				
				reader.readAsDataURL(file);
				reader.onload = function() {
					if (element === "storeImage")
						$scope.store.imagePath = reader.result;
					console.log($scope.store.imagePath);
					$('#previewImage').attr('src', reader.result);
				}
				reader.onerror = function(error) {
				}
			});
			
			function fileCheck(file, elementName) {
				var img;
				if (file.type.indexOf("image") == -1) {
					alert("Invalid image file.");
					$('#' + elementName).wrap('<form>').closest('form').get(0).reset();
					$('#' + elementName).unwrap();
					$('#previewImage').attr('src', previewDefault);
					$scope.store.imagePath = null;
					return false;
				}
				
				if (file.size > 150000) {
					alert("Image file must not exceed 150kb.");
					$('#' + elementName).wrap('<form>').closest('form').get(0).reset();
					$('#' + elementName).unwrap();
					$('#previewImage').attr('src', previewDefault);
					$scope.store.imagePath = null;
					return false;
				}

				if (file) {
					img = new Image();
					img.onload = function() {
						var aspectRatio = this.width / this.height;
						if(aspectRatio<1 || aspectRatio>1.5){
							alert("Please make sure that the image aspect ratio is within 1:1 to 3:2");
							$('#' + elementName).wrap('<form>').closest('form').get(0).reset();
							$('#' + elementName).unwrap();
							$('#previewImage').attr('src', previewDefault);
							$scope.store.imagePath = null;
							return false;
						}		
						else if(this.width < 300){
							alert("Please make sure that the image has minimum width of 300px");
							$('#' + elementName).wrap('<form>').closest('form').get(0).reset();
							$('#' + elementName).unwrap();
							$('#previewImage').attr('src', previewDefault);
							$scope.store.imagePath = null;
							return false;
						}
						else if(this.height < 200){
							alert("Please make sure that the image has minimum height of 200px");
							$('#' + elementName).wrap('<form>').closest('form').get(0).reset();
							$('#' + elementName).unwrap();
							$('#previewImage').attr('src', previewDefault);
							$scope.store.imagePath = null;
							return false;
						}
					}
					
					img.onerror = function() {
						alert("Invalid image file.");
						$('#' + elementName).wrap('<form>').closest('form').get(0).reset();
						$('#' + elementName).unwrap();
						$('#previewImage').attr('src', previewDefault);
						$scope.store.imagePath = null;
						return false;
			        };
					img.src = URL.createObjectURL(file);
				}
			};
		} ); 

	});
	
</script>
</html>