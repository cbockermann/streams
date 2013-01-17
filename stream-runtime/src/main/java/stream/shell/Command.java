/**
 * 
 */
package stream.shell;

import stream.DebugShell;

/**
 * @author chris
 * 
 */
public abstract class Command {

	final DebugShell shell;

	public Command(DebugShell shell) {
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
