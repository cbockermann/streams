package stream.runtime.rpc;

import java.io.Serializable;
import java.nio.charset.Charset;

public final class ContainerAnnouncement implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6722120641397620906L;
	public final static byte[] CONTAINER_QUERY = "Where are my little stream-containers?!".getBytes();
	
	final String name;
	final String protocol;
	final String host;
	final Integer port;
	
	public ContainerAnnouncement( String name, String protocol, String host, Integer port ){
		this.name = name;
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}
	
	public ContainerAnnouncement( byte[] array ){
		String str = new String( array, Charset.forName( "UTF-8" ) ).trim();
		System.out.println( "Announcement-string is: '" + str + "'" );
		String[] tok = str.split( "\\|" );
		this.name = tok[0];
		this.protocol = tok[1];
		this.host = tok[2];
		this.port = new Integer( tok[3] );
	}

	public String getName() {
		return name;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}
	
	public byte[] toByteArray(){
		return (name + "|" + protocol + "|" + host + "|" + port).getBytes( Charset.forName( "UTF-8" ) );
	}
	
	public String toString(){
		return (name + "|" + protocol + "|" + host + "|" + port);
	}
}