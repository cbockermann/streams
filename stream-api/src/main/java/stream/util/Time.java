/**
 * 
 */
package stream.util;

import stream.util.parser.TimeFormat;
import stream.util.parser.TimeParser;

/**
 * <p>
 * This is a very simple class that represents time.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class Time {

	final Long millis;

	public Time(String time) throws Exception {
		millis = TimeParser.parseTime(time);
	}

	public Time(Long millis) {
		this.millis = millis;
	}

	public Long asMillis() {
		return millis;
	}

	public Long asSeconds() {
		return millis / 1000L;
	}

	public Long asMinutes() {
		return millis / (60 * 1000L);
	}

	public String toString() {
		return new TimeFormat().format(millis);
	}
}
