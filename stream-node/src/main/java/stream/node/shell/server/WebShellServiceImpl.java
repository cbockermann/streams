package stream.node.shell.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Shell;
import stream.node.shell.client.WebShellService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WebShellServiceImpl extends RemoteServiceServlet implements WebShellService {

	/** The unique class ID */
	private static final long serialVersionUID = 6025330703026655607L;
	
	static Logger log = LoggerFactory.getLogger( WebShellServiceImpl.class );
	
	
	Shell shell;
	
	public WebShellServiceImpl(){
		shell = new Shell();
	}
	

	@Override
	public String execute(String command) throws Exception {
		log.info( "Executing command: '{}'", command );
		try {
			return shell.eval( command );
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
