package stream.node.web;

import org.apache.struts2.convention.annotation.Result;

import com.opensymphony.xwork2.ActionSupport;


@Result(name = "success", location = "/WEB-INF/content/shell.jsp")
public class ShellAction extends ActionSupport {

	/** The unique class ID */
	private static final long serialVersionUID = 8456784414142710922L;

	public String execute() throws Exception {
		return "success";
	}
}
