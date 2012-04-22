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
package stream.data.vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Test;

/**
 * @author chris
 *
 */
public class VectorSpeedTest {

	static int NUMBER_OF_VECTORS = 1000;
	static List<Vector> samples = createRandomVectors( NUMBER_OF_VECTORS );
	
	protected static List<Vector> createRandomVectors( int num ){
		ArrayList<Vector> rnds = new ArrayList<Vector>( num );
		for( int i = 0; i < num; i++ ){
			rnds.add( createRandomVector() );
		}
		return rnds;
	}
	
	protected static Vector createRandomVector(){

		Random rnd = new Random();
		int size = rnd.nextInt( 1000 );
		
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		
		for( int i = 0; i < size; i++ ){
			indexes.add( new Integer( rnd.nextInt( size ) ) );
		}

		int[] idx = new int[ indexes.size() ];
		double[] val = new double[ indexes.size() ];
		
		int k = 0;
		Iterator<Integer> it = indexes.iterator();
		while( it.hasNext() ){
			idx[k] = it.next();
			val[k] = rnd.nextDouble();
			k++;
		}
		
		return new Vector( idx, val );
	}
	
	@Test
	public void testDummy(){
		// simple dummy test method
	}
	
	/**
	 * Test method for {@link stream.data.vector.Vector#add(double, stream.data.vector.Vector)}.
	 */
	//@Test
	public void testAddDoubleVector() {
		
		double sizes = 0.0d;
		for( Vector vec : samples ){
			sizes += vec.length();
		}
		System.out.println( "average vector-size is " + (sizes / samples.size()) );
		
		
		Vector sum = new Vector();
		long start = System.currentTimeMillis();

		for( Vector vec : samples ){
			sum = sum.add( 1.0d, vec );
		}
		
		long end = System.currentTimeMillis();
		System.out.println( "Sum is: " + sum );
		//System.out.println( "Size of sum is: " + sum.size() + ", mem-size is: " + sum.memSize() );
		System.out.println( "Summing up " + samples.size() + " random vectors took " + (end-start) + "ms.");
	}
}
