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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minidev.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.util.ByteSize;

/**
 * @author chris
 * 
 */
public class JSONBlockWriter extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(JSONBlockWriter.class);
	ByteSize blockSize = new ByteSize(64 * ByteSize.MB);
	File directory;

	File currentBlock = null;
	FileOutputStream out;

	long bytesWritten = 0L;
	int blocksCreated = 0;
	String pattern = "yyyy/MM/dd/HH00";
	SimpleDateFormat fmt = new SimpleDateFormat(pattern);

	String timeKey = "timestamp";
	String blockFormat = "block-${blockId}.json";

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		Long time = System.currentTimeMillis();
		try {
			time = new Long(input.get(timeKey).toString());
		} catch (Exception e) {
			time = System.currentTimeMillis();
		}

		try {
			if (directory != null) {

				Data dat = DataFactory.create(input);
				for (String key : dat.keySet()) {
					Serializable val = dat.get(key);
					if (val.getClass().isArray()
							&& val.getClass().getComponentType() == byte.class) {
						try {
							String enc = Base64
									.encodeBase64String((byte[]) val);
							dat.put(key, enc);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				byte[] json = (JSONObject.toJSONString(input) + "\n")
						.getBytes();

				File parent = new File(directory.getAbsolutePath()
						+ File.separator + fmt.format(new Date(time))
						+ File.separator);

				if (currentBlock != null
						&& !currentBlock.getCanonicalPath().startsWith(
								parent.getCanonicalPath())) {
					log.info("Change of target path detected!");

					out.flush();
					out.close();
					out = null;
					blocksCreated = 0;
				}

				if (out != null
						&& bytesWritten + json.length >= blockSize.getBytes()) {
					out.flush();
					out.close();
					out = null;
					log.info("Block {} full!", currentBlock);
				}

				if (out == null) {
					currentBlock = new File(parent.getAbsolutePath()
							+ File.separator
							+ blockFormat.replace("${blockId}", blocksCreated
									+ ""));
					log.info("Opening new block at {}", currentBlock);

					if (parent != null && !parent.isDirectory()) {
						parent.mkdirs();
						if (!parent.isDirectory()) {
							log.error("Failed to create directory {}", parent);
						}
					}

					out = new FileOutputStream(currentBlock);
					bytesWritten = 0L;
					blocksCreated++;
				}

				out.write(json);
				bytesWritten += json.length;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	/**
	 * @return the blockSize
	 */
	public ByteSize getBlockSize() {
		return blockSize;
	}

	/**
	 * @param blockSize
	 *            the blockSize to set
	 */
	@Parameter(description = "The size of the blocks that are to be written. Default is 64M.")
	public void setBlockSize(ByteSize blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * @return the directory
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * @param directory
	 *            the directory to set
	 */
	@Parameter(description = "The base directory in which the file are to be stored.", required = true)
	public void setDirectory(File directory) {
		this.directory = directory;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern
	 *            the pattern to set
	 */
	@Parameter(description = "A data-time pattern that is used to create subdirectories where to store blocks (within the base directory). The default pattern is `yyyy/MM/dd/HH00`.")
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the timeKey
	 */
	public String getTimeKey() {
		return timeKey;
	}

	/**
	 * @param timeKey
	 *            the timeKey to set
	 */
	@Parameter(description = "The key that is used for determining the current time. If not specified, the default key `timestamp` is used. If a data item does not contain a timestamp, the current system time is used.")
	public void setTimeKey(String timeKey) {
		this.timeKey = timeKey;
	}
}