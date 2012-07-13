/**
 * 
 */
package stream.moa.test;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class ParseLine extends AbstractProcessor {

	String key = "LINE";

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		String line = input.get(key).toString();
		String[] tok = line.split("\\s+");

		Data data = DataFactory.create();

		for (int i = 0; i < tok.length - 1; i++) {
			String[] pair = tok[i].split(":");
			data.put(pair[0], new Double(pair[1]));
		}

		data.put("@label", tok[tok.length - 1]);
		return data;
	}
}
