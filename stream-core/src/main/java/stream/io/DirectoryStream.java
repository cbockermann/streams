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
package stream.io;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.data.DataFactory;

/**
 * This stream reads the filenames from the given directory. The provided
 * dataItem includes the full qualified filename (@url), the directory
 * (@directory) and the filename(@filename).
 * 
 * @author Hendrik Blom
 * 
 */
@Description(group = "Data Stream.Sources")
public class DirectoryStream extends AbstractStream {

	Logger log = LoggerFactory.getLogger(DirectoryStream.class);

	private File dir;
	private static String[] files;
	private String dirPath;
	private static AtomicInteger counter;
	private static AtomicBoolean filesAreRead;

	public DirectoryStream(SourceURL url) throws Exception {
		super(url);
		if (filesAreRead == null)
			filesAreRead = new AtomicBoolean(true);
	
	}

	@Override
	public void init() throws Exception {
		if (filesAreRead.getAndSet(false)) {
			log.debug("Initializing directory stream with URL '{}'",
					url.toString());
			log.debug("   file path of URL is: {}", url.getFile());
			dir = new File(url.getFile());
			// dir = new File(new URI(url.toString()));
			if (!dir.isDirectory())
				throw new IllegalArgumentException("Directory not found");
			dirPath = dir.getAbsolutePath();
			files = dir.list();
			counter = new AtomicInteger(0);
		}

	}

	@Override
	public Data readNext() throws Exception {
		Data data = DataFactory.create();
		int c = counter.getAndIncrement();
		if (c < files.length) {
			data.put(
					"@url",
					new URL("file:" + dirPath
							+ System.getProperty("file.separator") + files[c]));
			data.put("@directory", dirPath);
			data.put("@filename", files[c]);
			return data;
		}
		filesAreRead.getAndSet(true);
		return null;
	}
}