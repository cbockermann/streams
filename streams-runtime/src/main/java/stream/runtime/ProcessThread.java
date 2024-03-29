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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streams.runtime.Hook;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public class ProcessThread extends Thread implements Hook {

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
	final ApplicationContext context;

	final AtomicBoolean executing = new AtomicBoolean(false);
	boolean running = false;

	protected final List<ProcessListener> processListener = new ArrayList<ProcessListener>();

	public ProcessThread(stream.Process process, ApplicationContext ctx) {

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
		Exception error = null;
		try {

			log.debug("Starting process {}, notifying listeners {}", process, processListener);
			for (ProcessListener l : this.processListener) {
				log.debug("Calling process-listener {}", l);
				l.processStarted(process);
			}
			executing.set(true);
			process.execute();
			executing.set(false);
		} catch (InterruptedException ie) {
			log.error("Process thread interruped while executing!?");
			ie.printStackTrace();
			executing.set(false);
			error = ie;
		} catch (Exception e) {
			error = e;
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			log.error(exceptionDetails);
			// e.printStackTrace();

			for (ProcessListener l : this.processListener) {
				log.debug("Calling process-listener {} for error handling", l);
				l.processError(process, e);
			}

		} finally {
			executing.set(false);
			log.debug("ProcessThread shutting down...");

			try {
				log.debug("Finishing processors...");
				process.finish();
			} catch (Exception e) {
				log.error("Failed to finish: {}", e.getMessage());
				e.printStackTrace();
			}

			log.debug("Process {} finished, notifying listeners: {}", process, processListener);
			for (ProcessListener l : this.processListener) {
				log.debug(" Calling listener {}", l);
				l.processFinished(process);
			}

			running = false;

			if (error != null) {
				error.printStackTrace();
				throw new RuntimeException("Process stopped after exception: " + error.getMessage(), error.getCause());
			}
		}
	}

	/**
	 * @see streams.runtime.Hook#signal(int)
	 */
	@Override
	public void signal(int flags) {
		log.debug("Signaling ProcessThread {}", this);
		if (executing.get()) {
			log.debug("   process thread is executing, sending interrupt() signal");
			this.interrupt();
		}
	}
}
