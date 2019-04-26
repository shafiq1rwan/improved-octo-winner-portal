<html>
<script>
	app.controller('ctl_setting', function($scope, $http, $compile) {
		$scope.byod = {};
		
		$scope.refreshPage = function(){
			$scope.uploadImg1 = false;
			$scope.uploadImg2 = false;
			$scope.uploadImg3 = false;
			$scope.uploadImg4 = false;
			
			$http({
				method : 'GET',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/setting/getConfig'		
			})
			.then(function(response) {
				if(response.status == "200") {
					console.log(response.data);
					$scope.byod.appName = response.data.appName;
					$scope.byod.mainLogo = '${pageContext.request.contextPath}' + response.data.mainLogoPath;					
					$scope.byod.shortcutLogo = '${pageContext.request.contextPath}' + response.data.shortcutLogoPath;				
					$scope.byod.mainBackground = '${pageContext.request.contextPath}' + response.data.mainBackgroundPath;					
					$scope.byod.landingLogo = '${pageContext.request.contextPath}' + response.data.landingLogoPath;						
					$scope.byod.mainColor = response.data.mainColor;
					$scope.byod.subColor = response.data.subColor;
					$scope.byod.mainTextColor = response.data.mainTextColor;
					$scope.byod.subTextColor = response.data.subTextColor;
					$scope.byod.lbColor = response.data.localeButtonColor;
					$scope.byod.mbTextColor = response.data.mainButtonTextColor;
					$scope.byod.mbBackgroundColor = response.data.mainButtonBackgroundColor;
					$scope.byod.mbBackgroundHoverColor = response.data.mainButtonBackgroundHoverColor;
					$scope.byod.mbBackgroundFocusColor = response.data.mainButtonBackgroundFocusColor;
				}
			}, function(response){
				console.log(response);
				swal({
					  title: "Error",
					  text: response.data,
					  icon: "warning",
					  dangerMode: true,
					});
			});
		}
		
		$scope.promptSubmit = function (){
			if($scope.byod.appName == null || $scope.byod.appName == ''){
				swal({
					  title: "Error",
					  text: "Please enter application name",
					  icon: "warning",
					  dangerMode: true,
					});
				$('#appName').focus();
			}
			else if($scope.byod.mainLogo == null || $scope.byod.mainLogo == '' || 
					$scope.byod.shortcutLogo == null || $scope.byod.shortcutLogo == '' || 
						$scope.byod.mainBackground == null || $scope.byod.mainBackground == '' ||
							$scope.byod.landingLogo == null || $scope.byod.landingLogo == ''){
				swal({
					  title: "Error",
					  text: "Please upload an image",
					  icon: "warning",
					  dangerMode: true,
					});
				//focus($('#storeImage'));
			}
			else{
				swal({
					  title: "Confirmation",
					  text: "Do you want to save the changes?",
					  icon: "warning",
					  buttons: true,
					  dangerMode: true,
					})
					.then((willCreate) => {
					  if (willCreate) {
							$scope.submitByod();
					}					 
				});
			}
		}
		
		$scope.submitByod = function(){
			var postData = {
					appName :$scope.byod.appName,
					mainLogoPath : $scope.uploadImg1 ? $scope.byod.mainLogo : undefined,
					shortcutLogoPath : $scope.uploadImg2 ? $scope.byod.shortcutLogo : undefined,
					mainBackgroundPath : $scope.uploadImg3 ? $scope.byod.mainBackground : undefined,
					landingLogoPath : $scope.uploadImg4 ? $scope.byod.landingLogo : undefined,
					mainColor : $scope.byod.mainColor,
					subColor : $scope.byod.subColor,
					mainTextColor : $scope.byod.mainTextColor,
					subTextColor : $scope.byod.subTextColor,
					localeButtonColor : $scope.byod.lbColor,
					mainButtonTextColor : $scope.byod.mbTextColor,
					mainButtonBackgroundColor : $scope.byod.mbBackgroundColor,
					mainButtonBackgroundHoverColor : $scope.byod.mbBackgroundHoverColor,
					mainButtonBackgroundFocusColor : $scope.byod.mbBackgroundFocusColor
			};
			console.log(postData);
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/setting/updateConfig',
				data : postData
			})
			.then(function(response) {
				if(response.status == "200") {
					console.log(response.data);
					$scope.refreshPage();
					swal("The setting is updated", {
						icon: "success",
					});
				}
			}, function(response){
				console.log(response);
				swal({
					  title: "Error",
					  text: response.data,
					  icon: "warning",
					  dangerMode: true,
					});
			});
		}
		
		$(document).ready(function() {
			$scope.refreshPage();
			
			$('input[type=file]').change(function(event) {
				var element = event.target.id;			
				var _URL = window.URL || window.webkitURL;
				var file = this.files[0];
				console.log(element);
				verifyFileUpload(file, element);
			});
			
			function verifyFileUpload(file, id){  
				if (file) {
			        var img = new Image(),
			        	msg, 
			        	errorFlag = false;
			        img.src = window.URL.createObjectURL(file);
			        img.onload = function() {
		            	var width = img.naturalWidth,
			                height = img.naturalHeight,
			                aspectRatio = width/height,
			                extension = $('#'+id).val().split('.').pop();
		            	      
			        	if (['png', 'jpg', 'jpeg'].indexOf(extension) == -1) {
							msg = 'Make sure that the image is in png / jpg / jpeg format.';
							errorFlag = true;
					 	}		          
			        	/* else if (file.size > 150000) {
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
						} */
			          	
			        	if(errorFlag){
			        		$('#'+id).val('');
				        	swal({
								  title: "Error",
								  text: msg,
								  icon: "warning",
								  dangerMode: true,
								});
							focus($('#'+id));
							return false;
			        	}
			        	
			          	// if successful validation
			        	var reader = new FileReader();
			        	reader.readAsDataURL(file); 
			        	reader.onloadend = function() {
			        		if(id == 'mainLogo'){
				       	  		$scope.byod.mainLogo = reader.result;
				       	  		$scope.uploadImg1 = true;
			        		}
			        		else if (id == 'shortcutLogo'){
			        			$scope.byod.shortcutLogo = reader.result;
			        			$scope.uploadImg2 = true;
			        		}
			        		else if (id == 'mainBackground'){
			        			$scope.byod.mainBackground = reader.result;
			        			$scope.uploadImg3 = true;
			        		}
			        		else if (id == 'landingLogo'){
			        			$scope.byod.landingLogo = reader.result;
			        			$scope.uploadImg4 = true;
			        		}
				        	$scope.$apply();
			        	}		          
			        };
			        
			        img.onerror = function() {
			        	msg = 'Invalid image file.';			        	
			        	$('#'+id).val('');
			        	swal({
							  title: "Error",
							  text: msg,
							  icon: "warning",
							  dangerMode: true,
							});
						focus($('#'+id));
						return false;
			        };
			    }
			}
		});
	});
	
</script>
</html>