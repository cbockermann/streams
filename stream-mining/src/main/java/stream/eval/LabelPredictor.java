/**
 * 
 */
package stream.eval;

import java.io.Serializable;

import stream.data.Data;
import stream.learner.Learner;
import stream.model.PredictionModel;
import stream.runtime.Context;

/**
 * @author chris
 * 
 */
public class LabelPredictor<T extends Serializable> implements
		Learner<PredictionModel<T>> {

	/** The unique class ID */
	private static final long serialVersionUID = 1238284089229472027L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.data.DataProcessor#init(stream.data.Context)
	 */
	@Override
	public void init(Context context) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.data.DataProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.learner.ModelProvider#getModel()
	 */
	@Override
	public PredictionModel<T> getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.learner.Learner#init()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.learner.Learner#learn(stream.data.Data)
	 */
	@Override
	public void learn(Data item) {
		// TODO Auto-generated method stub

	}

}
