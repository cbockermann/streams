/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
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
	
	
	public boolean equals( Object o ){
		if( o == this )
			return true;
		
		if( o != null && toString().equals( o.toString() ) )
			return true;
		
		return false;
	}
}