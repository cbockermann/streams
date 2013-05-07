<%java.util.Properties props = new java.util.Properties();
    props.setProperty( "context.path", request.getContextPath() );
    props.setProperty( "content", "<div id='editor-tools'></div><div id='editor'></div>" ); // "<div id='ContainerEditor'></div>" );
    props.setProperty( "stream.node.version", "0.9.4-SNAPSHOT" );
    
	String html = org.jwall.rapidminer.arts.servlets.HtmlTemplate.expand( "/template.html", props );
	out.println( html );%>