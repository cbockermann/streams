/**
 * 
 */
package stream.data;

import java.io.ByteArrayInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
public class Decrypt extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Decrypt.class);
	Serializer serializer = new JavaSerializer();
	String secret;

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		if (secret == null) {
			throw new Exception("No 'secret' parameter specified!");
		}

		if (secret.trim().isEmpty()) {
			throw new Exception(
					"Parameter 'secret' does not contain any information!");
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (!input.containsKey(Encrypt.ID_KEY)) {
			log.warn("Item does not contain key '{}'", Encrypt.ID_KEY);
			return input;
		}

		if (!input.containsKey(Encrypt.DATA_KEY)) {
			log.warn("Item does not contain encrypted data!");
			return input;
		}

		if (!input.containsKey(Encrypt.CIPHER_KEY)) {
			log.warn(
					"Item does not contain cipher type (attribute '{}' is missing)!",
					Encrypt.CIPHER_KEY);
			return input;
		}

		String id = input.get(Encrypt.ID_KEY).toString();
		byte[] ivData = Encrypt.computeIV(id, secret);

		String cipher = input.get(Encrypt.CIPHER_KEY).toString();
		log.debug("Decryption cipher '{}' = {}", cipher);
		byte[] data = (byte[]) input.get(Encrypt.DATA_KEY);

		try {

			Cipher c = Cipher.getInstance(cipher);
			SecretKeySpec key = new SecretKeySpec(MD5.md5(secret).getBytes(),
					"AES");
			IvParameterSpec iv = new IvParameterSpec(ivData);
			c.init(Cipher.DECRYPT_MODE, key, iv);
			data = c.doFinal(data);

			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			Data item = (Data) serializer.read(bais);
			bais.close();

			// log.debug("Decrypted item: {}", item);
			return item;
		} catch (Exception e) {
			log.error("Decryption failed: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return input;
		}
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
}
