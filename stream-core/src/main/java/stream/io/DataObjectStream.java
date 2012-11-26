/**
 * 
 */
package stream.io;

import java.io.InputStream;
import java.io.ObjectInputStream;

import stream.Data;

/**
 * @author chris
 * 
 */
public class DataObjectStream extends AbstractStream {

	ObjectInputStream input;

	public DataObjectStream(SourceURL url) {
		super(url);
	}

	public DataObjectStream(InputStream in) {
		super(in);
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	@Override
	public void close() throws Exception {
		input.close();
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		input = new ObjectInputStream(getInputStream());
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		return (Data) input.readObject();
	}
}
