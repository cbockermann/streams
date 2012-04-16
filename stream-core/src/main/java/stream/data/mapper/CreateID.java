package stream.data.mapper;

import stream.AbstractProcessor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(text = "This processor tags all processed items with integer IDs.", group = "Data Stream.Processing.Annotations")
public class CreateID extends AbstractProcessor implements IDService {
	Long start = 0L;
	Long nextId = 0L;
	String key = "@id";

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (key != null) {
			synchronized (nextId) {
				data.put(key, nextId++);
			}
		}

		return data;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(defaultValue = "@id")
	public void setKey(String key) {
		this.key = key;
	}

	@Parameter(defaultValue = "0")
	public void setStart(Long l) {
		start = l;
		nextId = start;
	}

	public Long getStart() {
		return start;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		nextId = start;
	}
}