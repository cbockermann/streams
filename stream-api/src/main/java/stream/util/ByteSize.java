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