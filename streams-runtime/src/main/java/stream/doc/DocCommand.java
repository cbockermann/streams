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
package stream.doc;

import java.util.Arrays;
import java.util.List;

import stream.annotations.Parameter;
import stream.doc.helper.DocIndex;
import stream.doc.helper.DocIndex.Result;
import stream.runtime.setup.ParameterDiscovery;
import stream.shell.ShellCommand;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class DocCommand implements ShellCommand {

	/**
	 * @see stream.shell.ShellCommand#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {

		if (args.isEmpty()) {
			System.out
					.println("'doc' command requires a processor/stream name!");
			System.out.println();
		}

		String query = args.get(0);

		DocIndex index = DocIndex.getInstance();
		List<Result> results = index.search(query);
		if (results.isEmpty()) {
			System.err.println("No documentation found for processr '" + query);
			return;
		} else {

			Result result = results.get(0);
			String txt = URLUtilities.readContentOrEmpty(result.url);
			System.out.println(txt);
			System.out.println();
			try {
				List<Parameter> params = ParameterDiscovery
						.discoverParameterAnnotations(Class.forName(result
								.getClassName()));
				System.out.println("Parameters");
				System.out.println("----------");
				for (Parameter p : params) {
					if (p.name() == null || p.name().trim().isEmpty())
						continue;
					System.out.print("  \"" + p.name() + "\"  (");
					if (p.required())
						System.out.print("required");
					else
						System.out.print("optional");
					System.out.println(")");
					System.out.println("\t" + p.description());
					System.out.println();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println();
	}

	public static void main(String[] args) throws Exception {
		DocCommand cmd = new DocCommand();
		cmd.execute(Arrays.asList("stream.flow.Delay"));
	}
}