/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.ProcessorList;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class DataStreamProcess implements DataStream {

	static Logger log = LoggerFactory.getLogger(DataStreamProcess.class);
	final ProcessorList processors = new ProcessorList();
	Process process;
	DataStream dataStream;
	Class<? extends DataStream> dataStreamClass = stream.io.CsvStream.class;

	String format;
	String command;

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return null;
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
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
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		process = runtime.exec(command);

		Constructor<? extends DataStream> stream = dataStreamClass
				.getConstructor(InputStream.class);
		InputStream input = process.getInputStream();
		dataStream = stream.newInstance(input);
	}

	/**
	 * @param format
	 *            the format to set
	 */
	@Parameter(required = true, values = { "stream.io.CsvStream" })
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {

		if (process == null) {
			init();
		}

		return dataStream.readNext(datum);
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		if (process != null) {
			process.destroy();
		}
	}

	/**
	 * @see stream.io.DataStream#addPreprocessor(stream.Processor)
	 */
	@Override
	public void addPreprocessor(Processor proc) {
		processors.addProcessor(proc);
	}

	/**
	 * @see stream.io.DataStream#addPreprocessor(int, stream.Processor)
	 */
	@Override
	public void addPreprocessor(int idx, Processor proc) {
		processors.addProcessor(idx, proc);
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<Processor> getPreprocessors() {
		return processors.getProcessors();
	}
}