/**
 * 
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

	public ByteSize(Integer bytes) throws Exception {
		this.bytes = bytes;
		if (bytes < 0)
			throw new Exception("ByteSizes must not be negative!");
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