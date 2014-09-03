/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.script;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.StatefulProcessor;
import stream.annotations.Description;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Script")
public class JRuby extends Script {
	static Logger log = LoggerFactory.getLogger(JRuby.class);

	final static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	final static String preamble = "";

	transient String theScript = null;
	String script = null;

	Invocable impl;

	/**
	 */
	public JRuby() {
		super(scriptEngineManager.getEngineByName("jruby"));
	}

	/**
	 * @see stream.data.AbstractProcessor#init(stream.Context)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		this.script = loadScript();

		try {
			this.initScript();

			if (impl != null && (impl instanceof StatefulProcessor)) {
				try {
					impl.invokeFunction("init", ctx);
					// ((StatefulProcessor) impl).init(ctx);
				} catch (NoSuchMethodException nsm) {
					log.warn("No init() function defined in JRuby.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			log.error("Error while initializing script: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	/**
	 * @see stream.data.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {

		try {
			if (script == null) {
				log.debug("No script loaded, skipping script execution...");
				return data;
			}

			if (impl != null) {
				try {
					log.debug("Calling JavaScript implementation of processor interface...");
					data = (Data) impl.invokeFunction("process", data);
					return data;
					// return ((Processor) impl).process(data);
				} catch (NoSuchMethodException nsme) {
					log.warn("No function 'process(data)' defined, evaluating running script code!");
				}
			}

			log.debug("Script loaded is:\n{}", script);

			ScriptContext ctx = scriptEngine.getContext();
			scriptEngine.put("data", data);
			scriptEngine.put("process", this.context);

			log.debug("Evaluating script...");
			scriptEngine.eval(script, ctx);

		} catch (Exception e) {
			log.error("Failed to execute script: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();

			throw new RuntimeException("Script execution error: "
					+ e.getMessage());
		}

		log.debug("Returning data: {}", data);
		return data;
	}

	protected String loadScript() throws Exception {

		if (embedded != null) {
			log.info("Using embedded content...");
			theScript = preamble + "\n" + embedded.getContent();
			return theScript;
		}

		if (file != null) {
			log.debug("Reading script from file {}", file);
			theScript = loadScript(new FileInputStream(file));
			return theScript;
		}

		throw new Exception("Neither embedded script not script file provided!");
	}

	protected String loadScript(InputStream in) throws Exception {
		log.debug("Loading script from input-stream {}", in);
		StringBuffer s = new StringBuffer();
		s.append(preamble);
		s.append("\n");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (line != null) {
			s.append(line + "\n");
			log.debug("Appending line: {}", line);
			line = reader.readLine();
		}
		reader.close();
		return s.toString();
	}

	private void initScript() throws Exception {

		log.debug("Script loaded is:\n{}", script);

		ScriptContext ctx = scriptEngine.getContext();
		scriptEngine.put("process", this.context);

		log.debug("Evaluating script...");
		scriptEngine.eval(script, ctx);

		if (scriptEngine instanceof Invocable) {
			Invocable invocable = (Invocable) scriptEngine;
			impl = invocable; // invocable.getInterface(StatefulProcessor.class);
			if (impl != null) {
				log.debug("JRuby script implements StatefulProcessor interface!!");

				try {
					impl.invokeFunction("init", this.context);
				} catch (Exception e) {
					log.error("Initialization of JRuby script failed: {}",
							e.getMessage());
				}

				return;
			}

			// impl = invocable.getInterface(Processor.class);
			log.debug("Found JavaScript implementation of processor interface...");
		}
	}
}