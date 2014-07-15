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
package stream.doc.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.doc.DocFinder;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class DocIndex implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 6874000860509328529L;

	static Logger log = LoggerFactory.getLogger(DocIndex.class);

	Map<Data, URL> docs = new HashMap<Data, URL>();

	public static DocIndex getInstance() {

		File userIndex = new File(System.getProperty("user.home")
				+ File.separator + ".streams.doc");
		if (userIndex.canRead()) {

			try {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(userIndex));
				DocIndex idx = (DocIndex) ois.readObject();
				ois.close();
				return idx;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		DocIndex index = new DocIndex();
		try {
			Map<Class<?>, URL> help = DocFinder.findDocumentations(null);

			for (Class<?> clazz : help.keySet()) {
				URL url = help.get(clazz);
				String text = URLUtilities.readContentOrEmpty(url);
				log.debug(
						"Adding text:\n-----------------\n{}\n--------------------\n",
						text);
				index.add(text, url, clazz);
			}

			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(userIndex));
			oos.writeObject(index);
			oos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}

	public DocIndex() {
	}

	private Data createWordVector(String text) {
		Data item = DataFactory.create();

		String[] words = text.split("\\W+");
		for (String w : words) {
			String word = w.trim();
			if (word.isEmpty())
				continue;

			Double count = (Double) item.get(word);
			if (count == null)
				count = 0.0;

			count += 1.0;
			item.put(word, count);
		}

		return item;
	}

	public void add(String text, URL location, Class<?> clazz) {
		log.debug("Adding document {}", location);
		Data wv = createWordVector(text.toLowerCase());
		if (clazz != null)
			wv.put("@class", clazz.getCanonicalName());

		log.debug(
				"Adding text:\n-----------------\n{}\n--------------------\n",
				text);

		docs.put(wv, location);
	}

	public List<Result> search(String query) {
		List<Result> results = new ArrayList<Result>();

		Data qv = createWordVector(query.toLowerCase());

		for (Data wv : docs.keySet()) {
			double score = dist(qv, wv);
			if (score > 0)
				results.add(new Result(docs.get(wv), score, wv.get("@class")
						+ ""));
		}

		Collections.sort(results);
		return results;
	}

	public Double dist(Data w, Data v) {

		Set<String> keys = new HashSet<String>();
		keys.addAll(w.keySet());
		keys.addAll(v.keySet());

		double val = 0.0;

		for (String k : keys) {

			if (k.startsWith(Data.ANNOTATION_PREFIX))
				continue;

			Double w1 = (Double) w.get(k);
			if (w1 == null)
				continue;

			Double v1 = (Double) v.get(k);
			if (v1 == null)
				continue;

			val += (w1 * v1);
		}

		return val;
	}

	public class Result implements Comparable<Result>, Serializable {
		/** The unique class ID */
		private static final long serialVersionUID = -1224420400062085703L;
		public final URL url;
		public final Double score;
		public final String className;

		public Result(URL url, Double score, String className) {
			this.url = url;
			this.score = score;
			this.className = className;
		}

		public String getClassName() {
			return className;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Result arg0) {

			int ret = score.compareTo(arg0.score);
			if (ret == 0) {
				return url.toString().compareTo(arg0.toString());
			}

			return -ret;
		}
	}
}
