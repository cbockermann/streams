package stream.runtime.shutdown;


public interface ShutdownCondition {

	public abstract boolean isMet(DependencyGraph graph);

	public abstract void waitForCondition(DependencyGraph graph);

}