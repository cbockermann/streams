/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import net.minidev.json.JSONObject;
import stream.data.Data;

/**
 * <p>
 * This is a simple JSON writer that will write all data items into JSON strings
 * (one line for each item).
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class JSONStreamWriter extends DataStreamWriter {

	public JSONStreamWriter(File file) throws IOException {
		super(file);
	}

	public JSONStreamWriter(URL url) throws Exception {
		super(url);
	}

	public JSONStreamWriter(OutputStream out) throws Exception {
		super(out);
	}

	/**
	 * @see stream.io.DataStreamWriter#writeHeader(stream.data.Data)
	 */
	@Override
	public void writeHeader(Data datum) {
		//
		// we overwrite this method to ensure no data-header is
		// written by the super-class
		//
	}

	/**
	 * @see stream.io.DataStreamWriter#write(stream.data.Data)
	 */
	@Override
	public void write(Data datum) {
		p.println(JSONObject.toJSONString(datum));
	}
}
