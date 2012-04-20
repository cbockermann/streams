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
package stream.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;
import stream.data.DataUtils;
import stream.data.mapper.HideKey;
import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterDiscovery;
import stream.runtime.setup.ParameterInjection;

public class ParameterDiscoveryTest {

	static Logger log = LoggerFactory.getLogger(ParameterDiscoveryTest.class);

	@Test
	public void testDiscover() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startId", new Long(100L));
		params.put("key", "x");
		HideKey proc = new HideKey();

		try {

			Map<String, Class<?>> keys = ParameterDiscovery
					.discoverParameters(proc.getClass());
			log.info("Discovered parameters: {}", keys);

			ParameterInjection.inject(proc, params, new VariableContext());

			Data datum = DataFactory.create();
			datum.put("x", 1.0d);
			datum.put("y", 2.10d);

			log.info("Initial datum: {}", datum);
			datum = proc.process(datum);
			log.info("Processed datum: {}", datum);

			for (String key : datum.keySet()) {
				if (key.endsWith("x"))
					Assert.assertTrue(DataUtils.isHidden(key));
			}

		} catch (Exception e) {
			Assert.fail("Failed: " + e.getMessage());
		}
	}
}