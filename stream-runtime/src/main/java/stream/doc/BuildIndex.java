/**
 * 
 */
package stream.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import stream.Command;
import stream.doc.helper.DocIndex;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class BuildIndex implements Command {

	/**
	 * @see stream.Command#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {

		File userIndex = new File(System.getProperty("user.home")
				+ File.separator + ".streams.doc");
		DocIndex index = new DocIndex();
		try {
			Map<Class<?>, URL> help = DocFinder.findDocumentations(null);

			int added = 0;
			for (Class<?> clazz : help.keySet()) {
				URL url = help.get(clazz);
				String text = URLUtilities.readContentOrEmpty(url);
				index.add(text, url, clazz);
				added++;
			}

			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(userIndex));
			oos.writeObject(index);
			oos.close();
			System.out.println("Updated doc index, " + added + " files added.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		BuildIndex reindex = new BuildIndex();
		reindex.execute(new ArrayList<String>());
	}
}
