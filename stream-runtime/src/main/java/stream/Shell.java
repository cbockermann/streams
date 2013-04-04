/**
 * 
 */
package stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stream.doc.BuildIndex;
import stream.doc.SearchCommand;
import stream.shell.Run;
import stream.shell.ShellCommand;

/**
 * @author chris
 * 
 */
public class Shell {

	final static Map<String, ShellCommand> commands = new LinkedHashMap<String, ShellCommand>();
	static {
		commands.put("search", new SearchCommand());
		// commands.put("help", new SearchCommand());
		// commands.put("doc", new SearchCommand());
		// commands.put("index-doc", new BuildIndex());
		commands.put("build-index", new BuildIndex());
		commands.put("run", new Run());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			return;
		}

		List<String> params = new ArrayList<String>();
		params.addAll(Arrays.asList(args));
		String cmd = params.remove(0);

		ShellCommand command = commands.get(cmd);
		if (command != null) {
			try {
				command.execute(params);
			} catch (Exception e) {
				System.err.println("Command failed: " + e.getMessage());
				System.exit(-1);
			}
		} else {
			System.err.println("Unknown command '" + cmd + "'.");
			System.exit(-1);
		}
	}
}
