/**
 * 
 */
package stream.io;

/**
 * @author chris
 * 
 */
public abstract class AbstractQueue implements Queue, QueueService { // TODO:
																		// QueueService
																		// ist
																		// hier
																		// ein
																		// fieser
																		// Hack!!!
																		// Bitte
																		// wieder
																		// entfernen
																		// und
																		// Queues
																		// in
																		// prozessoren
																		// extra
																		// injecten...

	protected String id;
	protected Integer limit = 1000;

	/**
	 * @see stream.io.Source#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.io.Source#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.Queue#setSize(java.lang.Integer)
	 */
	@Override
	public void setSize(Integer limit) {
		this.limit = limit;
	}

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return limit;
	}
}