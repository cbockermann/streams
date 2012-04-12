/**
 * 
 */
package stream.logger;

import stream.ConditionedDataProcessor;
import stream.data.Data;
import stream.runtime.Context;
import stream.runtime.ContextAwareMacroExpander;
import stream.runtime.annotations.Description;
import stream.runtime.annotations.Parameter;
import stream.runtime.expressions.Expression;
import stream.runtime.expressions.MacroExpander;

/**
 * @author chris
 * 
 */
@Description(text = "", group = "Data Stream.Monitoring")
public class Message extends ConditionedDataProcessor {
	Expression filter;
	String txt;
	String condition;
	MacroExpander macroExpander = new MacroExpander();

	/**
	 * @return the txt
	 */
	public String getTxt() {
		if (txt == null)
			return "";

		return txt;
	}

	/**
	 * @param txt
	 *            the txt to set
	 */
	@Parameter
	public void setTxt(String txt) {
		this.txt = txt;
	}

	public void setText(String str) {
		setTxt(str);
	}

	public void setMessage(String msg) {
		setTxt(msg);
	}

	/**
	 * @see stream.AbstractDataProcessor#init(stream.runtime.Context)
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);
		macroExpander = new ContextAwareMacroExpander(ctx);
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (filter == null || filter.matches(context, data)) {
			String msg = macroExpander.substitute(getTxt(), data);
			System.out.println(msg);
		}

		return data;
	}
}