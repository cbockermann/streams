/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.plugin.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.DataStream;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.tools.Ontology;

/**
 * @author chris
 */
public class ExampleSetDataStreamWrapper implements DataStream {

	Map<String, Class<?>> types;
	Map<String, Attribute> attributes;
	ExampleSet exampleSet;
	int idx = 0;
	Iterator<Example> examples;

	public ExampleSetDataStreamWrapper(ExampleSet exampleSet) {
		this.exampleSet = exampleSet;

		types = new LinkedHashMap<String, Class<?>>();
		attributes = new LinkedHashMap<String, Attribute>();

		Iterator<Attribute> it = exampleSet.getAttributes().allAttributes();

		while (it.hasNext()) {

			Attribute attr = it.next();
			AttributeRole role = exampleSet.getAttributes().getRole(attr);
			String name = attr.getName();

			attributes.put(attr.getName(), attr);

			if (role.isSpecial() && !name.startsWith("@")) {
				name = "@" + name;
			}

			Class<?> type = String.class;

			switch (attr.getValueType()) {
			case Ontology.NUMERICAL:
				type = Double.class;
			}

			types.put(name, type);
		}

		examples = this.exampleSet.iterator();
	}

	public String mapAttributeName(Attribute attribute, Attributes attributes) {
		String name = attribute.getName();
		AttributeRole role = attributes.getRole(attribute);

		if (role.isSpecial() && !name.startsWith("@")) {
			return "@" + name;
		}

		return name;
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return types;
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {

		while (examples.hasNext()) {
			return this.wrap(datum, examples.next());
		}

		return null;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<Processor> getPreprocessors() {
		return new ArrayList<Processor>();
	}

	protected Data wrap(Data item, Example example) {

		for (String name : attributes.keySet()) {

			Attribute attribute = attributes.get(name);
			String key = mapAttributeName(attribute, example.getAttributes());

			if (attribute.getValueType() == Ontology.NUMERICAL) {
				double d = example.getValue(attribute);
				item.put(key, new Double(d));
			} else {
				item.put(key, example.getValueAsString(attribute));
			}
		}

		return item;
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
	}
}