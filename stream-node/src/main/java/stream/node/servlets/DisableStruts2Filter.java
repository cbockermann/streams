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