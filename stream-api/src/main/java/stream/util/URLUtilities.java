/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class URLUtilities {

	public static String readContentOrEmpty(URL url) {
		try {
			return readContent(url);
		} catch (Exception e) {
			return "";
		}
	}

	public static String readContent(URL url) throws IOException {
		if (url == null)
			return "";

		return readResponse(url.openStream());
	}

	public static String readResponse(InputStream in) throws IOException {
		StringBuffer s = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (line != null) {
			s.append(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		return s.toString();
	}
}
