/**
 * 
 */
package stream.logger;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;
import stream.runtime.annotations.Description;
import stream.runtime.annotations.Parameter;
import stream.runtime.expressions.Expression;
import stream.util.MacroExpander;

/**
 * @author chris
 * 
 */
@Description(text = "", group = "Data Stream.Monitoring")
public class Message extends ConditionedDataProcessor {
	Expression filter;
	String txt;
	String condition;

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

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (filter == null || filter.matches(context, data)) {
			String msg = MacroExpander.expand(getTxt(), data);
			System.out.println(msg);
		}

		return data;
	}
}