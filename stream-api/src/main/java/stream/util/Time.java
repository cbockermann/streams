/**
 * 
 */
package stream.util;

import java.io.Serializable;

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
public class Time implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 5508820209522304182L;

	public final static long SECOND = 1000L;
	public final static long MINUTE = 60 * SECOND;
	public final static long HOUR = 60 * MINUTE;
	public final static long DAY = 24 * HOUR;
	public final static long WEEK = 7 * DAY;
	public final static long MONTH = 30 * DAY + DAY / 2;
	public final static long YEAR = 12 * MONTH;

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
