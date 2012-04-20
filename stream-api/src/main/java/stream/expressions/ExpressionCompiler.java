/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExpressionCompiler {

	static Logger log = LoggerFactory.getLogger(ExpressionCompiler.class);

	public final static Expression parse(String str) throws ExpressionException {
		log.debug("Parsing expression: '{}'", str);
		if (str == null || str.trim().isEmpty())
			return null;

		ExpressionReader r = new ExpressionReader(str);
		return r.readFilterExpression();
	}

	public final static Expression parse(String str,
			Collection<String> variables) throws ExpressionException {
		log.debug("Parsing expression: '{}'", str);
		ExpressionReader r = new ExpressionReader(str, variables);
		return r.readFilterExpression();
	}

	public static List<Expression> expand(ExpressionList list) {
		List<Expression> exp = new LinkedList<Expression>();

		for (Expression e : list.getElements()) {
			if (e instanceof ExpressionList)
				exp.addAll(expand((ExpressionList) e));
			else
				exp.add(e);
		}

		return exp;
	}
}
