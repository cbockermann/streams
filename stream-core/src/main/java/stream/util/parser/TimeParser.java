package stream.util.parser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.History;

public class TimeParser {
	static Logger log = LoggerFactory.getLogger(TimeParser.class);
	static Map<String, Long> UNITS = new LinkedHashMap<String, Long>();

	static {
		UNITS.put("ms", History.MILLISECOND);
		UNITS.put("years", History.YEAR);
		UNITS.put("months", History.MONTH);
		UNITS.put("weeks", History.WEEK);
		UNITS.put("days", History.DAY);
		UNITS.put("hours", History.HOUR);
		UNITS.put("minutes", History.MINUTE);
		// UNITS.put( "mins", History.MINUTE );
		UNITS.put("seconds", History.SECOND);
		// UNITS.put( "secs", History.SECOND );
	}

	public static Long parseTime(String str) throws Exception {

		long time = 0L;

		String s = str.trim();
		while (s.length() > 0) {

			String[] tok = readInteger(s);
			Integer i = new Integer(tok[0]);

			s = tok[1].trim();
			tok = readUnit(s);
			String unit = tok[0];

			List<String> uk = findUnits(unit);
			if (uk.size() == 1) {
				unit = uk.get(0);
				log.debug("integer i = " + i + ",  unit = " + unit);
			} else {
				if (uk.isEmpty())
					throw new Exception("Unknown time-unit '" + unit
							+ "' found!");

				if (uk.size() > 1)
					throw new Exception("Ambiguous time-unit '" + unit
							+ "' found!");
			}

			Long ms = UNITS.get(unit);
			time += i * ms;

			s = tok[1].trim();
		}

		return time;
	}

	public static String[] readInteger(String str) {

		StringBuffer s = new StringBuffer();
		int i = 0;
		while (i < str.length() && isDigit(str, i))
			s.append(str.charAt(i++));

		return new String[] { s.toString(), str.substring(i).trim() };
	}

	public static String[] readUnit(String str) {

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
			if (key.startsWith(str))
				units.add(key);
		}

		return units;
	}
}