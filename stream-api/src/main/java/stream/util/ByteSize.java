/**
 * 
 */
package stream.util;

import java.io.Serializable;

import stream.util.parser.ByteSizeParser;

/**
 * @author chris
 * 
 */
public class ByteSize implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -2170050360625001698L;

	Integer bytes = 0;

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