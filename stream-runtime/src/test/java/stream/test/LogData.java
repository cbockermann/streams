/**
 * 
 */
package stream.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.data.Data;
import stream.expressions.ContextAwareMacroExpander;
import stream.expressions.MacroExpander;

/**
 * @author chris
 * 
 */
public class LogData extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(LogData.class);

	String message;
	MacroExpander resolver;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		resolver = new ContextAwareMacroExpander(ctx);
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (message != null) {
			String text = resolver.substitute(message, input);
			log.info("Message: '{}'", text);
		}

		return input;
	}
}
