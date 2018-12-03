<!-- top bar navigation -->
<div class="headerbar">

	<!-- LOGO -->
       <div class="headerbar-left">
		<a href="index.html" class="logo"><img alt="Logo" src="${pageContext.request.contextPath}/assets/images/logo.png" /> <span>Admin</span></a>
       </div>

       <nav class="navbar-custom">

                   <ul class="list-inline float-right mb-0">

                       <li class="list-inline-item dropdown notif">
                           <a class="nav-link dropdown-toggle nav-user" data-toggle="dropdown" href="#" role="button" aria-haspopup="false" aria-expanded="false">
                               <i class="fa fa-cog"></i>
                           </a>
                           <div class="dropdown-menu dropdown-menu-right profile-dropdown ">
                               <!-- item-->
                               <a href="${pageContext.request.contextPath}/user/#!Router_profile" class="dropdown-item notify-item">
                                   <i class="fa fa-user"></i> <span>Profile</span>
                               </a>

                               <!-- item-->
                               <a href="${pageContext.request.contextPath}/logout" class="dropdown-item notify-item">
                                   <i class="fa fa-power-off"></i> <span>Logout</span>
                               </a>
							
                           </div>
                       </li>

                   </ul>

                   <ul class="list-inline menu-left mb-0">
                       <li class="float-left">
                           <button class="button-menu-mobile open-left">
							<i class="fa fa-fw fa-bars"></i>
                           </button>
                       </li>                        
                   </ul>

       </nav>

</div>
<!-- End Navigation -->