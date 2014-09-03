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
package stream.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chris
 * 
 */
public class FileUtils {

	public static List<File> findAllFiles(File dir) {
		return listFiles(dir, true);
	}

	public static List<File> listFiles(File directory, boolean recursive) {
		List<File> result = new ArrayList<File>();
		if (directory.isFile()) {
			result.add(directory);
			return result;
		}

		File[] files = directory.listFiles();
		if (files == null)
			return result;

		for (File f : files) {

			if (f.isDirectory() && recursive) {
				result.addAll(listFiles(f, recursive));
				continue;
			}
			if (f.isFile() && !f.getName().startsWith(".")) {
				result.add(f);
			} else {
			}
		}
		return result;
	}
}
