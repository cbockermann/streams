/**
 * 
 */
package stream;

import stream.util.Time;

/**
 * @author chris
 * 
 */
public interface Ticker {

	public Time getInterval();

	public void onTick(long timestamp);
}
