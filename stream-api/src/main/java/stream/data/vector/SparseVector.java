/**
 * 
 */
package stream.data.vector;

import java.io.Serializable;

/**
 * @author chris
 *
 */
public class SparseVector
	implements Serializable, Vector
{
	/** The unique class ID */
	private static final long serialVersionUID = 8773169606200673610L;

	
	int[] indexes = new int[0];
	double[] values = new double[0];
	double y;

	public SparseVector(){
		indexes = new int[0];
		values = new double[0];
		y = 0.0d;
	}

	public SparseVector( SparseVector vec ){
		this( vec.indexes, vec.values, vec.y, true );
	}


	public SparseVector( int[] indexes, double[] values, double y) {
		this( indexes, values, y, true );
	}

	public SparseVector( int[] indexes, double[] values, double y, boolean copy ){
		if( !copy ){
			this.indexes = indexes;
			this.values = values;
		} else {
			this.indexes = new int[ indexes.length ];
			this.values = new double[ indexes.length ];

			for( int i = 0; i < indexes.length; i++ ){
				this.indexes[i] = indexes[i];
				this.values[i] = values[i];
			}
		}

		this.y = y;
	}
	
	public double getLabel(){
		return y;
	}
	

	public Vector scale( double d ){
		for( int i = 0; i < values.length; i++ )
			values[i] = d * values[i];
		
		return this;
	}

	public SparseVector add( double factor, SparseVector x ){

		int[] rind = new int[ indexes.length + x.indexes.length ];
		double[] rval = new double[ indexes.length + x.indexes.length ];


		int i = 0;
		int j = 0;
		int k = 0;

		while( i < indexes.length && j < x.indexes.length ){

			if( indexes[i] == x.indexes[j] ) {
				rind[k] = indexes[i];
				rval[k++] = values[i++] + factor * x.values[j++];
			} else {
				
				if( indexes[i] < x.indexes[j] ){
					rind[k] = indexes[i];
					rval[k++] = values[i++];
				} else {
					rind[k] = x.indexes[j];
					rval[k++] = factor * x.values[j++];
				}
			}
		}

		while( i < indexes.length ){
			rind[k] = indexes[i];
			rval[k++] = values[i++];
		}


		while( j < x.indexes.length ){
			rind[k] = x.indexes[j];
			rval[k++] = x.values[j++];
		}

		
		for( int l = k; l < rind.length; l++ ){
			rind[l] = -1;
			rval[l] = Double.NaN;
		}

		return new SparseVector( rind, rval, y, false );
	}
	
	
	public double innerProduct( SparseVector x ){
		int i = 0;
		int j = 0;
		double sum = 0.0d;

		while( i < indexes.length && j < x.indexes.length ){

			if( indexes[i] == x.indexes[j] ) {
				sum += values[i++] * x.values[j++];
			} else {
				
				if( indexes[i] < x.indexes[j] ){
					i++;
				} else {
					j++;
				}
			}
		}
		
		return sum;
	}


	public double get( int i ){
		for( int k = 0; k < indexes.length; k++ ){
			if( indexes[k] >= 0 && indexes[k] == i )
				return values[k];

			if( indexes[k] < 0 || indexes[k] > i )
				return 0.0d;
		}

		return 0.0d;
	}

	
	public double norm(){
		return Math.sqrt( snorm() );
	}
	

	public double snorm(){
		double sum = 0.0d;

		for( int i = 0; i < values.length && indexes[i] >= 0; i++ )
			sum += values[i] * values[i];

		return sum;
	}


	public int size(){
		int i = 0;
		while( i < indexes.length && indexes[i] >= 0 )
			i++;

		return i;
	}

	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append( y );
		s.append( " | " );
		for( int i = 0; i < indexes.length && indexes[i] >= 0; i++ ){
			s.append( " " );
			s.append( indexes[i] );
			s.append( ":" );
			s.append( values[i] );
		}

		return s.toString();
	}

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#set(int, double)
	 */
	@Override
	public void set(int j, double d) {
		
		int pos = -1;
		
		for( int i = 0; i < indexes.length; i++ ){
			if( indexes[i] == j ){
				values[i] = d;
				return;
			}
			
			if( pos < 0 && indexes[i] > j )
				pos = i;
		}
		
		
		int[] rind = new int[ indexes.length + 1 ];
		double[] rval = new double[ indexes.length + 1 ];
		
		int off = 0;
		
		for( int k = 0; k < indexes.length; k++ ){
			if( k == pos ){
				rind[k] = j;
				rval[k] = d;
				off = 1;
			} else {
				rind[k + off] = indexes[k];
				rval[k + off] = values[k];
			}
		}
			
		this.indexes = rind;
		this.values = rval;
	}
	

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#add(stream.data.vector.Vector)
	 */
	@Override
	public void add(Vector vec) {
		
	}

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#add(double, stream.data.vector.Vector)
	 */
	@Override
	public void add(double scale, Vector vec) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#innerProduct(stream.data.vector.Vector)
	 */
	@Override
	public double innerProduct(Vector vec) {
		// TODO Auto-generated method stub
		return 0;
	}
}