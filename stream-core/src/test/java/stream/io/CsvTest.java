/**
 * 
 */
package stream.io;

import java.net.URL;

import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class CsvTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		CsvWriter writer = new CsvWriter(new URL("file:/tmp/test.csv"));

		for (int i = 0; i < 10; i++) {
			Data item = DataFactory.create();
			item.put("x", i + "");
			writer.process(item);
		}

		writer.finish();
	}

}
