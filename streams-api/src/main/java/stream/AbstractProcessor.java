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
package stream;

/**
 * <p>
 * This class implements some basic methods of the {@link StatefulProcessor}
 * interface and can serve as basis for custom processor implementations.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class AbstractProcessor implements StatefulProcessor {

	/** The process context provided at initialization time. */
	protected transient ProcessContext context;

	/**
	 * @see StatefulProcessor#init(ProcessContext)
	 *
	 * @param ctx	The process context.
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		context = ctx;
	}

	/**
	 * @see stream.StatefulProcessor#resetState()
	 *
	 */
	@Override
	public void resetState() throws Exception {
	}

	/**
	 * @see stream.StatefulProcessor#finish()
	 * 
	 */
	@Override
	public void finish() throws Exception {
	}
}
