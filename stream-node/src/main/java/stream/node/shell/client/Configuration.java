/**
 * 
 */
package stream.node.shell.client;

import java.io.Serializable;

/**
 * @author chris
 * 
 */
public class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 282727117167878386L;

	String name;

	String file;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
}
