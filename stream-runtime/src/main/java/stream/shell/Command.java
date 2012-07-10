/**
 * 
 */
package stream.shell;

import stream.Shell;

/**
 * @author chris
 * 
 */
public abstract class Command {

	final Shell shell;

	public Command(Shell shell) {
		this.shell = shell;
	}

	public void print(String msg) {
		System.out.print(msg);
	}

	public void println(String msg) {
		System.out.println(msg);
	}

	public abstract String execute(String[] args);
}
