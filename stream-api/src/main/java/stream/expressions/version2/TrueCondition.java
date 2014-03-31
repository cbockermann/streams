package stream.expressions.version2;

import stream.Context;
import stream.Data;

public class TrueCondition extends Condition{

	public TrueCondition(String e) {
		super(e);
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		return true;
	}

}