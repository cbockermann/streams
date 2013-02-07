/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class CyclicMockLineStream extends AbstractStream {

	static Logger log = LoggerFactory.getLogger(CyclicMockLineStream.class);
	int count = 0;

	ArrayList<String> lines;
	Integer lineCount = 100;

	public CyclicMockLineStream(URL source, int lineCount) throws IOException {
		super(new SourceURL(source));
		this.lineCount = lineCount;
	}

	/**
	 * @see stream.io.AbstractStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();

		lines = new ArrayList<String>(lineCount);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				this.getInputStream()));
		String line = reader.readLine();
		int i = 0;
		while (line != null && i++ < lineCount) {
			lines.add(line);
			line = reader.readLine();
		}
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data item = DataFactory.create();
		item.put("LINE", lines.get(count++ % lines.size()));
		return item;
	}
}