<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<jsp:include page="/WEB-INF/templates/header.jsp" />
	
<body>
  <jsp:include page="/WEB-INF/templates/page-header.jsp" />
 
<div style="width: 800px; margin-left: 40px;">
  <h2>Container Upload</h2>


  <div id="left">
  <p>
    Use this form to upload and deploy an XML container configuration. If
    the container requires additional Java classes, you need to upload a
    jar file with these classes along with the XML.
  </p>  
  </div>
 
 <div style="margin-top: 20px;">
  <table>
  <tr>
    <th>Property</th>
    <th>Value</th>
  </tr>
  <tr>
  	<td>
  		XML container file
  	</td>
  	<td><input type="file" /></td>
  </tr>
  <tr>
  	<td>jar archive</td>
  	<td><input type="file" /></td>
  </tr>
  <tr>
  	<td colspan="2" align="center"><input type="submit" value="Upload" /></td>
  </tr>
  </table>
</div>
</div>
 
  <jsp:include page="/WEB-INF/templates/footer.jsp" />
  
</body>
</html>
