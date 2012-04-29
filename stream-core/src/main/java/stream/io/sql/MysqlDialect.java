/**
 * 
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
