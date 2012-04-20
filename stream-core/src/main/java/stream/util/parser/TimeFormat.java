/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.util.parser;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * <p>
 * This class provides some utility functions useful to format elapsed time (usually
 * given in milliseconds).
 * </p>
 * @author Christian Bockermann &lt;chris@jwall.org&g;
 *
 */
public class TimeFormat
{
    public final static int SHORT_FORMAT = 0;
    public final static int LONG_FORMAT = 1;
    
    /* */
    public final static long SEC_MS = 1000;

    /* Milliseconds of one minute */
    public final static long MIN_MS = 60 * SEC_MS;

    /* Milliseconds of one hour */
    public final static long HOUR_MS = 60 * MIN_MS;

    /* Milliseconds of one day */
    public final static long DAY_MS = 24 * HOUR_MS;

    /* Milliseconds of one year (using 365,5 days) */
    public final static long YEAR_MS = DAY_MS * 365; // + 12 * HOUR_MS;

    /* The number format for formatting the seconds */
    static final NumberFormat fmt = null; //new DecimalFormat( "0.000" );

    static final long[] UNITS = {
        YEAR_MS, DAY_MS, HOUR_MS, MIN_MS
    };

    static final String[] UNIT_NAME = {
        " year", " day", "h", "m"
    };

    
    static final String[] UNIT_LONG_NAMES = {
        " year", " day", " hour", " minute"
    };
    
    
    private String[] format = UNIT_NAME;
    private int style = 0;
    NumberFormat secondFormat = new DecimalFormat( "0.00" );

    public TimeFormat(){
        this( TimeFormat.SHORT_FORMAT );
    }

    public TimeFormat( int style ){
        
        this.style = style;
        format = UNIT_NAME;
        
        if( style == LONG_FORMAT ){
            format = UNIT_LONG_NAMES;
            secondFormat = new DecimalFormat( "0" );
        }
    }
    

    /**
     * <p>
     * This method takes the given number of milliseconds, <code>time</code>, and creates a
     * new String containing a description of the time by means of days, hours, minutes and
     * seconds. If <code>time</code> is less than any of the mentioned properties, then this
     * field will not be printed, e.g.
     * <ul>
     *   <li>calling <code>format( 1000 )</code> will result in the string <code>&quot;1s&quot;</code> </li>
     *   
     *   <li>calling <code>format( 90000 * 1000 )</code>, i.e. milliseconds of one day + 1 hour, will
     *       result in <code>&quot;1 day 1h&quot;</code>.
     *   </li>
     * </ul>
     * </p>
     * <p>
     * This method is optimized over the old version (<code>formatOld()</code).
     * </p>
     * 
     * @param timeInMilliseconds The time as an amount of milliseconds.
     * @return The time formatted as printable string.
     */
    public String format( long timeInMilliseconds ){

        long ms = timeInMilliseconds;
        long left = ms;
        long units = 0;

        StringBuilder s = new StringBuilder();
        
        for( int i = 0; i < UNITS.length; i++ ){

            long unit = UNITS[i];
            if( ms > unit ){
                left = timeInMilliseconds % unit;
                units = ( ms - left ) / unit;
                s.append( units );
                if( style == TimeFormat.LONG_FORMAT )
                    s.append( " " );
                
                s.append( format[ i ] );
                if( units > 1 && i > 0 )
                    s.append("s");
                s.append( " " );
                ms = left;
            }
        }

        double sec = (( double ) ms) / (double) SEC_MS;
        if( s.length() == 0 || sec > 0 ){
            s.append( secondFormat.format( sec ) );
            if( style == LONG_FORMAT )
                s.append( " sec" );
            else
                s.append( "s" );
        }
        return s.toString();
    }
}