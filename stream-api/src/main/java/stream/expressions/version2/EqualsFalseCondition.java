package stream.expressions.version2;

import stream.Context;
import stream.Data;

public class EqualsFalseCondition extends Condition {

	protected Expression<Boolean> exp;

	public EqualsFalseCondition(Expression<Boolean> exp) {
		super(null);
		this.exp = exp;
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		Boolean b = exp.get(ctx, item);
		if (b == null)
			return null;
		return b == false;
	}
}
