/**
 * 
 */
package streams.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Bockermann
 *
 */
public class BobCodec {

	final static Logger log = LoggerFactory.getLogger(BobCodec.class);

	// The *original* development magic code header
	public final static byte[] MAGIC_CODE = new byte[] { 0xD, 0xE, 0xA, 0xD, 0xB, 0xE, 0xE, 0xF };

	// B0B1 0000 -> Bob version 1
	public final static byte[] BOB1_CODE = new byte[] { 0xB, 0x0, 0xB, 0x1, 0x0, 0x0, 0x0, 0x0 };

	public final static int writeBlock(byte[] block, DataOutputStream out) throws IOException {
		int written = 0;

		out.write(MAGIC_CODE);
		written += MAGIC_CODE.length;

		out.writeInt(block.length);
		written += 4; // 4 bytes for an integer

		out.write(block);
		written += block.length;

		return written;
	}

	public final static byte[] readBlock(DataInputStream dataInputStream) throws Exception {

		log.debug("Expected magic sequence is {}", formatByteSequence(MAGIC_CODE));

		// checkk for magic byte sequence
		byte[] separator = new byte[MAGIC_CODE.length];
		log.debug("Reading {} bytes for magic byte sequence...", separator.length);

		int read = dataInputStream.read(separator, 0, separator.length);
		if (read <= 0) {
			log.debug("Found EOF!");
			return null;
		}

		while (read < separator.length) {
			int add = dataInputStream.read(separator, read, separator.length - read);
			if (add < 0) {
				throw new IOException("Hit end-of-file while looking for byte-boundary!");
			}
			read += add;
		}
		if (read <= 0) {
			log.debug("Found EOF!");
			return null;
		}

		log.debug("Read {} bytes for separator check", read);
		log.debug("Read separator sequence: {}", formatByteSequence(separator));

		for (int i = 0; i < MAGIC_CODE.length; i++) {
			if (MAGIC_CODE[i] != separator[i]) {
				log.warn("Found magic code mismatch, code found: {}", formatByteSequence(separator));
				throw new IOException("byte separator mismatch!");
			}
		}

		// read the block length
		int blockSize = dataInputStream.readInt();
		log.debug("block-size to read is {} bytes", blockSize);

		byte[] block = new byte[blockSize];
		int off = 0;
		read = dataInputStream.read(block, 0, blockSize - off);
		while (read < blockSize) {
			int add = dataInputStream.read(block, read, blockSize - read);
			if (add < 0) {
				throw new IOException("Hit end-of-file while reading data item block!");
			}
			read += add;
		}
		log.debug("Read block of {} bytes", read);

		return block;
	}

	protected static String formatByteSequence(byte[] bytes) {
		StringBuffer s = new StringBuffer("0x");
		for (int i = 0; i < bytes.length; i++) {
			s.append(Integer.toHexString(bytes[i]));
		}

		return s.toString();
	}
}
