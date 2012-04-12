/**
 * 
 */
package stream.runtime.expressions;

import stream.data.Data;
import stream.runtime.Context;

/**
 * <p>
 * This class
 * </p>
 * 
 * @author chris
 * 
 */
public class ExpressionResolver {

	public final static String MACRO_START = "%{";
	public final static String MACRO_END = "}";

	public static String[] extractName(String variable) {

		if (variable != null) {
			String var = variable.trim();
			if (var.startsWith(MACRO_START) && var.endsWith(MACRO_END)) {
				var = var.substring(MACRO_START.length(), var.length() - 1);
				if (var.indexOf(".") >= 0)
					return var.split("\\.", 2);
				else {
					return new String[] { "", var };
				}
			}
		}

		return new String[] { "", variable };
	}

	public static Object resolve(String variable, Context ctx, Data item) {

		if (variable == null)
			return null;

		String var = variable.trim();
		if (var.startsWith(MACRO_START) && var.endsWith(MACRO_END)) {
			var = var.substring(MACRO_START.length(), var.length() - 1);
			if (var.startsWith("data.")) {
				return item.get(var.substring(5));
			}

			return ctx.resolve(variable);
		}

		return item.get(variable);
	}
}