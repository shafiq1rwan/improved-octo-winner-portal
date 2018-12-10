<html>
<script>
	app.controller('ctl_menu_dashboard', function($scope, $http) {
		
		$scope.data = [{category:"test 1", itemSet:[{name:"set 1", itemGroup:[{name:"group 1"}, {name:"group 2"}]},{name:"set 2",itemGroup:[{name:"group 1"}, {name:"group 2"}]}]}, 
			{category:"test 2", itemSet:[{name:"set 1", itemGroup:[{name:"group 1"}, {name:"group 2"}]},{name:"set 2",itemGroup:[{name:"group 1"}, {name:"group 2"}]}]}, 
			{category:"test 3", itemSet:[{name:"set 1", itemGroup:[{name:"group 1"}, {name:"group 2"}]},{name:"set 2",itemGroup:[{name:"group 1"}, {name:"group 2"}]}]}, 
			{category:"test 4", itemSet:[{name:"set 1", itemGroup:[{name:"group 1"}, {name:"group 2"}]},{name:"set 2",itemGroup:[{name:"group 1"}, {name:"group 2"}]}]}]
		
		$scope.itemGroup = [{name:"group 1", item:[{name:"1"}, {name:"2"}, {name:"3"}, {name:"4"}, {name:"5"}]}, {name:"group 2", item:[{name:"1"}, {name:"2"}, {name:"3"}, {name:"4"}]}]
		
		$scope.selectCategory = function($event){
			var selected = $event.currentTarget;
			if($(selected).hasClass('border-warning')){
				// category
				$(selected).removeClass( 'border-warning' ).removeClass( 'selected' );	    			  
			}
			else{
				// category
			    $(selected).addClass( 'border-warning').addClass('selected' );
			    
			    // item set
			    $('#itemSet').addClass('border').addClass('border-warning').addClass( 'selected' );				   
			}
		}
		
		$(document).ready(function() {
			$('.card').on('click', function(event) {
				if($(this).hasClass('border-warning')){
					// category
					$(this).removeClass( 'border-warning' ).removeClass( 'selected' );	    			  
				}
				else{
					// category
				    $(this).addClass( 'border-warning').addClass('selected' );
				    
				    // item set
				    $('#itemSet').addClass('border').addClass('border-warning').addClass( 'selected' );				   
				}
			});
		} );
	});
</script>
</html>