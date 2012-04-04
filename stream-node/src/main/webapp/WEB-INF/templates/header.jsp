<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>DataStreamNode</title>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/base.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/config.css">
	<link rel="SHORTCUT ICON" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon">
	
	<%
	   String refresh = (String) request.getSession().getAttribute( "REFRESH" );
	   if( refresh != null ){
	%>
	   <meta http-equiv="refresh" content="1; URL=<%= refresh %>" />
	<% } %>
</head>
