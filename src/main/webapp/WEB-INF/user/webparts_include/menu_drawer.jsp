
<!-- Left Sidebar -->
<div class="left main-sidebar">

	<div class="sidebar-inner leftscroll">

		<div id="sidebar-menu">
       
		<ul>

				<li class="submenu">
					<a class="active" href="#"><i class="fa fa-fw fa-bars"></i><span> Dashboard </span> </a>
                   </li>
                
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_store"><i class="fa fa-fw fa-building"></i><span> Store</span> </a>
                </li>
                
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_group_category"><i class="fa fa-fw fa-sitemap"></i><span> Group Category</span> </a>
                </li>
                
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_menu_item"><i class="fa fa-fw fa-shopping-cart"></i><span> Menu Item</span> </a>
                </li>			
									
                 <li class="submenu">
                     <a href="#" ng-click="$event.preventDefault()"><i class="fa fa-fw fa-tv"></i> <span> Management </span> <span class="menu-arrow"></span></a>
                         <ul class="list-unstyled">
                             <li><a href="${pageContext.request.contextPath}/user/#!Router_item_group">Item Group</a></li>
                             <li><a href="${pageContext.request.contextPath}/user/#!Router_modifier_group">Modifier Group</a></li>
                         </ul>
                 </li>
                 
                 <li class="submenu">
                       <a href="#"><i class="fa fa-fw fa-area-chart"></i><span> Setting </span> </a>
                </li>	
           </ul>

           <div class="clearfix"></div>

		</div>
       
		<div class="clearfix"></div>
	</div>
</div>