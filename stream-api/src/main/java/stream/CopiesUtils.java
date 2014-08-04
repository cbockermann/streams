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
package stream;

import java.util.ArrayList;
import java.util.List;

import stream.util.Variables;

/**
 * @author Hendrik Blom
 * 
 */
public class CopiesUtils {

	public static Copy[] parse(String copiesString) {
		return parse(copiesString, false);
	}

	public static Copy[] parse(String copiesString, boolean ignoreNumbers) {
		int start = copiesString.indexOf("[");
		List<Copy> subCopyList = new ArrayList<Copy>();

		if (start < 0) {
			String[] copies = parseCopiesElemement(copiesString, ignoreNumbers);

			for (String c : copies) {
				Copy cr = new Copy();
				cr.setId(c);
				String[] subids = new String[1];
				subids[0] = c;
				cr.setSubids(subids);
				subCopyList.add(cr);
			}
			return subCopyList.toArray(new Copy[subCopyList.size()]);
		}
		List<Copy> copyList = new ArrayList<Copy>();
		Copy cr = new Copy();
		cr.setId(copiesString);

		copyList.add(cr);

		boolean run = true;
		while (run) {
			subCopyList = new ArrayList<Copy>();

			for (Copy copy : copyList) {
				String copyId = copy.getId();
				start = copyId.indexOf("[");
				int stop = copyId.indexOf("]");
				if (start >= 0 && stop >= 0) {

					String sub = copyId.substring(start + 1, stop);
					String[] copies = parseCopiesElemement(sub, false);
					if (copies == null)
						return null;
					String pre = copyId.substring(0, start);
					String post = copyId.substring(stop + 1, copyId.length());

					for (String c : copies) {
						cr = new Copy();
						cr.setId((pre + c + post));
						String[] subids = copy.getSubids();
						if (subids.length == 0) {
							subids = new String[1];
							subids[0] = c;
							cr.setSubids(subids);

						} else {
							String[] tmpIds = new String[subids.length + 1];
							for (int i = 0; i < subids.length; i++) {
								tmpIds[i] = subids[i];
							}
							tmpIds[subids.length] = c;
							cr.setSubids(tmpIds);
						}

						subCopyList.add(cr);

					}
				} else {
					run = false;
					break;
				}
			}
			if (run == true)
				copyList = subCopyList;
		}
		return copyList.toArray(new Copy[copyList.size()]);

	}

	public static String[] parseIds(String copiesString) {
		return parseIds(copiesString, false);

	}

	public static String[] parseIds(String copiesString, boolean ignoreNumbers) {
		Copy[] copies = parse(copiesString, ignoreNumbers);
		String[] result = new String[copies.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = copies[i].getId();
		}
		return result;

	}

	private static String[] parseCopiesElemement(String copies,
			boolean ignoreNumbers) {
		// incrementing ids or predefinied copies?
		String[] ids = null;

		// predefinied
		if (copies.indexOf(",") >= 0) {
			ids = copies.split(",");
		}
		// incrementing ids
		else {
			if (ignoreNumbers) {
				ids = new String[1];
				ids[0] = copies;
			} else {
				try {
					Integer times = new Integer(copies);
					if (times > 0) {
						ids = new String[times];
						for (int i = 0; i < times; i++) {
							ids[i] = "" + i;
						}
					}
				} catch (NumberFormatException ex) {
					ids = new String[1];
					ids[0] = copies;
				}
			}

		}

		return ids;
	}

	public static void addCopyIds(Variables var, Copy copy) {
		var.put("copy.id", copy.getId());
		String[] ids = copy.getSubids();
		for (int i = 0; i < ids.length; i++) {
			var.put("copy.id." + i, ids[i]);
		}
	}

}
