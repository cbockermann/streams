import stream.Data;
import stream.Processor;


/**
 * @author chris
 *
 */
public class Test implements Processor {

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		System.out.println( "input: " + input);
		return input;
	}
}
