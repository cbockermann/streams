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
package stream.node.runtime;

import java.io.File;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ProcessContainerThread extends Thread {
	File containerFile;
	ProcessContainer processContainer;

	public ProcessContainerThread(File containerFile, ProcessContainer pc) {
		this.containerFile = containerFile;
		this.processContainer = pc;
	}

	/**
	 * @return the processContainer
	 */
	public ProcessContainer getProcessContainer() {
		return processContainer;
	}

	/**
	 * @param processContainer
	 *            the processContainer to set
	 */
	public void setProcessContainer(ProcessContainer processContainer) {
		this.processContainer = processContainer;
	}

	public File getFile() {
		return containerFile;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			processContainer.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		processContainer.shutdown();
	}
}