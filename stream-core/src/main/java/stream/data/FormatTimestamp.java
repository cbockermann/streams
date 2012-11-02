/**
 * 
 */
package stream.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

/**
 * @author chris
 * 
 */
public class FormatTimestamp extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(FormatTimestamp.class);
	String from = "@timestamp";
	String key = "date-time";
	String format = "yyyy-MM-dd HH:mm:ss";
	SimpleDateFormat dateFormat;

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
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
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
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		dateFormat = new SimpleDateFormat(format);
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		try {
			Long val = new Long(input.get(from) + "");
			String fmt = dateFormat.format(new Date(val));
			input.put(key, fmt);
		} catch (Exception e) {
			log.error("Failed to format timestamp: {}", e.getMessage());
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
		}

		return input;
	}
}