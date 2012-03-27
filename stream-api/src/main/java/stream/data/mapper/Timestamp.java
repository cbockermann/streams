/**
 * 
 */
package stream.data.mapper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.Context;
import stream.util.Description;
import stream.util.Parameter;

/**
 * <p>
 * This simple processor adds a timestamp (current time in milliseconds) to all
 * processed data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class Timestamp extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(Timestamp.class);
	SimpleDateFormat dateFormat = null;
	String key = "@timestamp";
	String format = null;
	String from = null;

	public Timestamp() {
	}

	public Timestamp(String key, String format, String from) {
		setKey(key);
		setFormat(format);
		setFrom(from);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (data != null && key != null) {

			Serializable from = data.get(getFrom());
			if (dateFormat != null && from != null) {
				try {
					Date date = dateFormat.parse(from.toString());
					data.put(key, date.getTime());
				} catch (Exception e) {
					log.error(
							"Failed to parse timestamp from '{}', expected format is '{}'",
							from, format);
				}
			} else {
				data.put(key, new Long(System.currentTimeMillis()));
			}
		}

		return data;
	}

	/**
	 * @see stream.data.AbstractDataProcessor#init()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);

		if (getFormat() != null && getFrom() != null) {
			dateFormat = new SimpleDateFormat(getFormat());
		}
	}
}
