/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;
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