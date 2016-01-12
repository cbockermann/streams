/**
 * 
 */
package stream.test;

import java.util.UUID;

import stream.runtime.ContainerContext;

/**
 * @author chris
 *
 */
public class RootContextMock extends ContainerContext {

	public RootContextMock() {
		super(UUID.randomUUID().toString());
	}
}
