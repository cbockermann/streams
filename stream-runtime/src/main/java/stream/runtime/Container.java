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
package stream.runtime;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author hendrik
 * 
 */
public class Container implements Callable<Boolean> {

	private URL url;
	private Map<String, String> args;

	public Container(URL url) {
		this.url = url;
	}

	public Container(URL url, Map<String, String> args) {
		this(url);
		this.args = args;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			if (this.args == null)
				stream.run.main(this.url);
			else
				stream.run.mainWithMap(this.url, this.args);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
