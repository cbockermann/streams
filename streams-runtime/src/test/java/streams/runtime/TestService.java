package streams.runtime;

import stream.service.EchoService;

/**
 * Test service for serviceinjection tests. There is not a single simple Service
 * implementation in streams. Created by kai on 14.01.16.
 */
public class TestService implements EchoService {
    @Override
    public void reset() throws Exception {

    }

    /**
     * @see stream.service.EchoService#echo(java.lang.String)
     */
    @Override
    public String echo(String text) {
        return text.toUpperCase() + ".. " + text + ".. " + text.toLowerCase();
    }
}
