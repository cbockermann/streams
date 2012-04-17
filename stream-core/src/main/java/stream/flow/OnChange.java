/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * <p>
 * This class provides a data process that will identify return true if the
 * given attribute will change from "from" to "to".
 * </p>
 * 
 * @author Hendrik Blom &lt;hendrik.blom@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Flow")
public class OnChange extends If {

	static Logger log = LoggerFactory.getLogger(OnChange.class);

	private String key;
	private String oldValue;

	private String from;
	private String to;

	public String getFrom() {
		return from;
	}

	@Parameter(required = false, defaultValue = "")
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	@Parameter(required = false, defaultValue = "")
	public void setTo(String to) {
		this.to = to;
	}

	public OnChange() {
		oldValue = "";
	}

	@Parameter(required = true, defaultValue = "")
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public boolean matches(Data item) {
		String value = String.valueOf(item.get(key));
		boolean result = false;

		if (from.equals(to) && "".equals(from))
			if (!oldValue.equals(value))
				result = true;
			else if (oldValue.equals(from) && value.equals(to)) {
				result = true;
				log.debug(key + " changed from " + from + " to " + to + "!");
			}
		oldValue = value;
		return result;
	}
}