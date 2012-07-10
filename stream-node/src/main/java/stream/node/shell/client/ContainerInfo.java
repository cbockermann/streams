/**
 * 
 */
package stream.node.shell.client;

import java.io.Serializable;

/**
 * @author chris
 * 
 */
public class ContainerInfo implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6356799927377738223L;

	public final static String STATUS_RUNNING = "running";
	public final static String STATUS_STOPPED = "stopped";

	String name;
	String uri;
	String status;

	protected ContainerInfo() {
	}

	public ContainerInfo(String name, String uri) {
		this.name = name;
		this.uri = uri;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}