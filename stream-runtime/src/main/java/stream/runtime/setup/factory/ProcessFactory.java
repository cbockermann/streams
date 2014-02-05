package stream.runtime.setup.factory;

import org.w3c.dom.Element;

import stream.util.Variables;

/**
 * @author hendrik
 * 
 */

public interface ProcessFactory {

	public ProcessConfiguration[] createConfigurations(Element e, Variables v);
	
	public void createAndRegisterProcesses(ProcessConfiguration[] configs) throws Exception;	
	
//	public List<Processor> createNestedProcessors(Element child, Variables local) throws Exception;






}
