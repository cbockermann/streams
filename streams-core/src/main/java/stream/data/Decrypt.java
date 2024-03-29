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
package stream.data;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

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

			Data item;
			if ("json".equalsIgnoreCase("" + input.get(Encrypt.SERIALIZER_KEY))) {
				JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
				JSONObject object = (JSONObject) parser.parse(data);
				item = DataFactory.create();
				for (String k : object.keySet()) {
					item.put(k, (Serializable) object.get(k));
				}
			} else {
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				item = (Data) serializer.read(bais);
				bais.close();
			}

			log.debug("Decrypted item: {}", item);
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
