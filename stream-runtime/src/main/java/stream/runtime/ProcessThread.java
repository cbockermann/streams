/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public class ProcessThread extends Thread {

	static Logger log = LoggerFactory.getLogger(ProcessThread.class);

	static final Map<String, Integer> PRIORITY_NAMES = new LinkedHashMap<String, Integer>();
	static {
		PRIORITY_NAMES.put("lowest", Thread.MIN_PRIORITY);
		PRIORITY_NAMES.put("low", 2);
		PRIORITY_NAMES.put("normal", Thread.NORM_PRIORITY);
		PRIORITY_NAMES.put("high", 7);
		PRIORITY_NAMES.put("highest", Thread.MAX_PRIORITY);
	}

	final stream.Process process;
	final ProcessContext context;
	boolean running = false;

	protected final List<ProcessListener> processListener = new ArrayList<ProcessListener>();

	public ProcessThread(stream.Process process, ProcessContext ctx) {

		Integer prio = Thread.NORM_PRIORITY;
		try {
			String prioValue = process.getProperties().get("priority");
			if (prioValue == null)
				prioValue = "normal";

			if (PRIORITY_NAMES.containsKey(prioValue)) {
				prioValue = PRIORITY_NAMES.get(prioValue).toString();
			}

			prio = new Integer(prioValue);
		} catch (Exception e) {
			prio = Thread.NORM_PRIORITY;
		}

		if (prio > Thread.MAX_PRIORITY)
			prio = Thread.MAX_PRIORITY;

		if (prio < Thread.MIN_PRIORITY)
			prio = Thread.MIN_PRIORITY;

		this.setPriority(prio);
		this.process = process;
		this.context = ctx;
	}

	public void addListener(ProcessListener l) {
		processListener.add(l);
	}

	public void removeListener(ProcessListener l) {
		processListener.remove(l);
	}

	public boolean isRunning() {
		return running;
	}

	public stream.Process getProcess() {
		return process;
	}

	public void init() throws Exception {
		log.debug("Initializing process with process-context...");
		process.init(context);
	}

	public void run() {
		running = true;
		try {

			log.debug("Starting process {}, notifying listeners {}", process,
					processListener);
			for (ProcessListener l : this.processListener) {
				log.debug("Calling process-listener {}", l);
				l.processStarted(process);
			}

			process.execute();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				process.finish();
			} catch (Exception fe) {
				fe.printStackTrace();
			}
		} finally {

			try {
				log.debug("Process {} finished, notifying listeners: {}",
						process, processListener);
				for (ProcessListener l : this.processListener) {
					log.debug("   Calling listener {}", l);
					l.processFinished(process);
				}
			} catch (Exception e) {
				log.error("Failed to call process listeners: {}",
						e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}

			running = false;
		}
	}
}
