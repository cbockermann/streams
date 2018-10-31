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
package stream.util.parser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteSizeParser {
	static Logger log = LoggerFactory.getLogger(ByteSizeParser.class);
	static Map<String, Long> UNITS = new LinkedHashMap<String, Long>();

	public final static Long BYTE = 1L;
	public final static Long KILOBYTE = 1024 * BYTE;
	public final static Long MEGABYTE = 1024 * KILOBYTE;
	public final static Long GIGABYTE = 1024 * MEGABYTE;
	public final static Long TERABYTE = 1024 * GIGABYTE;
	public final static Long PETABYTE = 1024 * TERABYTE;

	static {
		UNITS.put("b", BYTE);
		UNITS.put("kb", KILOBYTE);
		UNITS.put("mb", MEGABYTE);
		UNITS.put("gb", GIGABYTE);
		UNITS.put("tb", TERABYTE);
		UNITS.put("pb", PETABYTE);
	}

	public static Long parseByteSize(String str) throws Exception {

		Double bytes = 0.0;

		String s = str.trim();
		while (s.length() > 0) {

			String[] tok = readLong(s);
			Double i = new Double(tok[0]);

			s = tok[1].trim();
			tok = readUnit(s);
			String unit = tok[0];

			List<String> uk = findUnits(unit);
			if (uk.size() == 1) {
				unit = uk.get(0);
				log.debug("integer i = " + i + ",  unit = " + unit);
			} else {
				if (uk.isEmpty()) {
					uk.add("b");
				}

				if (uk.size() > 1)
					throw new Exception("Ambiguous time-unit '" + unit
							+ "' found!");
			}

			Long ms = UNITS.get(unit);
			bytes += i * ms;

			s = tok[1].trim();
		}

		return bytes.longValue();
	}

	public static String[] readLong(String str) {

		StringBuffer s = new StringBuffer();
		int i = 0;
		int separators = 0;
		while (i < str.length() && (isDigit(str, i) || (separators < 1 && isDecimalSeparator(str, i))))
			s.append(str.charAt(i++));

		return new String[] { s.toString().replace( ',', '.'), str.substring(i).trim() };
	}
	
	public static boolean isDecimalSeparator(String str, int i ) {
	        char c = str.charAt(i);
	        return c == ',' || c == '.';
	}

	public static String[] readUnit(String str) {

		if (str.trim().isEmpty()) {
			return new String[] { "b", "" };
		}

		StringBuffer s = new StringBuffer();
		int i = 0;
		while (i < str.length() & isUnitChar(str, i))
			s.append(str.charAt(i++));

		return new String[] { s.toString(), str.substring(i) };
	}

	public static boolean isDigit(String str, int i) {
		if (i >= 0 && i < str.length()) {
			char ch = str.charAt(i);
			return Character.isDigit(ch);
		} else
			return false;
	}

	public static boolean isUnitChar(String str, int i) {
		if (i >= 0 && i < str.length()) {
			char ch = str.charAt(i);
			return Character.isLetter(ch);
		} else
			return false;
	}

	public static List<String> findUnits(String str) {

		LinkedList<String> units = new LinkedList<String>();

		for (String key : UNITS.keySet()) {
			if (key.startsWith(str.toLowerCase())) {
				units.add(key);
			}
		}
		log.debug("Units: {}", units);
		return units;
	}
}