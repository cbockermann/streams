package stream.service;

import java.io.Serializable;

public final class ServiceInfo implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -8691557171316841174L;

	final String name;
	final String[] interfaces;
	
	
	public ServiceInfo( String name, String[] interfaces ){
		this.name = name;
		this.interfaces = interfaces;
	}
}
