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
package stream;

/**
 * <p>
 * This class extends the RuntimeException and can be fired by processors while
 * processing events. In addition to the default properties of the super class,
 * it may provide a reference to the processor that fired the exception.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ProcessorException extends RuntimeException {

	/** The unique class ID */
	private static final long serialVersionUID = 8110866979842503200L;

	final Processor processor;

	public ProcessorException() {
		super();
		processor = null;
	}

	public ProcessorException(String msg) {
		this(null, msg);
	}

	public ProcessorException(Processor processor, String msg) {
		super(msg);
		this.processor = processor;
	}

	public Processor getProcessor() {
		return processor;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		if (processor != null) {
			s.append("[");
			s.append(processor.toString());
			s.append("] ");
		} else {
			s.append("[Unknown Processor] ");
		}
		s.append(super.toString());
		return s.toString();
	}
}
