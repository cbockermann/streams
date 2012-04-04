package stream.node.web;

import org.apache.struts2.convention.annotation.Result;

import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>
 * Basically, this action simply is a proxy for the alerts/ Index action...
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Result(name = "success", location = "/WEB-INF/content/help.jsp")
public class HelpAction extends ActionSupport {
	/** The unique class ID */
	private static final long serialVersionUID = -5120299089079573448L;

	public String execute() throws Exception {
		return "success";
	}
}