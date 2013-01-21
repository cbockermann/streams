/**
 * 
 */
package stream.doc;

import java.util.List;

import stream.Command;

/**
 * @author chris
 * 
 */
public class VersionCommand implements Command {

	/**
	 * @see stream.Command#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {
		String version = stream.run.getVersion();
		System.out.println("streams, Version " + version);
	}
}
