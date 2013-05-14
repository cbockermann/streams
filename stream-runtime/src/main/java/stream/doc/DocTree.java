/**
 * 
 */
package stream.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Internal;
import stream.io.Stream;
import stream.util.WildcardPattern;

/**
 * @author chris
 * 
 */
public class DocTree implements Comparable<DocTree> {

	static Logger log = LoggerFactory.getLogger(DocTree.class);
	DocTree parent = null;
	String prefix = "API_";
	final String name;
	final SortedSet<DocTree> children = new TreeSet<DocTree>();

	public DocTree(String pkg) {
		name = pkg;
	}

	public void add(DocTree child) {
		children.add(child);
		child.parent = this;
	}

	public Set<DocTree> getChildren() {
		return children;
	}

	public void add(String[] path, DocTree child) {
		if (path.length > 1) {
			String[] remain = new String[path.length - 1];
			for (int i = 1; i < path.length; i++) {
				remain[i - 1] = path[i];
			}
			getChild(path[0]).add(remain, child);
		} else {
			getChild(path[0]).add(child);
		}
	}

	public DocTree getChild(String n) {
		for (DocTree ch : children)
			if (ch.name.equals(n)) {
				return ch;
			}

		DocTree nch = new DocTree(n);
		nch.parent = this;
		children.add(nch);
		return nch;
	}

	public boolean hasChild(String name) {
		for (DocTree ch : children) {
			if (name.equals(ch.name))
				return true;
		}
		return false;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DocTree arg0) {
		if (arg0 == null)
			return 1;
		if (this == arg0)
			return 0;
		return name.compareTo(arg0.name);
	}

	public void print(String prefix) {
		System.out.print(prefix + " | " + name + "\n");
		for (DocTree ch : children) {
			ch.print(prefix + "  ");
		}
	}

	public String getPath() {

		if (parent == null)
			return "";
		else {
			if (isLeaf())
				return parent.getPath();
			else
				return parent.getPath() + "/" + name;
		}
	}

	public void generateDocs(File base) throws IOException {
		// System.out.println("Generating docs in " +
		// base.getAbsolutePath());

		if (!isLeaf()) {
			// System.out.println("Checking for index.md file in package "
			// + name + ", package path is " + getPath());

			PrintStream out;

			String p = getPath();
			if (!p.isEmpty())
				p = p.substring(1);

			p = p.replace('/', '_') + "_index.tex";
			while (p.startsWith("_"))
				p = p.substring(1);

			File indexTex = new File(base.getAbsolutePath() + "/" + prefix + p);
			indexTex.getParentFile().mkdirs();
			out = new PrintStream(new FileOutputStream(indexTex));

			URL texUrl;
			URL url = DocTree.class.getResource(getPath() + "/index.md");

			try {
				// Class<?> clazz = Class.forName(getPath().replace('/', '.'));

				texUrl = DocTree.class.getResource(getPath() + "/index.tex");
				log.info("index.tex for path {} is: {}", getPath(), texUrl);
				if (texUrl != null) {
					DocGenerator.copy(texUrl.openStream(), out);
					out.println();
					out.println();
					out.println();
				} else {
					if (url != null) {
						DocGenerator.converter.convert(url.openStream(), out);
						out.println();
						out.println();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			indexTex.getParentFile().mkdirs();

			List<DocTree> list = new ArrayList<DocTree>(children);
			Iterator<DocTree> it = list.iterator();
			while (it.hasNext()) {
				DocTree ch = it.next();
				URL docUrl = DocTree.class.getResource(ch.getPath() + "/"
						+ ch.name);

				texUrl = DocTree.class.getResource(ch.getPath() + "/"
						+ ch.name.replace(".md", ".tex"));
				if (docUrl == null && texUrl == null) {
					log.debug("Not linking non-existing document url for {}",
							ch.getPath() + "/" + ch.name);
					it.remove();
				}
			}

			DocGenerator.converter.createTableOfContents(list, out);
			/*
			 * for (DocTree ch : children) { if (ch.isLeaf()) {
			 * out.println("\\input{" + ch.getPath().substring(1) + "/" +
			 * ch.name.replace(".md", ".tex") + "}"); } }
			 */
			out.close();
			DocGenerator.converter.sectionDown();
			for (DocTree ch : children) {
				ch.generateDocs(base);
			}
			DocGenerator.converter.sectionUp();
		} else {
			// System.out.println("Converting doc-tree leaf " + getPath()
			// + "/" + name);

			String path = getPath();
			if (path.trim().isEmpty())
				return;

			log.debug("Converting doc-tree leaf '{}'", path);
			File md = new File(base.getAbsolutePath() + File.separator + prefix
					+ getPath().substring(1).replace('/', '_') + "_" + name);
			generateTex(md);
		}
	}

	public String getResourceName(String ext) {
		return "/" + getPath() + "/" + name.replace(".md", ext);
	}

	private File generateTex(File md) {

		File tex = new File(md.getAbsolutePath().replace(".md", ".tex"));
		String className = getPath().substring(1).replace('/', '.') + "."
				+ name.replace(".md", "");

		String texResource = getPath() + "/" + name.replace(".md", ".tex");
		URL texUrl = DocFinder.class.getResource(texResource);
		log.debug("checking for .tex file at {} => {}", texResource, texUrl);
		if (texUrl != null) {
			log.debug("Found existing .tex documentation!");
			try {
				FileOutputStream fos = new FileOutputStream(tex);

				PrintStream out = new PrintStream(fos);

				Class<?> clazz = Class.forName(className);

				if (DocFinder.implementsInterface(clazz, Stream.class)) {
					out.println("\\Stream{" + clazz.getSimpleName() + "}");
					out.println("\\label{sec:" + clazz.getCanonicalName()
							+ "}\n");
				}

				if (DocFinder.implementsInterface(clazz, Processor.class)) {
					out.println("\\Processor{" + clazz.getSimpleName() + "}");
					out.println("\\label{sec:" + clazz.getCanonicalName()
							+ "}\n");
				}

				DocGenerator.copy(texUrl.openStream(), fos);

				out.println();
				DocGenerator.converter.writeParameterTable(clazz, out);
				out.flush();

				fos.close();
				return tex;
			} catch (Exception e) {
				log.error("Error: {}", e.getMessage());
			}
		} else {
			// log.warn("No .tex resource for {}", className);
		}
		URL url = DocFinder.class.getResource(getPath() + "/" + name);
		if (url == null) {
			// System.err
			// .println("No documentation found for '" + className + "'");
			return null;
		}

		try {
			log.debug("Converting {} to {}", url, tex);
			FileOutputStream fos = new FileOutputStream(tex);
			PrintStream pos = new PrintStream(fos);
			DocGenerator.converter.convert(url.openStream(), pos);
			DocGenerator.converter.writeParameterTable(
					Class.forName(className), pos);
			pos.flush();
			pos.close();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
		}
		return tex;
	}

	public static boolean matches(String[] patterns, Class<?> clazz) {
		if (patterns == null || patterns.length == 0)
			return true;
		String className = clazz.getCanonicalName();
		if (className == null)
			return false;

		for (String pattern : patterns) {

			if (className.startsWith(pattern)) {
				log.debug("Class '{}' starts with pattern {}", className,
						pattern);
				return true;
			}

			if (WildcardPattern.matches(pattern, className)) {
				return true;
			}
		}

		log.debug("No match for class '{}' and patterns {}", className,
				patterns);
		return false;
	}

	public static DocTree findDocs(Class<?>[] CLASSES, String[] patterns) {
		DocTree tree = new DocTree("");

		try {
			List<Class<?>> classes = new ArrayList<Class<?>>();
			for (String pattern : patterns) {
				classes.addAll(ClassFinder.getClasses(pattern));
			}

			SortedSet<String> docs = new TreeSet<String>();
			SortedSet<String> missing = new TreeSet<String>();

			for (Class<?> clazz : classes) {

				if (Modifier.isAbstract(clazz.getModifiers())
						|| Modifier.isInterface(clazz.getModifiers()))
					continue;

				if (clazz.isAnnotationPresent(Internal.class)) {
					log.debug("Skipping internal class {}", clazz);
					continue;
				}

				for (Class<?> apiClass : CLASSES) {

					if (apiClass.isAssignableFrom(clazz)) {

						if (clazz
								.isAnnotationPresent(java.lang.Deprecated.class)) {
							log.debug("Skipping deprecated class {}", clazz);
							break;
						}

						if (matches(patterns, clazz)) {
							log.debug("Found processor-class {}", clazz);
							log.debug("    clazz.getName() = {}",
									clazz.getName());

							String doc = "/"
									+ clazz.getName().replace('.', '/') + ".md";
							log.debug("    docs are at {}", doc);

							int idx = doc.substring(1).lastIndexOf("/");
							String[] path = doc.substring(1, idx + 1)
									.split("/");
							DocTree elem = new DocTree(doc.substring(idx + 2));
							tree.add(path, elem);

							URL url = getMarkDownFile(clazz);
							URL texUrl = getTexFile(clazz);

							if (texUrl != null) {
								url = texUrl;
								doc = doc.replace(".md", ".tex");
							}

							// URL url = DocFinder.class.getResource(doc);
							if (url != null)
								docs.add(doc);
							else {
								missing.add(doc);
								log.error(
										"No documentation provided for class '{}'",
										clazz.getCanonicalName());
							}
						} else {
							log.debug("Skipping class '{}' due to patterns {}",
									clazz, patterns);

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return tree;
	}

	public static URL getMarkDownFile(Class<?> clazz) {
		String doc = "/" + clazz.getName().replace('.', '/') + ".md";
		log.debug("Checking for markdown file at '{}'", doc);
		return DocTree.class.getResource(doc);
	}

	public static URL getTexFile(Class<?> clazz) {
		String doc = "/" + clazz.getName().replace('.', '/') + ".tex";
		log.debug("Checking for TeX file at '{}'", doc);
		return DocTree.class.getResource(doc);
	}
}