/**
 * 
 */
package stream.runtime.expressions;

import java.util.Map;

import stream.runtime.Context;

/**
 * @author chris
 * 
 */
public class ContextAwareMacroExpander extends MacroExpander {

	Context ctx;

	public ContextAwareMacroExpander(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * @see stream.runtime.expressions.MacroExpander#get(java.lang.String, java.util.Map)
	 */
	@Override
	public String get(String variable, Map<String, ?> evt) {

		Object object = ctx.resolve(variable);
		if (object != null)
			return object.toString();

		return super.get(variable, evt);
	}
}
