/**
 * 
 */
package stream.io.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public abstract class DatabaseDialect {

	static Logger log = LoggerFactory.getLogger(DatabaseDialect.class);

	public final static Class<?> BYTE_ARRAY_CLASS = (new byte[0]).getClass();

	final protected Map<Class<?>, String> columnTypeDefs = new HashMap<Class<?>, String>();

	public DatabaseDialect() {
		setColumnType(Double.class, "REAL");
		setColumnType(Float.class, "REAL");
		setColumnType(Integer.class, "INTEGER");
		setColumnType(Long.class, "BIGINT");
		setColumnType(String.class, "VARCHAR(1024)");
		setColumnType(BYTE_ARRAY_CLASS, "BLOB");
	}

	protected void setColumnType(Class<?> clazz, String colDef) {
		log.debug("setting ColumnType for class {} to '{}'", clazz, colDef);
		columnTypeDefs.put(clazz, colDef);
	}

	/**
	 * @see stream.io.sql.DatabaseDialect#getColumnType(java.lang.Class)
	 */
	public String getColumnType(Class<?> clazz) {

		if (columnTypeDefs.containsKey(clazz)) {
			String def = columnTypeDefs.get(clazz);
			log.debug(
					"Found pre-defined column type definition '{}' for class {}",
					def, clazz);
			return def;
		}

		if (clazz == Double.class) {
			log.debug("Mapping class {} to column type 'REAL'", clazz);
			return "REAL";
		}

		if (clazz == Integer.class) {
			log.debug("Mapping class {} to column type 'INTEGER'", clazz);
			return "INTEGER";
		}

		if (clazz == Long.class) {
			log.debug("Mapping class {} to column type 'BIGINT'", clazz);
			return "BIGINT";
		}

		if (clazz.isArray() && clazz.getComponentType() == Byte.class) {
			log.debug("Mapping class array of {} to column type 'BLOB'",
					clazz.getComponentType());
			return "BLOB";
		}

		if (clazz == String.class) {
			log.debug("Mapping class {} to column type 'VARCHAR(255)'", clazz);
			return "VARCHAR(255)";
		}

		log.debug("Mapping class {} to default column type 'TEXT'", clazz);
		return "TEXT";
	}

	/**
	 * @param t
	 * @return
	 */
	public Class<?> mapTypeToClass(int t) {

		switch (t) {
		case java.sql.Types.FLOAT:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.DECIMAL:
			return Double.class;

		case java.sql.Types.INTEGER:
			return Integer.class;

		case java.sql.Types.BIGINT:
			return Long.class;

		case java.sql.Types.BOOLEAN:
		case java.sql.Types.BIT:
			return Boolean.class;

		case java.sql.Types.BINARY:
			return BYTE_ARRAY_CLASS;
		}

		return String.class;
	}

	/**
	 * @see stream.io.sql.DatabaseDialect#mapColumnName(java.lang.String)
	 */
	public String mapColumnName(String key) {
		if (key.startsWith("@")) {
			return "at_" + key.substring(1);
		}

		return key;
	}

	public String getCreatePrimaryKeySequence(String key) {
		return null;
	}

	public String getPrimaryKeyDefinition(String key) {
		return key;
	}

	public String getCreateTableCommand(String name, Map<String, Class<?>> types) {
		StringBuffer s = new StringBuffer();
		s.append("CREATE TABLE " + name + " (\n");
		Iterator<String> it = types.keySet().iterator();
		while (it.hasNext()) {

			String key = it.next();

			String colName = mapColumnName(key);
			String colType = getColumnType(types.get(key));

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

	/**
	 * @see stream.io.sql.DatabaseDialect#unmapColumnName(java.lang.String)
	 */
	public String unmapColumnName(String column) {
		if (column.startsWith("AT_"))
			return "@" + column.substring(3).toLowerCase();

		return column.toLowerCase();
	}

	public Map<String, Class<?>> getTableSchema(Connection con, String table) {

		Statement stmt = null;
		ResultSet rs = null;

		try {
			Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();

			stmt = con.createStatement();
			stmt.setMaxRows(1);
			rs = stmt.executeQuery("SELECT * FROM " + table);

			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 0; i < meta.getColumnCount(); i++) {
				String name = meta.getColumnName(i + 1);
				int type = meta.getColumnType(i + 1);
				Class<?> clazz = mapTypeToClass(type);

				name = this.unmapColumnName(name);
				log.info("Adding  {} => {}", name, clazz);
				types.put(name, clazz);
			}
			rs.close();
			stmt.close();
			return types;

		} catch (Exception e) {
			log.error("Failed to retrieve schema of table {}: {}", table,
					e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return null;
		} finally {
			try {
				if (stmt != null)
					stmt.close();

				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, Class<?>> getColumnTypes(Data item) {
		Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();
		for (String key : item.keySet()) {
			log.info("Checking type of key {}", key);
			types.put(key, item.get(key).getClass());
		}
		return types;
	}
}
