<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<jsp:include page="/WEB-INF/templates/header.jsp" />
	
<body>
  <jsp:include page="/WEB-INF/templates/page-header.jsp" />
 
  <h2>DataStreamNode</h2>


  <div id="left">
  <p>
    This is the administrative interface for a simple data node. <br/>
  </p>
  
  </div>
 
 <div style="margin-top: 20px;">
  <table>
  <tr>
    <th>Property</th>
    <th>Value</th>
  </tr>
  <%
     java.util.Map<String,String> info = stream.node.StreamNodeContext.getSystemInfo();
     for( String key : info.keySet() ){
       String header = key;
       out.println( "<tr><td><nobr><code>" + header + "</code></nobr></td><td><code>" + info.get( header ) + "</code></td></tr>" );
     }
  %>
  </table>
</div>
 
  <jsp:include page="/WEB-INF/templates/footer.jsp" />
  
</body>
</html>
