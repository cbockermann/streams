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
package stream.plugin.sources;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.DataStream;
import stream.plugin.data.DataSourceObject;
import stream.plugin.util.OperatorUtils;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.runtime.setup.DataStreamFactory;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;

/**
 * <p>
 * This class implements a simple DataStreamReader that will be equipped with a
 * URL (parameter) and will provide a single, stateful data-stream object.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class DataStreamReader extends Operator {

	static Logger log = LoggerFactory.getLogger(DataStreamReader.class);
	public final static String INPUT_FILE = "url";
	public final static String LIMIT = "limit";
	final OutputPort output = getOutputPorts().createPort("stream");

	boolean setup = false;
	DataStream stream;
	Class<? extends DataStream> dataStreamClass;

	DataSourceObject dataSource = null;

	/**
	 * @param description
	 */
	public DataStreamReader(OperatorDescription description,
			Class<? extends DataStream> streamClass) {
		super(description);
		dataStreamClass = streamClass;
		producesOutput(DataSourceObject.class);
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		super.doWork();

		if (dataSource == null) {
			try {
				Map<String, String> params = OperatorUtils.getParameters(this);
				stream = createDataStream(dataStreamClass, params);
				// dataSource = new DataSourceObject( stream );
				output.deliver(new DataSourceObject(stream));
			} catch (Exception e) {
				throw new OperatorException("Failed to create data-source!", e);
			}
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		// types.add(new ParameterTypeFile(INPUT_FILE, "The file to read from",
		// "", false));
		boolean fileParam = false;
		try {
			Constructor<?> con = dataStreamClass.getConstructor(URL.class);
			if (con != null) {
				fileParam = true;
				types.add(new ParameterTypeFile(INPUT_FILE,
						"The file to read from", "", false));
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
		}

		types.add(new ParameterTypeInt(LIMIT,
				"The maximum number of items read from the stream", -1,
				Integer.MAX_VALUE, true));

		Map<String, ParameterType> discovered = ParameterTypeDiscovery
				.discoverParameterTypes(dataStreamClass);
		for (String key : discovered.keySet()) {
			if (fileParam
					&& ("url".equalsIgnoreCase(key) || "file"
							.equalsIgnoreCase(key))) {
				log.debug("File/URL parameter-type already added!");
				continue;
			}
			types.add(discovered.get(key));
		}

		return types;
	}

	public DataStream createDataStream(
			Class<? extends DataStream> dataStreamClass,
			Map<String, String> parameters) throws Exception {
		parameters.put("class", dataStreamClass.getName());

		try {
			DataStream stream = (DataStream) DataStreamFactory
					.createStream(parameters);
			return stream;
		} catch (Exception e) {
			log.error("Failed to create stream: {}", e.getMessage());
			String url = parameters.get("url");
			parameters.put("url", "file:" + url);
			DataStream stream = (DataStream) DataStreamFactory
					.createStream(parameters);
			return stream;
		}
	}
}
