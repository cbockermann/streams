/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.expressions;

import stream.Context;
import stream.data.Data;

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

			return ctx.resolve(var);
		}

		return null;
	}

	public static boolean isMacroObject(String variable) {
		if (variable != null) {
			String var = variable.trim();
			if (var.startsWith(MACRO_START) && var.endsWith(MACRO_END))
				return true;
		}
		return false;
	}
	
	public static String expand( String str, Context ctx, Data item ){
		return str;
	}
}
