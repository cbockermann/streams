package stream.expressions.version2;

import java.io.Serializable;

import stream.Context;
import stream.Data;

public class BooleanExpression extends AbstractExpression<Boolean> {

	public BooleanExpression(String e) {
		super(e);
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		Serializable s = r.get(ctx, item);
		if (s == null)
			return null;
		if (s instanceof Boolean)
			return ((Boolean) s);
		if (s instanceof Double) {
			Double d = (Double) s;
			if (d == 1)
				return true;
			if (d == 0)
				return false;
			return null;
		}
		if (s instanceof Integer) {
			Integer d = (Integer) s;
			if (d == 1)
				return true;
			if (d == 0)
				return false;
			return null;
		}

		if (s instanceof String) {
			String d = (String) s;
			if (d.equals("1") || d.equals("1.0"))
				return true;
			if (d.equals("0") || d.equals("0.0"))
				return false;
			return null;
		}
		return null;
	}

	@Override
	public Class<Boolean> type() {
		return Boolean.class;
	}

}
