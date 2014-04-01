package stream.expressions.version2;

import stream.Context;
import stream.Data;

public class FalseCondition extends Condition{

	public FalseCondition(String e) {
		super(e);
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		return false;
	}

}