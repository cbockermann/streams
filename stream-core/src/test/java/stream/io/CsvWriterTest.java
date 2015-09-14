/**
 * 
 */
package stream.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Keys;
import stream.data.DataFactory;
import stream.runtime.ProcessContextImpl;

/**
 * @author chris
 * 
 */
public class CsvWriterTest {

	static Logger log = LoggerFactory.getLogger(CsvWriterTest.class);

	public static Data create(String str) {
		Data item = DataFactory.create();
		String[] parts = str.split(",");
		for (String part : parts) {
			String[] kv = part.split("=", 2);
			try {
				item.put(kv[0], new Double(kv[1]));
			} catch (Exception e) {
				item.put(kv[0], kv[1]);
			}
		}
		return item;
	}

	@Test
	public void test() {

		List<Data> items = new ArrayList<Data>();
		items.add(create("x1=1.0,x2=2.0,x3=3.0"));

		try {
			File tmp = File.createTempFile("test-csv-writer", ".csv");
			log.info("Writing test-data to {}", tmp);
			tmp.deleteOnExit();

			CsvWriter writer = new CsvWriter();

			writer.setUrl("file:" + tmp.getAbsolutePath());
			writer.setKeys(new Keys("*,!x3"));
			writer.init(new ProcessContextImpl());

			for (Data item : items) {
				writer.process(item);
			}

			writer.finish();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
