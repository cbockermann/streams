package stream.learner.tree;

import java.util.Map;

import stream.learner.Regressor;

public interface LinearRegression extends Regressor {
	public void setParameters(Map<String, Object> parameters);

}
