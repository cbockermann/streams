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
package stream.node.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This filter breaks up the filter chain of the standard web-application by
 * removing the struts2-action processing from request which provide plain
 * servlet-implementations.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class DisableStruts2Filter implements Filter {
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger(DisableStruts2Filter.class);

	/* A list of prefix-patterns of URI paths for which struts is to be disabled */
	final static List<String> patterns = new ArrayList<String>();

	public DisableStruts2Filter() {
		patterns.add("/api/");
		patterns.add("/doc/");
		patterns.add( "/stream.node.shell.WebShell/" );
	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		if (arg0 instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) arg0;
			String requestURI = request.getRequestURI();
			String ctx = request.getContextPath();

			for (String prefix : patterns) {

				if (requestURI.startsWith(prefix)) {
					log.debug("aborting filter-chain for uri {}", requestURI);
					request.getRequestDispatcher(requestURI)
							.forward(arg0, arg1);
					return;
				}

				if (requestURI.startsWith(ctx + prefix)) {
					log.debug(
							"aborting filter-chain for uri {}   (ctx + prefix)",
							requestURI);
					request.getRequestDispatcher(
							requestURI.substring(ctx.length())).forward(arg0,
							arg1);
					return;
				}
			}
		}

		arg2.doFilter(arg0, arg1);
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
}