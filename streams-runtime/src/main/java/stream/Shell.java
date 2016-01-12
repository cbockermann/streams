/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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
