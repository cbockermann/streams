package stream.expressions.version2;

import java.io.Serializable;

public abstract class OperatorCondition<I extends Serializable> extends
		Condition implements Operator<I, Boolean> {

	protected final Expression<I> left;
	protected final Expression<I> right;
	protected final String op;

	public OperatorCondition(Expression<I> left, Expression<I> right,
			String operation) {
		// TODO
		super(left.toString() + " " + operation + " " + right.toString());
		this.left = left;
		this.right = right;
		this.op = operation;
	}

}
