/**
 * 
 */
package stream.plugin.processing.convert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataUtils;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.tools.Ontology;

/**
 * <p>
 * This class provides a factory to create example-sets from data items while
 * still maintaining the nominal mapping, i.e. attributes are shared among
 * multiple example sets created with the same factory instance.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann&gt;
 * 
 */
public class ExampleSetFactory {

	static Logger log = LoggerFactory.getLogger(ExampleSetFactory.class);
	Attribute[] attributeArray = null;

	final static DataRowFactory drf = new DataRowFactory(
			DataRowFactory.TYPE_DOUBLE_ARRAY, '.');

	public static ExampleSetFactory newInstance() {
		return new ExampleSetFactory();
	}

	private ExampleSetFactory() {
	}

	private Attribute[] getAttributes(Data item) {

		if (attributeArray != null)
			return attributeArray;

		Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();

		for (String key : item.keySet()) {

			Serializable s = item.get(key);
			if (Number.class.isAssignableFrom(s.getClass())) {
				attributes.put(key, Double.class);
			} else {
				attributes.put(key, String.class);
			}
		}

		attributeArray = new Attribute[attributes.size()];

		int i = 0;
		for (String key : attributes.keySet()) {

			int type = Ontology.NUMERICAL;

			if (String.class.equals(attributes.get(key))) {
				type = Ontology.NOMINAL;
			}

			Attribute attr = AttributeFactory.createAttribute(key, type);
			attributeArray[i++] = attr;
		}

		return attributeArray;
	}

	public ExampleSet createExampleSet(Data item) {
		List<Data> items = new ArrayList<Data>();
		items.add(item);
		return createExampleSet(items);
	}

	public ExampleSet createExampleSet(Collection<Data> items) {

		if (items.isEmpty()) {
			log.error("Cannot create ExampleSet from empty collection of data items!");
			return null;
		}

		Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();

		for (Data item : items) {
			for (String key : item.keySet()) {

				Serializable s = item.get(key);
				if (Number.class.isAssignableFrom(s.getClass())) {
					attributes.put(key, Double.class);
				} else {
					attributes.put(key, String.class);
				}
			}
		}

		log.debug("Incoming data stream contains {} examples", items.size());

		Data first = items.iterator().next();

		Attribute[] attributeArray = getAttributes(first);
		if (attributeArray == null) {
			log.error("Failed to determine attributes for item {}", first);
		}

		MemoryExampleTable table = new MemoryExampleTable();
		int i = 0;

		for (int j = 0; j < attributeArray.length; j++) {
			table.addAttribute(attributeArray[j]);
		}

		for (Data datum : items) {
			String[] data = new String[attributeArray.length];
			i = 0;
			for (String key : attributes.keySet()) {

				if (datum.get(key) == null)
					data[i] = "?";
				else
					data[i] = datum.get(key).toString();
				i++;
			}

			while (i < attributeArray.length)
				data[i++] = "?";

			table.addDataRow(drf.create(data, attributeArray));
		}

		ExampleSet exampleSet = table.createExampleSet();

		List<Attribute> attributeSet = new ArrayList<Attribute>();
		for (Attribute attr : exampleSet.getAttributes())
			attributeSet.add(attr);

		for (Attribute attr : attributeSet) {
			if (attr.getName().startsWith("@id")) {
				exampleSet.getAttributes().setId(attr);
				continue;
			}

			if (attr.getName().startsWith("@label")) {
				exampleSet.getAttributes().setLabel(attr);
				continue;
			}

			if (DataUtils.isAnnotation(attr.getName())) {
				exampleSet.getAttributes().setSpecialAttribute(attr,
						attr.getName());
			}
		}

		return exampleSet;
	}

	public void reset() {
		attributeArray = null;
	}
}
