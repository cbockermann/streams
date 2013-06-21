/**
 * 
 */
package stream.data;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.io.Serializer;
import stream.util.JavaSerializer;
import stream.util.MD5;

/**
 * @author chris
 * 
 */
public class Encrypt extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Encrypt.class);

	public final static String ID_KEY = "encrypted:id";
	public final static String CIPHER_KEY = "encrypted:cipher";
	public final static String DATA_KEY = "encrypted:data";
	public final static String SERIALIZER_KEY = "serializer:class";

	String serialize = "java";
	Serializer serializer = new JavaSerializer();
	String secret;
	String cipher = "AES/CBC/PKCS5Padding";
	SecretKeySpec key;
	Cipher c;

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		key = new SecretKeySpec(MD5.md5(secret).getBytes(), "AES");
		c = Cipher.getInstance(cipher);

		if (secret == null) {
			throw new Exception("No 'secret' parameter specified!");
		}

		if (secret.trim().isEmpty()) {
			throw new Exception(
					"Parameter 'secret' does not contain any information!");
		}

		try {
			log.debug("Initializing cipher for '{}'", cipher);
			Cipher c = Cipher.getInstance(this.cipher);
			log.debug("Cipher initialized: {}", c);
		} catch (Exception e) {
			log.error("Failed to initialized cipher '" + this.cipher + "': "
					+ e.getMessage());
			throw new Exception("Failed to initialized cipher '" + this.cipher
					+ "': " + e.getMessage());
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();
		Data enc = DataFactory.create();

		byte[] ivData = computeIV(id, secret);
		enc.put(ID_KEY, id);

		try {

			byte[] data;

			if ("json".equalsIgnoreCase(serialize)) {
				String json = JSONObject.toJSONString(input);
				data = json.getBytes(); // baos.toByteArray();
				enc.put(SERIALIZER_KEY, "json");
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				serializer.write(input, baos);
				baos.flush();
				baos.close();
				data = baos.toByteArray();
				enc.put(SERIALIZER_KEY, serializer.getClass()
						.getCanonicalName());
			}

			IvParameterSpec iv = new IvParameterSpec(ivData);
			c.init(Cipher.ENCRYPT_MODE, key, iv);

			data = c.doFinal(data);
			enc.put(CIPHER_KEY, this.cipher);
			enc.put(DATA_KEY, data);
			log.debug("Encrypted serialized item into {} bytes", data.length);

		} catch (Exception e) {
			log.error("Encryption failed: {}", e.getMessage());
			return null;
		}

		return enc;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the serializer
	 */
	public String getSerializer() {
		return serialize;
	}

	/**
	 * @param serializer
	 *            the serializer to set
	 */
	public void setSerializer(String serialize) {
		this.serialize = serialize;
	}

	public static byte[] computeIV(String id, String secret) {
		String from = MD5.md5(id + secret);

		byte[] ivData = new byte[16];
		for (int i = 0; i < ivData.length; i++) {
			if (i < from.length()) {
				ivData[i] = (byte) from.charAt(i);
			} else {
				ivData[i] = 0;
			}
		}

		return ivData;
	}
}
