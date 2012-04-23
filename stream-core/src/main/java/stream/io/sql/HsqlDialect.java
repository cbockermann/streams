/**
 * 
 */
package stream.io.sql;

/**
 * @author chris
 * 
 */
public class HsqlDialect extends DatabaseDialect {

	public String AT_SUBSTITUTION = "AT_";

	public HsqlDialect() {
	}

	/**
	 * @see stream.io.sql.DatabaseDialect#mapColumnName(java.lang.String)
	 */
	@Override
	public String mapColumnName(String key) {

		if (key.startsWith("@")) {
			return AT_SUBSTITUTION + key.substring(1).toUpperCase();
		}

		return super.mapColumnName(key.toUpperCase());
	}
}
