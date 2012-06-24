/**
 * 
 */
package stream.shell;

import stream.Shell;

/**
 * @author chris
 * 
 */
public class Call implements Command {

	final Shell shell;

	public Call(Shell shell) {
		this.shell = shell;
	}

	/**
	 * @see stream.shell.Command#execute(java.lang.String[])
	 */
	@Override
	public String execute(String[] args) {

		String ref = args[0];
		String method = args[1];

		return "";
	}
}
