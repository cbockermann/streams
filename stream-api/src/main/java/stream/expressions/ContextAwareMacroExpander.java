/**
 * 
 */
package stream.expressions;

import java.util.Map;

import stream.Context;

/**
 * @author chris
 * 
 */
public class ContextAwareMacroExpander extends MacroExpander {

	Context ctx;

	public ContextAwareMacroExpander(Context ctx) {
		super();
		this.ctx = ctx;
	}

	public ContextAwareMacroExpander(Context ctx, Map<String, ?> vars) {
		super(vars);
		this.ctx = ctx;
	}

	/**
	 * @see stream.expressions.MacroExpander#get(java.lang.String,
	 *      java.util.Map)
	 */
	@Override
	public String get(String variable, Map<String, ?> evt) {

		Object object = ctx.resolve(variable);
		if (object != null)
			return object.toString();

		return super.get(variable, evt);
	}
}
