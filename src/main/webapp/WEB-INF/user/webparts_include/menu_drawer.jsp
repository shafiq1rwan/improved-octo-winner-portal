
<!-- Left Sidebar -->
<div class="left main-sidebar">

	<div class="sidebar-inner leftscroll">

		<div id="sidebar-menu">
       
		<ul>
				<li class="submenu">
					<a href="${pageContext.request.contextPath}/user/#!Router_dashboard"><i class="far fa-home"></i><span> Dashboard </span> </a>
                </li>
                
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_store"><i class="far fa-building"></i><span> Store</span> </a>
                </li>
                
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_group_category"><i class="far fa-sitemap"></i><span> Group Category</span> </a>
                </li>
                
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_menu_item"><i class="far fa-shopping-cart"></i><span> Menu Item</span> </a>
                </li>			
									
                 <li class="submenu">
                     <a href="#" ng-click="$event.preventDefault()"><i class="far fa-tv"></i> <span> Management </span> <span class="far menu-arrow"></span></a>
                         <ul class="list-unstyled">
                             <li><a href="${pageContext.request.contextPath}/user/#!Router_item_group"><i class="far fa-ball-pile"></i>Item Group</a></li>
                             <li><a href="${pageContext.request.contextPath}/user/#!Router_modifier_group"><i class="far fa-layer-group"></i>Modifier Group</a></li>
                         </ul>
                 </li>
                 <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_user"><i class="far fa-users"></i><span> User Configuration </span> </a>
                </li>	
                 <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_setting"><i class="far fa-cog"></i><span> Setting </span> </a>
                </li>	
           </ul>

           <div class="clearfix"></div>

		</div>
       
		<div class="clearfix"></div>
	</div>
</div>