/**
 * 
 */
package stream.test;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.expressions.ContextAwareMacroExpander;

/**
 * @author chris
 * 
 */
public class Print extends AbstractProcessor {

	String message;

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
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (message != null) {
			ContextAwareMacroExpander expander = new ContextAwareMacroExpander(
					context);
			String txt = expander.expand(message);
			System.out.println(txt);
		}

		return input;
	}
}
