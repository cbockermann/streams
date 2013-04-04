/**
 * 
 */
package stream.shell;

import java.util.List;


/**
 * @author chris
 */
public class Run implements ShellCommand {

	/**
	 * @see stream.shell.ShellCommand#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {
		stream.run.main(args.toArray(new String[args.size()]));
	}
}
