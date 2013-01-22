/**
 * 
 */
package stream;

import java.util.List;

/**
 * @author chris
 * 
 */
public class RunCommand implements Command {

	/**
	 * @see stream.Command#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {
		stream.run.main(args.toArray(new String[args.size()]));
	}
}
