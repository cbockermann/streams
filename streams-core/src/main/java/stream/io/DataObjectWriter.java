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
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class DataObjectWriter extends AbstractWriter {

	File file;
	ObjectOutputStream out;
	long cnt = 0L;
	Integer reset = 25;

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
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (file.getAbsolutePath().endsWith(".gz")) {
			out = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(file, true)));
		} else
			out = new ObjectOutputStream(new FileOutputStream(file, true));
	}

	/**
	 * @see stream.io.AbstractWriter#write(stream.Data)
	 */
	@Override
	public void write(Data input) {
		if (input != null) {
			try {

				Data store = DataFactory.create();
				for (String key : selectedKeys(input)) {
					store.put(key, input.get(key));
				}

				out.writeObject(store);
				this.cnt++;
				if (cnt % reset == 0) {
					out.reset();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
		out.flush();
		out.close();
	}

	/**
	 * @return the reset
	 */
	public Integer getReset() {
		return reset;
	}

	/**
	 * @param reset
	 *            the reset to set
	 */
	public void setReset(Integer reset) {
		this.reset = Math.max(1, reset);
	}
}