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
package stream.io.sql;

import java.util.Iterator;
import java.util.Map;

/**
 * @author chris
 * 
 */
public class MysqlDialect extends DatabaseDialect {

	/**
	 * @see stream.io.sql.DatabaseDialect#getColumnType(java.lang.Class)
	 */
	@Override
	public String getColumnType(Class<?> clazz) {

		if (clazz == Double.class)
			return "REAL";

		if (clazz == Long.class)
			return "BIGINT";

		if (clazz == Float.class)
			return "REAL";

		if (clazz == String.class)
			return "TEXT";

		return super.getColumnType(clazz);
	}

	/**
	 * @see stream.io.sql.DatabaseDialect#mapColumnName(java.lang.String)
	 */
	@Override
	public String mapColumnName(String key) {
		return "`" + key + "`";
	}

	/**
	 * @see stream.io.sql.DatabaseDialect#unmapColumnName(java.lang.String)
	 */
	@Override
	public String unmapColumnName(String column) {
		String col = column;
		if (col.startsWith("`"))
			col = col.substring(1);

		if (col.endsWith("`")) {
			col = col.substring(0, col.length() - 1);
		}

		return col;
	}

	public String getCreateTableCommand(String name, Map<String, Class<?>> types) {
		String primaryKey = null;
		StringBuffer s = new StringBuffer();
		s.append("CREATE TABLE " + name + " (\n");
		Iterator<String> it = types.keySet().iterator();
		while (it.hasNext()) {

			String key = it.next();
			String colName = mapColumnName(key);
			String colType = getColumnType(types.get(key));

			if (key.startsWith("@id") && primaryKey == null) {
				colType = getColumnType(types.get(key))
						+ " NOT NULL PRIMARY KEY ";
				if (types.get(key) == Integer.class
						|| types.get(key) == Long.class) {
					colType += " AUTO_INCREMENT";
				}
			}

			s.append(" ");
			s.append(colName);
			s.append(" ");
			s.append(colType);

			if (it.hasNext()) {
				s.append(",\n");
			}
		}

		s.append(" )\n");
		return s.toString();
	}
}
