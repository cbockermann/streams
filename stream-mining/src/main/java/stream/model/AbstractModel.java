/**
 * 
 */
package stream.model;

import stream.learner.Model;

/**
 * @author chris
 * 
 */
public abstract class AbstractModel implements Model {

	/** The unique class ID */
	private static final long serialVersionUID = -1006561867824599870L;
	String name;

	public AbstractModel(String name) {
		this.name = name;
	}

	/**
	 * @see stream.learner.Model#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
}