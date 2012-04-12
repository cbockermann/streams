package stream.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.CsvStream;
import stream.io.DataStream;
import stream.io.HttpDataStreamUploader;
import stream.io.JSONStream;

public class DataUpload {

	static Logger log = LoggerFactory.getLogger(DataUpload.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = null;
		String remoteUrl = "rmi://kirmes.cs.uni-dortmund.de:9106/DataIndex";

		if (args.length > 0)
			url = new URL(args[0]);
		else
			url = new URL("http://kirmes.cs.uni-dortmund.de/data/adult.tr");

		if (args.length > 1)
			remoteUrl = args[1];

		DataStream stream = null;
		String fmt = url.toString();
		if (fmt.endsWith(".csv") || fmt.endsWith(".csv.gz")) {
			stream = new CsvStream(url);
		}

		if (fmt.endsWith(".json") || fmt.endsWith(".json.gz")) {
			stream = new JSONStream(url);
		}

		if (stream == null) {
			System.err.println("Failed to detect data stream format...");
			System.exit(-1);
		}

		int delay = -1;
		try {
			delay = new Integer(System.getProperty("delay"));
		} catch (Exception e) {
			delay = -1;
		}

		HttpDataStreamUploader upload = new HttpDataStreamUploader(new URL(
				remoteUrl));
		int limit = 10000;
		int i = 0;
		Data item = stream.readNext();
		while (item != null && limit-- > 0) {

			if (i > 0 && i % 100 == 0) {
				log.info("{} items inserted.", i);
			}

			upload.dataArrived(item);

			if (delay > 0) {
				Thread.sleep(delay);
			}

			item = stream.readNext();
			i++;
		}
	}
}
