/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataImpl;
import stream.runtime.annotations.Parameter;

/**
 * @author chris
 * 
 */
public abstract class AbstractDataStream implements DataStream {
	static Logger log = LoggerFactory.getLogger(AbstractDataStream.class);

	URL url;
	String username;
	String password;
	LinkedHashMap<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();
	BufferedReader reader;
	Long limit = -1L;
	Long count = 0L;
	String prefix = null;

	ArrayList<Processor> preprocessors = new ArrayList<Processor>();

	protected AbstractDataStream() {
	}

	public AbstractDataStream(URL url) throws Exception {
		this.url = url;
		// initReader();
	}

	public AbstractDataStream(URL url, String username, String password)
			throws Exception {
		this.url = url;
		this.username = username;
		this.password = password;
		this.initReader();
	}

	protected void initReader() throws Exception {

		if (username != null && password != null) {
			Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});
		}

		if (url.getFile().endsWith(".gz"))
			reader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(url.openStream())));
		else
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
		readHeader();
	}

	public AbstractDataStream(InputStream in) throws Exception {
		reader = new BufferedReader(new InputStreamReader(in));
		// readHeader();
	}

	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	@Parameter(name = "username", required = false)
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	@Parameter(name = "password", required = false)
	public void setPassword(String password) {
		this.password = password;
	}

	public List<Processor> getPreprocessors() {
		return this.preprocessors;
	}

	public void addPreprocessor(Processor proc) {
		preprocessors.add(proc);
	}

	public void addPreprocessor(int idx, Processor proc) {
		preprocessors.add(idx, proc);
	}

	public boolean removePreprocessor(Processor proc) {
		return preprocessors.remove(proc);
	}

	public Processor removePreprocessor(int idx) {
		return preprocessors.remove(idx);
	}

	/**
     * 
     */
	public abstract void readHeader() throws Exception;

	public abstract Data readItem(Data instance) throws Exception;

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	public final Data readNext(Data item) throws Exception {

		if (limit > 0 && count >= limit)
			return null;

		Data datum = null;
		while (datum == null) {

			//
			// If the source is empty (i.e. readItem(..) returned null), we
			// cannot continue, so we leave by returning null
			//
			datum = readItem(item);
			if (datum == null) {
				log.debug("End-of-stream reached!");
				return null;
			}

			if (prefix != null) {
				Data prefixed = new DataImpl();
				for (String key : datum.keySet()) {
					prefixed.put(prefix + ":" + key, datum.get(key));
				}
				datum = prefixed;
			}

			//
			// Hand over the item to all pre-processors. If one of them
			// discards the item, we will continue reading the next one.
			//
			for (Processor proc : preprocessors) {
				datum = proc.process(datum);
				if (datum == null)
					break;
			}
		}
		count++;
		return datum;
	}

	public Data readNext() throws Exception {
		return readNext(new DataImpl());
	}
}