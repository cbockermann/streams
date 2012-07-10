/**
 * 
 */
package stream.shell;

import stream.Shell;
import stream.runtime.Controller;

/**
 * @author chris
 * 
 */
public class Shutdown extends Command {

	public Shutdown(Shell shell) {
		super(shell);
	}

	/**
	 * @see stream.shell.Command#execute(java.lang.String[],
	 *      java.io.PrintStream)
	 */
	@Override
	public String execute(String[] args) {

		String container = args[0];
		println("Shutdown requested for container '" + container + "'");

		try {
			Controller ctrl = shell.getNamingService().lookup(
					"//" + container + "/.ctrl", Controller.class);

			ctrl.shutdown();
		} catch (Exception e) {
			println("Error: " + e.getMessage());
			println("");
		}

		return "";
	}
}