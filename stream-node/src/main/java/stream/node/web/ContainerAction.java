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
package stream.node.web;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.StreamNodeContext;
import stream.node.servlets.ContainerServlet;
import stream.runtime.Context;
import stream.runtime.ProcessContainer;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author chris
 * 
 */
@Results({ @Result(name = "notfound", type = "httpheader", params = { "status",
		"404", "errorMessage", "Not Found" }) })
public class ContainerAction extends ActionSupport implements ServletRequestAware {

	/** The unique class ID */
	private static final long serialVersionUID = -4595878396489464885L;
	static Logger log = LoggerFactory.getLogger(ContainerAction.class);
	HttpServletRequest req;

	String container;
	String processor;

	/**
	 * @see org.apache.struts2.interceptor.ServletRequestAware#setServletRequest(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		req = arg0;
	}

	/**
	 * @return the container
	 */
	public String getContainer() {
		return container;
	}

	/**
	 * @param container
	 *            the container to set
	 */
	public void setContainer(String container) {
		this.container = container;
	}

	/**
	 * @return the processor
	 */
	public String getProcessor() {
		return processor;
	}

	/**
	 * @param processor
	 *            the processor to set
	 */
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	/**
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {

		log.info("container = {}", container);
		log.info("processor = {}", processor);

		try {
			log.debug("Processing request for {}", req.getRequestURI());
			String name = req.getRequestURI().substring(
					req.getServletPath().length());

			while (name.startsWith("/"))
				name = name.substring(1);

			log.debug("name is: '{}'", name);
			String containerName = name;

			if (name.indexOf("/") > 0) {
				log.debug("Reference is a processor-request, need to lookup processor-object...");

				int idx = name.indexOf("/");
				containerName = name.substring(0, idx);
				log.info("Container-name is '{}'", containerName);

				String id = name.substring(idx + 1);
				log.debug("reference to look for is: {}", id);

				ProcessContainer container = StreamNodeContext.runtimeManager
						.getContainer(containerName);

				log.debug("Container for name '{}' is: {}", containerName,
						container);

				try {
					Context ctx = container.getContext();
					log.debug("Using contxt {} for lookup", ctx);
					Object object = ctx.lookup(id);
					if (object == null) {
						return "notfound";
					}
				} catch (Exception e) {
					log.error("Error: {}", e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				}

			} else {

				log.debug("Request for container configuration...");
				File file = new File(StreamNodeContext.getDeployDirectory()
						.getAbsolutePath() + File.separator + name + ".xml");
				if (file.exists()) {
					log.debug("Found container file {}", file);

					URL xslt = ContainerServlet.class
							.getResource("/container.xsl");
					log.debug("Using template {}", xslt);

					try {
						TransformerFactory tf = TransformerFactory
								.newInstance();
						Transformer trans = tf.newTransformer(new StreamSource(
								xslt.openStream()));
						StringWriter xml = new StringWriter();
						trans.transform(new StreamSource(new FileReader(file)),
								new StreamResult(xml));

						log.debug("Sending transformed HTML configuration...");
						// resp.getWriter().println(xml.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					log.error("No container found for that name!");
					return "notfound";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}
}
