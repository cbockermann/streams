/**
 * 
 */
package stream.io;

import java.util.Map;

/**
 * @author chris
 *
 */
public interface PartitionedStream extends Stream {

    public Map<String, Stream> partitions();
}
