package stream.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;

/**
 * This class reads the Typo3-SLS files from last year's SQL experiments and
 * writes them into proper demo-shop-log like format.
 * 
 * @author chris@jwall.org
 * 
 */
public class Typo3SLSConverter {
	static Logger log = LoggerFactory.getLogger(Typo3SLSConverter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File input = new File("/Users/chris/typo3-attacks.sls");
		File output = new File("/Users/chris/typo3-attacks.csv");

		DataStreamWriter writer = new DataStreamWriter(new FileOutputStream(
				output), "___");

		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = reader.readLine();
		int count = 0;
		while (line != null) {
			count++;
			if (count % 100 == 0)
				log.info("{} items processed", count);
			String[] tok = line.split(" ", 2);

			Data item = new DataImpl();
			item.put("REQUEST_METHOD", "GET");
			item.put("REQUEST_URI", "/attack/");
			item.put("sql:database", "mysql");
			item.put("sql:query", tok[1]);
			item.put("sql:ok", "true");
			item.put("@timestamp", "" + System.currentTimeMillis());
			writer.process(item);
			line = reader.readLine();
		}

		reader.close();
		writer.finish();
	}
}
