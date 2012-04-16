/**
 * 
 */
package stream.plugin;


import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 *
 */
public class GenericStreamWriter extends Operator {
	
	final InputPort input = getInputPorts().createPort( DataStreamPlugin.DATA_ITEM_PORT_NAME );
	final OutputPort output = getOutputPorts().createPort( DataStreamPlugin.DATA_ITEM_PORT_NAME );

	/**
	 * @param description
	 */
	public GenericStreamWriter(OperatorDescription description) {
		super(description);
	}
}