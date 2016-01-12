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
package stream.io;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;

/**
 * @author chris
 */
@Description(name = "Stream Process (exec)", group = "Data Stream.Sources")
public class ProcessStream extends AbstractLineStream {

	static Logger log = LoggerFactory.getLogger(ProcessStream.class);
	protected Process process;
	protected Stream dataStream;
	protected Class<? extends Stream> dataStreamClass = stream.io.CsvStream.class;

	protected String format;
	protected String command;

	/**
	 * @param url
	 */
	public ProcessStream() {
		super((SourceURL) null);
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command
	 *            the command to set
	 */
	@Parameter(required = true, description = "The command to execute. This command will be spawned and is assumed to output data to standard output.")
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void init() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		process = runtime.exec(command);

		dataStreamClass = (Class<? extends Stream>) Class.forName(format);
		Constructor<? extends Stream> stream = dataStreamClass
				.getConstructor(InputStream.class);
		InputStream input = process.getInputStream();
		dataStream = stream.newInstance(input);
		dataStream.init();
	}

	/**
	 * @param format
	 *            the format to set
	 */
	@Parameter(required = true, values = { "stream.io.CsvStream",
			"stream.io.SvmLight", "stream.io.JSONStream",
			"stream.io.LineStream" }, defaultValue = "stream.io.CsvStream", description = "The format of the input (standard input), defaults to CSV")
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	@Override
	public Data readNext() throws Exception {

		if (process == null) {
			init();
		}

		return dataStream.read();
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	@Override
	public void close() {
		if (process != null) {
			process.destroy();
		}
	}
}