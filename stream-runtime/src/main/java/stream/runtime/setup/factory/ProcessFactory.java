package stream.runtime.setup.factory;

import java.util.List;

import org.w3c.dom.Element;

import stream.Processor;
import stream.util.Variables;

/**
 * @author hendrik
 * 
 */

public interface ProcessFactory {

	public ProcessConfiguration[] createConfigurations(Element e, Variables v);
	
	public void createAndRegisterProcesses(ProcessConfiguration[] configs) throws Exception;	
	
	public List<Processor> createNestedProcessors(Element child, Variables local) throws Exception;






}
