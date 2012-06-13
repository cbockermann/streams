/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.generator;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Statistics;
import stream.runtime.rpc.RMINamingService;
import stream.statistics.StatisticsService;

/**
 * @author chris
 * 
 */
public class GaussianStreamTest {

	static Logger log = LoggerFactory.getLogger( GaussianStreamTest.class );
	
	@Test
	public void test() throws Exception {
		URL url = GaussianStreamTest.class.getResource("/test-gaussian.xml");
		stream.run.main(url);
	}
	
	
	public static void main( String[] args ){
		try {
			
			RMINamingService ns = new RMINamingService();
			StatisticsService stats = ns.lookup( "throughput", StatisticsService.class );
			
			while( true ){
				Statistics st = stats.getStatistics();
				log.info( "Received statistics: {}", st );
				Thread.sleep( 1000 );
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
