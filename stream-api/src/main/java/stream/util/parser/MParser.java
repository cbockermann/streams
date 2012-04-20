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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MParser {

	int pos = 0;
	
	public MParser(){
		
	}
	
	
	public void reset(){
		pos = 0;
	}
	
	public void skip( int i ){
		pos += i;
	}
	
	public void skipBlanks( String str ){
		while( pos < str.length() && Character.isWhitespace( str.charAt( pos ) ) )
			pos++;
	}
	
	public int position(){
		return pos;
	}
	
	public String remainder( String str ){
		if( pos < str.length() )
			return str.substring( pos );
		return "";
	}
	
	
	public Character firstChar( String str ){
		if( pos < str.length() )
			return str.charAt( pos );
		return null;
	}
	
	public String prefix( String str, int len ){
	    StringBuffer s = new StringBuffer();
	    int i = pos;
	    while( i < str.length() && i < pos + len ){
	        s.append( str.charAt( i++ ) );
	    }
	    return s.toString();
	}
	
	
	public String readToken( String str ){
		Pair<String,Integer> tok = readToken( str, pos );
		pos = tok.value;
		return tok.key;
	}
	
	public String readTokenUntil( String str, String prefix ){
        Pair<String,Integer> tok = readToken( str, pos, prefix );
        pos = tok.value;
        return tok.key;
	}
	
	public String readToken( String str, char open, char close ){
		Pair<String,Integer> tok = readToken( str, pos, open, close );
		pos = tok.value;
		return tok.key;
	}
	
	
	public Integer readInteger( String str ){
		this.skipBlanks( str );
		StringBuffer s = new StringBuffer();
		while( pos < str.length() && Character.isDigit( str.charAt( pos ) ) ){
			s.append( str.charAt( pos ) );
			pos++;
		}

		return new Integer( s.toString() );
	}

	
	public static boolean isEmpty( String str ){
		return str == null || str.trim().isEmpty();
	}
	
	public static Pair<String,Integer> readToken( String str, int offset ){
		int start = offset;
		
		if( start >= str.length() )
			return new Pair<String,Integer>( "", str.length() );
		
		while( start < str.length() && Character.isWhitespace( str.charAt( start ) ) )
			start++;
		
		int end = start;
		if( str.charAt( start ) == '"' ){
			return readToken( str, start, '"', '"' );
		}
		
		if( str.charAt( start ) == '(' )
			return readToken( str, start, '(', ')' );
		
		if( str.charAt( start ) == '[' )
			return readToken( str, start, '[', ']' );
		
		while( end < str.length() && str.charAt( end ) != ' ' )
			end++;
		
		return new Pair<String,Integer>( str.substring( start, end ), end );
	}
	
	   public static Pair<String,Integer> readToken( String str, int offset, String boundary ){
	        int start = offset;
	        
	        if( start >= str.length() )
	            return new Pair<String,Integer>( "", str.length() );
	        
	        while( start < str.length() && Character.isWhitespace( str.charAt( start ) ) )
	            start++;
	        
	        int end = start;
	        if( str.charAt( start ) == '"' ){
	            return readToken( str, start, '"', '"' );
	        }
	        
	        if( str.charAt( start ) == '(' )
	            return readToken( str, start, '(', ')' );
	        
	        if( str.charAt( start ) == '[' )
	            return readToken( str, start, '[', ']' );
	        
	        while( end < str.length() && !str.startsWith( boundary, end ) )
	            end++;
	        
	        return new Pair<String,Integer>( str.substring( start, end ), end );
	    }
	
	public static Pair<String,Integer> readToken( String str, int offset, char opening, char closing ){
		int start = offset;
		
		while( start < str.length() && str.charAt( start ) != opening )
			start++;
		
		if( str.charAt( start ) == opening ){
			int i = start + 1;
			while( i < str.length() && str.charAt( i ) != closing ){
				i++;
			}
			if( i + 1 < str.length() )
				i++;
			
			return new Pair<String,Integer>( str.substring( start, i + 1 ), i + 1 );
		}
		
		return new Pair<String,Integer>( "", offset );
		
	}

	
	public static String trim( String str, String tr ){
		if( str.startsWith( tr ) && str.endsWith( tr ) ){
			return str.substring( tr.length(), str.length() - 2 * tr.length() );
		}
		return str;
	}
	

	
	public static class Pair<K,V> {
		public final K key;
		public final V value;
		
		public Pair( K key, V val ){
			this.key = key;
			this.value = val;
		}
	}
	
	
	public static boolean isBlank( String str ){
		return str == null || str.trim().equals( "" );
	}
	
	
	static String line = "66.249.65.43 - - [22/Nov/2009:18:39:12 +0100]  \"GET /web/policy/editor.jsp HTTP/1.1\" 200 6065 \"-\" \"Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)\"";
	
	public static void main( String[] args ) throws Exception {
		
		String str = line;
		
		List<String> tokens = new LinkedList<String>();
		
		MParser p = new MParser();
		tokens.add( p.readToken( str ) );
		tokens.add( p.readToken( str ) );
		tokens.add( p.readToken( str ) );
		//tokens.add( p.readToken( str, '[', ']') );
		tokens.add( p.readToken( str ) );
		
		String tok = p.readToken( str );
		while( tok != null && ! tok.equals( "" ) ){
			tokens.add( tok );
			tok = p.readToken( str );
		}

		System.out.println( "----------------------------------------------------");
		System.out.println();
		int i = 0;
		for( String s : tokens ){
			System.out.println( "   token[" + i + "]: " + s );
			i++;
		}
		System.out.println();
		System.out.println( "----------------------------------------------------");
		System.out.println();
		System.out.println();
		
		
		p.reset();
		
		Map<String,String> map = new LinkedHashMap<String,String>();
		p.reset();
		map.put( "REMOTE_ADDR", p.readToken( str ) );
		map.put( "REMOTE_USER", p.readToken( str ) );
		map.put( "__UNKNOWN__", p.readToken( str ) );
		map.put( "DATE", p.readToken( str, '[', ']' ) );
		map.put( "REQUEST_LINE", p.readToken( str ) ); //, '"', '"' ) );
		map.put( "RESPONSE_STATUS", p.readToken( str ) );
		map.put( "RESPONSE_SIZE", p.readToken( str ) );
		map.put( "REFERER", p.readToken( str ) );
		map.put( "USER_AGENT", p.readToken( str ) );
		
		for( String key : map.keySet() ){
			System.out.println( "  " + key + "  => " + map.get( key ) );
		}
	}
}