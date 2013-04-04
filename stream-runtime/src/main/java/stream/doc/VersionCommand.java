/**
 * 
 */
package stream.doc;

import java.util.List;

import stream.shell.ShellCommand;

/**
 * @author chris
 * 
 */
public class VersionCommand implements ShellCommand {

	/**
	 * @see stream.shell.ShellCommand#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {
		String version = stream.run.getVersion();
		System.out.println("streams, Version " + version);
	}
}
