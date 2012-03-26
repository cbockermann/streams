/**
 * 
 */
package stream.io;

import java.net.URL;
import java.net.URLConnection;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class HttpDataStreamUploader extends DataStreamWriter {

	URL url;

	public HttpDataStreamUploader(URL url) {
		this.url = url;
	}

	/**
	 * @see stream.io.DataStreamWriter#writeHeader(stream.data.Data)
	 */
	@Override
	public void writeHeader(Data datum) {
	}

	/**
	 * @see stream.io.DataStreamWriter#write(stream.data.Data)
	 */
	@Override
	public void write(Data datum) {
	}

	/**
	 * @see stream.io.DataStreamWriter#dataArrived(stream.data.Data)
	 */
	@Override
	public void dataArrived(Data datum) {

		try {
			URLConnection con = url.openConnection();
			con.setDoInput(false);
			con.setDoOutput(true);

			DataStreamWriter writer = new DataStreamWriter(
					con.getOutputStream());

			writer.dataArrived(datum);
			writer.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.dataArrived(datum);
	}
}
