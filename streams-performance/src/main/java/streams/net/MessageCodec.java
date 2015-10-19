/**
 * 
 */
package streams.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import streams.logging.Message;

/**
 * @author chris
 *
 */
public class MessageCodec {

	public final static byte[] MESSAGE_HEADER = new byte[] { 0xB, 0x0, 0xB, 0x0 };

	public static boolean headerMatch(byte[] buf) {
		for (int i = 0; i < MESSAGE_HEADER.length; i++) {
			if (i >= buf.length || buf[i] != MESSAGE_HEADER[i]) {
				return false;
			}
		}
		return true;
	}

	public byte[] encode(Message m) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		oos.close();
		return baos.toByteArray();
	}

	public Message decode(byte[] bytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Message m = (Message) ois.readObject();
		ois.close();
		return m;
	}
}
