/**
 * 
 */
package stream.node.shell.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.runtime.RuntimeManager;
import stream.node.shell.client.ContainerEditorService;
import stream.node.shell.client.ElementDescription;
import stream.runtime.setup.DefaultParameterFinder;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ParameterFinder;
import stream.util.ClassFinder;
import stream.util.MD5;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author chris
 * 
 */
public class ContainerEditorServiceImpl extends RemoteServiceServlet implements
		ContainerEditorService {

	/** The unique class ID */
	private static final long serialVersionUID = 8181337710855358493L;

	static Logger log = LoggerFactory
			.getLogger(ContainerEditorServiceImpl.class);
	ObjectFactory objectFactory = ObjectFactory.newInstance();
	ParameterFinder parameterFinder = new DefaultParameterFinder();

	RuntimeManager runtimeManager = RuntimeManager.getInstance();
	String path = "/tmp";

	/**
	 * @see stream.node.shell.client.ContainerEditorService#getAvailableElements()
	 */
	@Override
	public Map<String, ElementDescription> getAvailableElements() {
		Map<String, ElementDescription> index = new LinkedHashMap<String, ElementDescription>();

		try {
			Class<?>[] classes = ClassFinder.getClasses("stream");

			for (Class<?> clazz : classes) {

				if (Processor.class.isAssignableFrom(clazz)) {
					ElementDescription desc = new ElementDescription();
					desc.setClassName(clazz.getCanonicalName());
					desc.setType("Processor");
					Map<String, Class<?>> types = parameterFinder
							.findParameters(clazz);
					desc.setParameters(types);
					log.info("Adding {} = {}", desc.getClassName(), desc);
					index.put(desc.getClassName(), desc);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return index;
	}

	public List<String> listFiles() {

		List<String> files = new ArrayList<String>();

		File dir = new File(this.path);
		if (!dir.isDirectory())
			dir.mkdirs();

		for (File file : dir.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".xml"))
				files.add(file.getName());
		}

		return files;
	}

	/**
	 * @see stream.node.shell.client.ContainerEditorService#readFile(java.lang.String)
	 */
	@Override
	public String readFile(String file) throws Exception {

		StringBuffer s = new StringBuffer();
		File f = new File(path + File.separator + file);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = reader.readLine();
		while (line != null) {
			s.append(line + "\n");
			line = reader.readLine();
		}

		reader.close();
		return s.toString();
	}

	/**
	 * @see stream.node.shell.client.ContainerEditorService#writeFile(java.lang.String
	 *      , java.lang.String)
	 */
	@Override
	public boolean writeFile(String name, String xml) throws Exception {
		try {
			File f = new File(path + File.separator + name);
			PrintWriter writer = new PrintWriter(new FileWriter(f));
			writer.print(xml);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @see stream.node.shell.client.ContainerEditorService#start(java.lang.String)
	 */
	@Override
	public String start(String file) throws Exception {

		File f = new File(path + File.separator + file);

		/*
		 * URL url = f.toURI().toURL(); ProcessContainer container = new
		 * ProcessContainer(url); ProcessContainerThread thread = new
		 * ProcessContainerThread(f, container); thread.start();
		 */

		if (runtimeManager.isDeployed(f)) {
			throw new Exception("File " + f.getAbsolutePath()
					+ " is already being executed!");
		} else {
			runtimeManager.deploy(f);
			return MD5.md5(f.getAbsolutePath());
		}
	}

	/**
	 * @see stream.node.shell.client.ContainerEditorService#stop(java.lang.String)
	 */
	@Override
	public String stop(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}