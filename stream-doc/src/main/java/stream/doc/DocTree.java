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

import stream.annotations.Internal;
import stream.util.ClassFinder;
import stream.util.DocFinder;

/**
 * @author chris
 * 
 */
public class DocTree implements Comparable<DocTree> {

	static Logger log = LoggerFactory.getLogger(DocTree.class);
	DocTree parent = null;
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

			File indexTex = new File(base.getAbsolutePath() + "/"
					+ p.replace('/', '_') + "_index.tex");
			indexTex.getParentFile().mkdirs();
			out = new PrintStream(new FileOutputStream(indexTex));

			URL url = DocTree.class.getResource(getPath() + "/index.md");
			if (url != null) {
				DocGenerator.converter.convert(url.openStream(), out);
				out.println();
				out.println();
			}
			indexTex.getParentFile().mkdirs();

			List<DocTree> list = new ArrayList<DocTree>(children);
			Iterator<DocTree> it = list.iterator();
			while (it.hasNext()) {
				DocTree ch = it.next();
				URL docUrl = DocTree.class.getResource(ch.getPath() + "/"
						+ ch.name);

				URL texUrl = DocTree.class.getResource(ch.getPath() + "/"
						+ ch.name.replace(".md", ".tex"));
				if (docUrl == null && texUrl == null) {
					log.info("Not linking non-existing document url for {}",
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
			File md = new File(base.getAbsolutePath() + File.separator
					+ getPath().substring(1).replace('/', '_') + "_" + name);
			generateTex(md);
		}
	}

	public String getResourceName(String ext) {

	}

	private File generateTex(File md) {

		File tex = new File(md.getAbsolutePath().replace(".md", ".tex"));
		String className = getPath().substring(1).replace('/', '.') + "."
				+ name.replace(".md", "");

		String texResource = getPath() + "/" + name.replace(".md", ".tex");
		URL texUrl = DocFinder.class.getResource(texResource);
		log.info("checking for .tex file at {} => {}", texResource, texUrl);
		if (texUrl != null) {
			log.info("Found existing .tex documentation!");
			try {
				FileOutputStream fos = new FileOutputStream(tex);
				DocGenerator.copy(texUrl.openStream(), fos);
				fos.close();
				return tex;
			} catch (Exception e) {
				log.error("Error: {}", e.getMessage());
			}
		} else {
			log.info("No .tex resource for {}", className);
		}
		URL url = DocFinder.class.getResource(getPath() + "/" + name);
		if (url == null) {
			System.err
					.println("No documentation found for '" + className + "'");
			return null;
		}

		try {
			System.out.println("Converting " + url + " to "
					+ tex.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(tex);
			PrintStream pos = new PrintStream(fos);
			DocGenerator.converter.convert(url.openStream(), pos);
			DocGenerator.converter.writeParameterTable(
					Class.forName(className), pos);
			pos.flush();
			pos.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return tex;
	}

	public static DocTree findDocs(Class<?>[] CLASSES) {
		DocTree tree = new DocTree("");

		try {
			Class<?>[] classes = ClassFinder.getClasses("");

			SortedSet<String> docs = new TreeSet<String>();
			SortedSet<String> missing = new TreeSet<String>();

			for (Class<?> clazz : classes) {

				if (Modifier.isAbstract(clazz.getModifiers())
						|| Modifier.isInterface(clazz.getModifiers()))
					continue;

				if (clazz.isAnnotationPresent(Internal.class)) {
					System.out.println("Skipping internal class " + clazz);
					continue;
				}

				for (Class<?> apiClass : CLASSES) {

					if (apiClass.isAssignableFrom(clazz)) {

						if (clazz
								.isAnnotationPresent(java.lang.Deprecated.class)) {
							System.out.println("Skipping deprecated class "
									+ clazz);
							break;
						}

						log.debug("Found processor-class {}", clazz);
						log.debug("    clazz.getName() = {}", clazz.getName());
						String doc = "/" + clazz.getName().replace('.', '/')
								+ ".md";
						log.debug("    docs are at {}", doc);

						int idx = doc.substring(1).lastIndexOf("/");
						String[] path = doc.substring(1, idx + 1).split("/");
						DocTree elem = new DocTree(doc.substring(idx + 2));
						tree.add(path, elem);

						URL url = DocFinder.class.getResource(doc);
						if (url != null)
							docs.add(doc);
						else {
							missing.add(doc);
							log.error("No documentation provided for class {}",
									clazz);
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
}