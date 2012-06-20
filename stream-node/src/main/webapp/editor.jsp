<%

    java.util.Properties props = new java.util.Properties();
    props.setProperty( "context.path", request.getContextPath() );
    props.setProperty( "content", "<div id='ContainerEditor'></div>" );
    props.setProperty( "stream.node.version", "0.9.4-SNAPSHOT" );
    
	String html = stream.node.servlets.HtmlTemplate.expand( "/template.html", props );
	out.println( html );
%>