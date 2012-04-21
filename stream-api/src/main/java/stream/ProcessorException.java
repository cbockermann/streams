/**
 * 
 */
package stream;

/**
 * @author chris
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
