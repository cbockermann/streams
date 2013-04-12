/**
 * 
 */
package stream.io;

import stream.Data;

/**
 * <p>
 * This interface is the top-level definition of queues provided within the
 * *streams* framework. Queues provide a limited space for temporarily storing
 * data items in main memory.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public interface Queue extends Barrel {

	public void setLimit(Integer limit);

	public Integer getLimit();

	public Data poll();

}
