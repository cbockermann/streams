/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public abstract class AbstractDataStream implements DataStream {
	static Logger log = LoggerFactory.getLogger(AbstractDataStream.class);

	protected URL url;
	protected String username;
	protected String password;
	protected LinkedHashMap<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();
	protected BufferedReader reader;
	protected Long limit = -1L;
	protected Long count = 0L;
	protected String prefix = null;
	protected String id;

	protected ArrayList<Processor> preprocessors = new ArrayList<Processor>();

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

	public AbstractDataStream(InputStream in) throws Exception {
		reader = new BufferedReader(new InputStreamReader(in));
		// readHeader();
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	@Parameter(required = false, description = "An optional prefix string to prepend to all attribute names", defaultValue = "")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Long getLimit() {
		return limit;
	}

	@Parameter(required = false, description = "The maximum number of items that this stream should deliver", defaultValue = "-1", max = Long.MAX_VALUE)
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
	@Parameter(name = "username", description = "The username required to connect to the stream URL (e.g web-user, database user)", required = false)
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
	@Parameter(name = "password", description = "The password for the stream URL (see username parameter)", required = false)
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
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
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
			datum.put("@stream", this.id);
			if (datum == null) {
				log.debug("End-of-stream reached!");
				return null;
			}

			if (prefix != null && !prefix.trim().isEmpty()) {
				Data prefixed = DataFactory.create();
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
		return readNext(DataFactory.create());
	}
}