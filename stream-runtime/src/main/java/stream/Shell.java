/**
 * 
 */
package stream;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stream.doc.BuildIndex;
import stream.doc.SearchCommand;

/**
 * @author chris
 * 
 */
public class Shell {

	final static Map<String, Command> commands = new LinkedHashMap<String, Command>();
	static {
		commands.put("search", new SearchCommand());
		commands.put("help", new SearchCommand());
		commands.put("doc", new SearchCommand());
		commands.put("index-doc", new BuildIndex());
		commands.put("build-index", new BuildIndex());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			return;
		}

		List<String> params = Arrays.asList(args);
		String cmd = params.remove(0);

		Command command = commands.get(cmd);
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
