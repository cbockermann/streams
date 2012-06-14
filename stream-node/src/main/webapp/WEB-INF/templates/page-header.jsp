<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div id="page-header" class="header" >

  <% if ( session.getAttribute( "LOGIN" ) != null ) { %>
  <div class="link">
	<a href="<%= request.getContextPath() %>/logout">Logout</a>
  </div>
  <% } %>

  <ul class="component">
	<li style="list-style-image: url('<%= request.getContextPath() %>/images/home.png');"><a href="<%= request.getContextPath() %>/index">Status</a></li>
  </ul>
  <ul class="component">
	<li><a href="<%= request.getContextPath() %>/processes">Processes</a></li>
  </ul>
  
  <div class="menuNodeStorage">
<!-- 
	<div class="subMenu">
		<ul>
			<li> <a href="<%= request.getContextPath() %>/audit-event-index">Audit Events</a> </li>
			<li> <a href="<%= request.getContextPath() %>/log-message-index">Log Messages</a> </li>
		</ul>
	</div>
 -->	
  </div>

  
  <ul style="float: right;" class="component">
	<li style="list-style-image: url('<%= request.getContextPath() %>/images/system-help.png');"><a href="<%= request.getContextPath() %>/doc/">Help</a></li>
  </ul>
  
  <!-- 
  <ul style="float: right;" class="component">
	<li style="list-style-image: url('<%= request.getContextPath() %>/images/logviewer.png');"><a href="<%= request.getContextPath() %>/logging">Log-Settings</a></li>
  </ul>

  <ul style="float: right;" class="component">
	<li style="list-style-image: url('<%= request.getContextPath() %>/images/stock_script.png');"><a href="<%= request.getContextPath() %>/script">Scripting</a></li>
  </ul>
   -->
  
  
</div>
    

<div class="content">