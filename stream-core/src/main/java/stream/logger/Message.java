/**
 * 
 */
package stream.logger;

import stream.ConditionedProcessor;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.data.Data;
import stream.expressions.ContextAwareMacroExpander;
import stream.expressions.Expression;
import stream.expressions.MacroExpander;

/**
 * @author chris
 * 
 */
@Description(text = "", group = "Data Stream.Monitoring")
public class Message extends ConditionedProcessor {
	Expression filter;
	String txt;
	String condition;
	MacroExpander macroExpander = new MacroExpander();

	public void setMessage(String msg) {
		if (msg == null)
			this.txt = "";
		else
			this.txt = msg;
	}

	public String getMessage() {
		return txt;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.Context)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		macroExpander = new ContextAwareMacroExpander(ctx);
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (filter == null || filter.matches(context, data)) {
			String msg = macroExpander.substitute(getMessage(), data);
			System.out.println(msg);
		}

		return data;
	}
}