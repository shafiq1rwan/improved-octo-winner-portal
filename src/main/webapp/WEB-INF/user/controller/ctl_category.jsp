<html>
<script>
	app.controller('ctl_category', function($scope, $http) {
		
		$(document).ready(function(){
		    //Example 2
		    $('#filer_example2').filer({
		        limit: 1,
		        maxSize: 1,
		        extensions: ['jpg', 'png'],
		        changeInput: true,
		        showThumbs: true,
		        addMore: true
		    });
		});
	});
</script>
</html>