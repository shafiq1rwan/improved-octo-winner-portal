<%@ page import="org.json.JSONObject"
%>

<%
	JSONObject accessRight = (JSONObject) session.getAttribute("access_rights");
	try {
		
		if(accessRight.toString() == null){
			 String redirectURL = "/user/signin";
			 response.sendRedirect(redirectURL);
		}
%>
<!-- Left Sidebar -->
<div class="left main-sidebar">

	<div class="sidebar-inner leftscroll">

		<div id="sidebar-menu">
       
		<ul>
				<%-- <li class="submenu">
					<a href="${pageContext.request.contextPath}/user/#!Router_dashboard"><i class="far fa-home"></i><span> Dashboard </span> </a>
                </li>  --%>    
                    
            <%if (accessRight.getJSONObject("accessRights").getBoolean("store")) {%>    
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_store"><i class="far fa-building"></i><span> Store</span> </a>
                </li>
            <%} %>    
            
            <%if (accessRight.getJSONObject("accessRights").getBoolean("group-category")) {%>    
                <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_group_category"><i class="far fa-sitemap"></i><span> Group Category</span> </a>
                </li>
            <%} %>    
            
            <%if (accessRight.getJSONObject("accessRights").getBoolean("menu-item")) {%>   
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
              <%} %> 
              
              <%if (accessRight.getJSONObject("accessRights").getBoolean("report")) {%>
                 <li class="submenu">
                      <a href="" ng-click="$event.preventDefault()"><i class="far fa-file"></i> <span> Report </span> 
                      <span class="far menu-arrow"></span></a>
                      <ul class="list-unstyled">
                      	<li><a href="${pageContext.request.contextPath}/user/#!Router_reportBSI">Best Selling Item</a></li>
                      	<li><a href="${pageContext.request.contextPath}/user/#!Router_report">Sales by Store</a></li>
                      	<li><a href="${pageContext.request.contextPath}/user/#!Router_reportSBE">Sales by Employee</a></li>
                      	<li><a href="${pageContext.request.contextPath}/user/#!Router_reportSBPT">Sales by Payment Type</a></li>
                      </ul>
                </li>	
              <%} %>   
                 
              <%if (accessRight.getJSONObject("accessRights").getBoolean("setting")) {%>   
                 <li class="submenu">
                       <a href="${pageContext.request.contextPath}/user/#!Router_setting"><i class="far fa-cog"></i><span> Setting </span> </a>
                </li>	
              <%} %>   
           </ul>

           <div class="clearfix"></div>

		</div>
       
		<div class="clearfix"></div>
	</div>
</div>

<% 
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
%>