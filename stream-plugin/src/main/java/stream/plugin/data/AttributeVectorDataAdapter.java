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
package stream.plugin.data;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataFactory;

import com.rapidminer.streaming.ioobject.AttributeValue;
import com.rapidminer.streaming.ioobject.AttributeVector;
import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;

/**
 * @author chris
 * 
 */
public class AttributeVectorDataAdapter {

	public static AttributeVector createAttributeVector(Data item) {
		AttributeVector v = new AttributeVector();
		for (String key : item.keySet()) {
			StreamingAttributeHeader header = ConventionMapping.map(key);
			v.setValue(header, new AttributeValue(item.get(key)));
		}
		return v;
	}

	public static Data createDataItem(AttributeVector vector) {
		Data item = DataFactory.create();
		for (StreamingAttributeHeader header : vector.getAttributeHeaders()) {
			item.put(ConventionMapping.map(header).toString(),
					(Serializable) vector.getAttributeValue(header).getRaw());
		}
		return item;
	}
}
