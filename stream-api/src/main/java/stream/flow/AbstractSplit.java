/**
 * 
 */
package stream.flow;


/**
 * @author chris
 * 
 */
public abstract class AbstractSplit implements Split {

	String id;

	/**
	 * @see stream.io.Sink#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}