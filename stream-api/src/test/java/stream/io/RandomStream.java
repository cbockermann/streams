/**
 * 
 */
package stream.io;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.Processor;

/**
 * @author chris
 * 
 */
public class RandomStream implements DataStream {

	final List<Processor> processors = new ArrayList<Processor>();
	final Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();

	Random rnd = new Random();

	public RandomStream() {
		this(System.nanoTime());
	}

	public RandomStream(Long seed) {
		rnd = new Random(seed);
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(new DataImpl());
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data item) throws Exception {
		item.clear();

		for (String key : attributes.keySet()) {
			item.put(key, rnd.nextDouble());
		}

		for (Processor proc : processors) {
			item = proc.process(item);
		}

		return item;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.DataStream#addPreprocessor(stream.data.DataProcessor)
	 */
	@Override
	public void addPreprocessor(Processor proc) {
		processors.add(proc);
	}

	/**
	 * @see stream.io.DataStream#addPreprocessor(int, stream.data.DataProcessor)
	 */
	@Override
	public void addPreprocessor(int idx, Processor proc) {
		processors.add(idx, proc);
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	public static void main(String[] args) throws Exception {
		RandomStream stream = new RandomStream();
		stream.getAttributes().put("x1", Double.class);
		stream.getAttributes().put("x2", Double.class);

		JSONStreamWriter writer = new JSONStreamWriter(new File(
				"/Users/chris/test.json"));

		int id = 1;
		int i = 100;
		while (i-- > 0) {
			Data item = stream.readNext();
			item.put("@id", id++);
			System.out.println("Writing out item " + item);
			writer.process(item);
		}

	}
}