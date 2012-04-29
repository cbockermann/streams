/**
 * 
 */
package stream.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chris
 * 
 */
public class FileUtils {

	public static List<File> findAllFiles(File dir) {
		return listFiles(dir, true);
	}

	public static List<File> listFiles(File directory, boolean recursive) {
		List<File> result = new ArrayList<File>();
		if (directory.isFile()) {
			result.add(directory);
			return result;
		}

		File[] files = directory.listFiles();
		if (files == null)
			return result;

		for (File f : files) {

			if (f.isDirectory() && recursive) {
				result.addAll(listFiles(f, recursive));
				continue;
			}
			if (f.isFile() && !f.getName().startsWith(".")) {
				result.add(f);
			} else {
			}
		}
		return result;
	}
}
