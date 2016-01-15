/**
 * 
 */
package streams.service;

import java.io.Serializable;
import java.util.Map;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

/**
 * This processor queries the lookup service (database) for each processed item,
 * merging the query result into the input item before returning it. This class
 * will throw an exception, if no lookup-service is provided.
 * 
 * It is meant to test the field-injection for services.
 * 
 * @author Christian Bockermann
 *
 */
public class LookupMethod extends AbstractProcessor {

    TestLookupService database;

    /**
     * @return the database
     */
    public TestLookupService getDatabase() {
        return database;
    }

    /**
     * @param database
     *            the database to set
     */
    public void setDatabase(TestLookupService database) {
        this.database = database;
    }

    /**
     * @see stream.AbstractProcessor#init(stream.ProcessContext)
     */
    @Override
    public void init(ProcessContext ctx) throws Exception {
        super.init(ctx);

        if (database == null) {
            throw new Exception("No database for lookups provided!");
        }
    }

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data input) {
        Map<String, Serializable> ext = database.lookup(input);
        input.putAll(ext);
        return input;
    }
}