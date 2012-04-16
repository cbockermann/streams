/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
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
@Description(text = "", group = "Data Stream.Change")
public class Change extends If {

	static Logger log = LoggerFactory.getLogger(Change.class);

	private String att;
	private String oldValue;

	private String from;
	private String to;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Change() {
		oldValue = "";
	}

	public void setAtt(String att) {
		this.att = att;
	}

	public String getAtt() {
		return att;
	}

	public boolean matches(Data item) {
		String value = String.valueOf(item.get(att));
		boolean result = false;
		if (oldValue.equals(from) && value.equals(to)) {
			result = true;
			log.info(att + " changed from " + from + " to " + to + "!");
		}
		oldValue = value;
		return result;
	}
}