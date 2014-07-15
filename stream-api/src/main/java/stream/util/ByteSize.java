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
package stream.util;

import java.io.Serializable;

import stream.util.parser.ByteSizeParser;

/**
 * @author Christian Bockermann
 * 
 */
public class ByteSize implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -2170050360625001698L;

	final Integer bytes;
	public final static int KB = 1024;
	public final static int MB = 1024 * KB;
	public final static int GB = 1024 * MB;
	public final static int TB = 1024 * GB;
	public final static int PT = 1024 * GB;

	public ByteSize(Integer bytes) {
		if (bytes < 0)
			this.bytes = 0;
		else
			this.bytes = bytes;
	}

	public ByteSize(String val) throws Exception {
		Long bs = ByteSizeParser.parseByteSize(val);
		bytes = bs.intValue();
	}

	public int getBytes() {
		return bytes;
	}

	public int getKilobyte() {
		return bytes / 1024;
	}

	public int getMegabyte() {
		return getKilobyte() / 1024;
	}

	public int getGigabyte() {
		return getMegabyte() / 1024;
	}

	public int getPetabyte() {
		return getGigabyte() / 1024;
	}
}