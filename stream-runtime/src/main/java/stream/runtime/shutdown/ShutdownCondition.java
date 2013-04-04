package stream.runtime.shutdown;

import stream.ComputeGraph;


public interface ShutdownCondition {

	public abstract boolean isMet(ComputeGraph graph);

	public abstract void waitForCondition(ComputeGraph graph);

}