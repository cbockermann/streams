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
 * This interface is implemented by processors requiring a context. The context
 * is provided by the runtime-environment. This interface is an extension of the
 * {@link Processor} interface and additionally provides a 3-way lifecycle:
 * 
 * <ol>
 * <li>The {@link #init(ProcessContext)} method is called at initialization
 * time, <i>after</i> all parameters (getters/setters) have been called.</li>
 * <li>The {@link #process(stream.data.Data)} method is called for each data
 * item that this processor has to work on.</li>
 * <li>After the stream is finished, the runtime ensures that the
 * {@link #finish()} method is called before the runtime is shut down.</li>
 * </ol>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface StatefulProcessor extends Processor {

	/**
	 * This method is called at initialization time before any item is given to
	 * the processor.
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void init(ProcessContext context) throws Exception;

	/**
	 * This method can be called from outside to reset the state of the
	 * processor to what can be regarded as the
	 * <em>state after initialization</em>.
	 * 
	 * For learning algorithms that can be an empty model or zero counters for
	 * counting algorithms or the like.
	 * 
	 * @throws Exception
	 */
	public void resetState() throws Exception;

	/**
	 * This method is called when the last item of a stream has been processed.
	 * 
	 * @throws Exception
	 */
	public void finish() throws Exception;
}
