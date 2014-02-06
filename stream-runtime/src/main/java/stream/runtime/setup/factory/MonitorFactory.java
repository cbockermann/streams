package stream.runtime.setup.factory;

import stream.runtime.DependencyInjection;
import stream.runtime.ProcessContainer;

/**
 * @author hendrik
 * 
 */
public class MonitorFactory extends DefaultProcessFactory {

	public MonitorFactory(ProcessContainer processContainer,
			ObjectFactory objectFactory, DependencyInjection dependencyInjection) {
		super(processContainer, objectFactory, dependencyInjection);
		this.defaultProcessImplementation = "stream.runtime.Monitor";
		this.processType = "monitor";
	}

}
