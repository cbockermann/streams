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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.expressions.ExpressionResolver;

/**
 * A simple output processor that writes out data items to a file according to
 * some format string.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Output")
public class LineWriter extends AbstractProcessor {

	File file;
	boolean append = false;
	PrintStream out;
	String format = null;
	boolean escapeNewlines = true;

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	@Parameter(required = true, description = "Name of the file to write to.")
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	@Parameter(required = true, description = "The format string, containing macros that are expanded for each item")
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the append
	 */
	public boolean isAppend() {
		return append;
	}

	/**
	 * @param append
	 *            the append to set
	 */
	@Parameter(required = false, description = "Denotes whether to append to existing files or create a new file at container startup.", defaultValue = "false")
	public void setAppend(boolean append) {
		this.append = append;
	}

	/**
	 * @return the escapeNewlines
	 */
	public boolean isEscapeNewlines() {
		return escapeNewlines;
	}

	/**
	 * @param escapeNewlines
	 *            the escapeNewlines to set
	 */
	@Parameter(required = false, description = "Whether to escape newlines contained in the attributes or not.")
	public void setEscapeNewlines(boolean escapeNewlines) {
		this.escapeNewlines = escapeNewlines;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		out = new PrintStream(new FileOutputStream(file, append));
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (input == null || format == null)
			return input;

		String line = ExpressionResolver.expand(format, context, input);
		if (escapeNewlines) {
			while (line.indexOf("\n") >= 0) {
				line = line.replace("\n", "\\n");
			}
		}

		out.println(line);
		return input;
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
	}
}
