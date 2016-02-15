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
package stream.container;

import java.util.List;
import java.util.Set;

import stream.Context;
import stream.Process;
import stream.io.Source;
import stream.runtime.ServiceReference;
import stream.service.NamingService;
import stream.util.Variables;
import streams.application.ComputeGraph;

/**
 * @author chris, Hendrik Blom
 *
 */
public interface IContainer {

	public abstract ComputeGraph computeGraph();

	public abstract Set<Source> getStreams();

	/**
	 * @return the name
	 */
	public abstract String getName();

	public abstract Context getContext();

	/**
	 * @return the processes
	 */
	public abstract List<Process> getProcesses();

	/**
	 * @return the serviceRefs
	 */
	public abstract List<ServiceReference> getServiceRefs();

	public abstract Variables getVariables();
	
	public abstract NamingService getNamingService();

}