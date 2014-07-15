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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class FileUtils {

	static Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static File mkfifo(File f) throws Exception {

		File file = f;
		if (!file.exists()) {
			log.info("Creating new fifo file '{}' with mkfifo", file);
			Process p = Runtime.getRuntime().exec(
					"mkfifo " + file.getAbsolutePath());
			log.info("Waiting for mkfifo to return...");
			int ret = p.waitFor();
			log.info("mkfifo finished: {}", ret);
		} else {
			log.info("Using existing fifo-file '{}'", file);
		}

		if (!file.exists()) {
			throw new IOException("Failed to create/acquire FIFO file '"
					+ file.getAbsolutePath() + "'!");
		}

		log.debug("Created fifo {}", file);
		return file;
	}
}
