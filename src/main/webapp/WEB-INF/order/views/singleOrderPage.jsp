<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/order/sop.css">
</head>
<body>
	<!-- Loading Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_loading.jsp" />
	<!-- Loading Display[END] -->
	
	<!-- Landing Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_landing.jsp" />
	<!-- Landing Display[END] -->
	
	<!-- Category Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_category.jsp" />
	<!-- Category Display[END] -->

	<!-- Item List Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_itemlist.jsp" />
	<!-- Item List Display[END] -->
	
	<!-- Pop-up Category Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_categoryselection.jsp" />
	<!-- Pop-up Category Display[END] -->

	<!-- Item Detail Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_itemdetail.jsp" />
	<!-- Item Detail Display[END] -->
	
	<!-- Tier Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_tierselection.jsp" />
	<!-- Tier Display[END] -->

	<!-- Item Cart Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_cart.jsp" />
	<!-- Item Cart Display[END] -->
	
	<!-- Edit Item Detail Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_edititemdetail.jsp" />
	<!-- Edit Item Detail Display[END] -->
	
	<!-- Edit Tier Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_edittierselection.jsp" />
	<!-- Edit Tier Display[END] -->
	
	<!--  Dialog Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_dialog.jsp" />
	<!--  Dialog Display[END] -->
</body>