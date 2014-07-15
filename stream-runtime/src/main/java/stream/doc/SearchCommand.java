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
import java.util.Iterator;
import java.util.List;

import stream.doc.helper.DocIndex;
import stream.doc.helper.DocIndex.Result;
import stream.shell.ShellCommand;

/**
 * @author chris
 * 
 */
public class SearchCommand implements ShellCommand {

	/**
	 * @see stream.shell.ShellCommand#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {

		if (args.isEmpty()) {
			System.out.println("'search' command requires query term!");
			System.out.println();
		}

		StringBuffer query = new StringBuffer();
		Iterator<String> it = args.iterator();
		while (it.hasNext()) {
			query.append(it.next());
			query.append(" ");
		}

		DocIndex index = DocIndex.getInstance();
		List<Result> results = index.search(query.toString());
		for (Result result : results) {
			System.out.println(" " + result.getClassName() + "  (score: "
					+ result.score + ")");
		}
	}

	public static void main(String[] args) throws Exception {
		SearchCommand cmd = new SearchCommand();
		cmd.execute(Arrays.asList("key"));
	}
}