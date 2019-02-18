<%
	String role = (String)session.getAttribute("role");
%>
<!-- Left Sidebar -->
<div class="left main-sidebar">

	<div class="sidebar-inner leftscroll">

		<div id="sidebar-menu">
       
		<ul>

				<li class="submenu">
					<a href="${pageContext.request.contextPath}/byod/byod-panel/#!brands"><i class="fa fa-fw fa-building"></i><span> Brands </span> </a>
                </li>
                
			<%if (!role.equals("ROLE_USER")) {%>    
                <li class="submenu">
                     <a href="${pageContext.request.contextPath}/byod/byod-panel/#!users"><i class="fa fa-fw fa-user"></i><span> User Management</span> </a>
                </li>
			<%} %>  
                
           </ul>

           <div class="clearfix"></div>

		</div>
       
		<div class="clearfix"></div>
	</div>
</div>