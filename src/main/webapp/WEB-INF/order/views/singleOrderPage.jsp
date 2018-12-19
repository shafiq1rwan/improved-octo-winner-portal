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
	
	<!-- Main Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_category.jsp" />
	<!-- Main Display[END] -->

	<!-- Item List Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_itemlist.jsp" />
	<!-- Item List Display[END] -->
	
	<!-- Item List Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_categoryselection.jsp" />
	<!-- Item List Display[END] -->

	<!-- Item Detail Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_itemdetail.jsp" />
	<!-- Item Detail Display[END] -->

	<!-- Item Cart Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_cart.jsp" />
	<!-- Item Cart Display[END] -->

	<!--  Item Checkout Display[START] -->
	<jsp:include page="/WEB-INF/order/views/sop_subviews/sop_checkout.jsp" />
	<!--  Item Checkout Display[END] -->
</body>