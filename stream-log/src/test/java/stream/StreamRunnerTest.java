package stream;

import java.net.URL;

import org.junit.Test;

import stream.runtime.ProcessContainer;

public class StreamRunnerTest
{

	// a test dummy
	@Test
	public void test(){
	}

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        URL url = ProcessContainer.class.getResource( "/demo-shop.xml" );
        ProcessContainer runner = new ProcessContainer( url );
        runner.run();
    }
}