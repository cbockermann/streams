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
import java.util.List;

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
	final stream.Process process;
	final ProcessContext context;
	boolean running = false;

	protected final List<ProcessListener> processListener = new ArrayList<ProcessListener>();

	public ProcessThread(stream.Process process, ProcessContext ctx) {
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

			for (ProcessListener l : this.processListener) {
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

			for (ProcessListener l : this.processListener) {
				l.processFinished(process);
			}

			running = false;
		}
	}
}
