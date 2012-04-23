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
package stream.plugin.test;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.CsvStream;
import stream.io.SQLWriter;
import stream.plugin.processing.DataStreamProcess;
import stream.plugin.util.ParameterTypeDiscovery;

import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
public class ParameterDiscoveryTest {

	static Logger log = LoggerFactory.getLogger(ParameterDiscoveryTest.class);

	@Test
	public void test() {

		log.info("");
		log.info("");
		log.info("Checking class {}", CsvStream.class);
		log.info("");
		Map<String, ParameterType> types = ParameterTypeDiscovery
				.discoverParameterTypes(stream.io.CsvStream.class);
		for (String key : types.keySet()) {
			log.info("{} => {}", key, types.get(key));
		}

		log.info("");
		log.info("");
		log.info("Checking class {}", DataStreamProcess.class);
		log.info("");
		types = ParameterTypeDiscovery
				.discoverParameterTypes(DataStreamProcess.class);

		for (String key : types.keySet()) {
			log.info("{} => {}", key, types.get(key));
		}

		log.info("");
		log.info("");
		log.info("Checking class {}", SQLWriter.class);
		log.info("");
		types = ParameterTypeDiscovery.discoverParameterTypes(SQLWriter.class);

		for (String key : types.keySet()) {
			log.info("{} => {}", key, types.get(key));
		}

		// fail("Not yet implemented");
	}
}
