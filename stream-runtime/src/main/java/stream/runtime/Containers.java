/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
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
package stream.runtime;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hendrik Blom
 * 
 */
public class Containers extends LinkedHashMap<String, Container> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(Containers.class);

	ExecutorService pool;

	public Containers() {
		super();
		pool = Executors.newCachedThreadPool();
	}

	public Containers(int n) {
		super();
		pool = Executors.newFixedThreadPool(n);
	}

	public Future<Boolean> start(String name) {
		Container c = get(name);
		if (c == null)
			return null;
		Future<Boolean> f = pool.submit(c);
		return f;
	}

	public boolean shutdown(String name) {
		Container c = get(name);
		if (c == null)
			return false;
		c.shutdown();
		return true;
	}

	public boolean shutdown() {
		pool.shutdown();

		int c = 0;
		try {
			while (c < 10 && !pool.awaitTermination(10, TimeUnit.SECONDS)) {
				c++;
				log.info("Awaiting completion of threads.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (c == 10) {
			log.info("Can't stop pool. Killed...");
			pool.shutdownNow();
			return false;
		}
		return true;
	}

}
