/**
 * 
 */
package stream.node.web.container;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author chris
 * 
 */
@Action("/container/{container}")
public class IndexAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3358590474620956368L;
	static Logger log = LoggerFactory.getLogger(IndexAction.class);

	/**
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		log.info("Execution IndexAction for request to {}",
				ServletActionContext.getRequest().getRequestURI());
		return super.execute();
	}
}
