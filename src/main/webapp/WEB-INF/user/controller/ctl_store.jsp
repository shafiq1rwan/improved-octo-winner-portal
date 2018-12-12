<html>
<script>
	app.controller('ctl_store', function($scope, $http) {
		
		$scope.store = {};
		
		$scope.createStore = function(){
			alert(JSON.stringify($scope.store));
		}
		
		$(document).ready(function() {
			$('#store_dtable').DataTable({
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
					{"data" : "backendId", "width": "15%"},
					{"data" : "name"},
					{"data" : "logoPath"},
					{"data" : "location.country"}
					],
				
			});
			/* $('#createStoreForm').parsley(); */
			
			$('input[type=file]').change(function(event) {
				var element = event.target.id;
				var reader = new FileReader();
				var _URL = window.URL || window.webkitURL;
				var file = this.files[0];

				reader.readAsDataURL(file);
				reader.onload = function() {
					if (element === "storeImage")
						console.log(reader.result);
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