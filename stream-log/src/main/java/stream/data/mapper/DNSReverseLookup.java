/**
 * 
 */
package stream.data.mapper;

import stream.runtime.annotations.Description;

/**
 * @author chris
 *
 */
@Description( name = "DNS Reverse lookup", group="Data Stream.Processing.Annotations" )
public class DNSReverseLookup extends DNSLookup {

	/**
	 * @see stream.data.mapper.DNSLookup#resolve(java.lang.String)
	 */
	@Override
	protected String resolve(String name) {
		return this.reverseLookup( name );
	}
}
