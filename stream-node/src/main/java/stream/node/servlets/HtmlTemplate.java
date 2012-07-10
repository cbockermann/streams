package stream.node.servlets;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

public class HtmlTemplate {

	public static String expand(String resource, Properties vars) {

		try {

			StringBuffer s = new StringBuffer();
			InputStream stream = HtmlTemplate.class
					.getResourceAsStream(resource);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			String line = reader.readLine();
			while (line != null) {
				s.append(line);
				s.append("\n");
				line = reader.readLine();
			}
			reader.close();

			String str = s.toString();

			Iterator it = vars.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				String value = vars.getProperty(key.toString());

				str = str.replace("${" + key.toString() + "}", value);
			}

			return str;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}
