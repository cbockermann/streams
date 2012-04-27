/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * @author chris
 * 
 */
@Description(group = "Data Streams.Output")
public class LineWriter extends AbstractProcessor {

	File file;
	boolean append = false;
	String[] keys = null;
	PrintStream out;

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
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
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
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		out = new PrintStream(new FileOutputStream(file, append));
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (input == null)
			return input;

		StringBuffer s = new StringBuffer();

		List<String> ks = null;

		if (keys == null)
			ks = new ArrayList<String>(input.keySet());
		else
			ks = Arrays.asList(keys);

		Iterator<String> it = ks.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Serializable value = input.get(key);
			if (value == null) {
				s.append("?");
			} else {
				String val = value.toString();
				while (val.indexOf("\n") >= 0) {
					val = val.replace("\n", "\\n");
				}
				s.append(val);
			}
			if (it.hasNext())
				s.append(" ");
		}
		out.println(s.toString());
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
