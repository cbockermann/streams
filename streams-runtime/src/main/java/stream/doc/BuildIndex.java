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

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import stream.doc.helper.DocIndex;
import stream.shell.ShellCommand;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class BuildIndex implements ShellCommand {

	/**
	 * @see stream.shell.ShellCommand#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {

		File userIndex = new File(System.getProperty("user.home")
				+ File.separator + ".streams.doc");
		DocIndex index = new DocIndex();
		try {
			Map<Class<?>, URL> help = DocFinder.findDocumentations(null);

			int added = 0;
			for (Class<?> clazz : help.keySet()) {
				URL url = help.get(clazz);
				String text = URLUtilities.readContentOrEmpty(url);
				index.add(text, url, clazz);
				added++;
			}

			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(userIndex));
			oos.writeObject(index);
			oos.close();
			System.out.println("Updated doc index, " + added + " files added.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		BuildIndex reindex = new BuildIndex();
		reindex.execute(new ArrayList<String>());
	}
}
