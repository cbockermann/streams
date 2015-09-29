/**
 * 
 */
package streams.io;

import java.io.DataInputStream;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;

/**
 * @author chris
 *
 */
public class BobStream extends AbstractStream {

	static Logger log = LoggerFactory.getLogger(BobStream.class);

	DataInputStream in;
	long count = 0L;

	public BobStream(SourceURL url) {
		super(url);
	}

	/**
	 * @see stream.io.AbstractStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();

		this.in = new DataInputStream(getInputStream());
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {

		try {

			final byte[] block = BobCodec.readBlock(in);

			final Data item = DataFactory.create();
			item.put("data", block);
			count++;
			return item;
		} catch (Exception e) {
			log.error("Failed to read event #{}:  {}", count, e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public static void main(String[] args) throws Exception {

		SourceURL source = new SourceURL(args[0]);
		BobStream stream = new BobStream(source);
		stream.init();

		long start = System.currentTimeMillis();
		long bytesRead = 0L;
		Data item = stream.read();
		while (item != null) {
			byte[] block = (byte[]) item.get("data");
			bytesRead += (12 + block.length);
			item = stream.read();
		}
		long end = System.currentTimeMillis();
		Double secs = (end - start * 1.0d) / 1000.0d;
		Double dataRate = (bytesRead / 1024 / 1024) / secs;

		stream.close();
		DecimalFormat fmt = new DecimalFormat("0.0");
		log.info("Read {} bytes, ({} MB/sec)", bytesRead, fmt.format(dataRate));
	}
}