/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author chris
 *
 */
public class CommandLineArgs {

	Map<String,String> options = new LinkedHashMap<String,String>();
	List<String> args = new ArrayList<String>();
	
	
	public CommandLineArgs( String[] args ){
		int i = 0;
		while( i < args.length ){
			
			if( args[i].startsWith( "-" ) && i+1 < args.length && ! args[i+1].startsWith( "--" ) ){
				String key = getOptionName( args[i] );
				String value = args[i+1];
				options.put( key, value );
				i++;
			} else {
				this.args.add( args[i] );
			}
			
			i++;
		}
	}
	
	public List<String> getArguments(){
		return args;
	}
	
	
	public Map<String,String> getOptions(){
		return options;
	}
	
	public String getOption( String key ){
		return getOption( key, null );
	}
	
	public String getOption( String key, String defaultValue ){
		if( options.get( key ) == null )
			return defaultValue;
		return options.get( key );
	}
	
	
	protected String getOptionName( String opt ){
		String str = opt;
		while( str.startsWith( "-" ) )
			str = str.substring(1);
		
		return str.replaceAll( "-", "\\." );
	}
	
	public void setSystemProperties( String prefix ){
		String pre = prefix;
		if( ! prefix.endsWith( "." ) )
			pre = prefix + ".";
		
		for( String opt : options.keySet() ){
			System.setProperty( pre + opt, options.get( opt ) );
		}
	}
	
	public void dumpArgs(){
		for( String opt : getOptions().keySet() ){
			System.out.println( "  option "+ opt + " = " + getOption( opt ) );
		}
		
		for( String arg : getArguments() ){
			System.out.println( "  arg: " + arg );
		}
	}
	
	public static Properties expandSystemProperties( Properties p ){
		Properties result = new Properties();
		
		for( Object key : p.keySet() ){
			String k = key.toString();
			String value = p.getProperty( k );
			if( value.indexOf( "${" ) >= 0 ){
				for( Object o : System.getProperties().keySet() ){
					String os = "${" + o.toString() + "}";
					value = value.replace( os, System.getProperty( o.toString() ) );
				}
				
				result.setProperty( k, value );
				
			} else {
				result.setProperty( k, p.getProperty(k) );
			}
		}

		return result;
	}
	
	public static void populateSystemProperties( Properties p ){
		for( Object k : p.keySet() ){
			if( System.getProperty( k.toString() ) == null ){
				System.setProperty( k.toString(), p.getProperty( k.toString() ) );
			}
		}
	}
	
	
	public static void main( String[] args ){
		String[] params = new String[]{
				"--limit", "120",
				"--block-size", "12",
				"--max-parts", "4",
				"/tmp/test.file",
				"/tmp/out-dir"
		};
		
		CommandLineArgs cla = new CommandLineArgs( params );
		cla.dumpArgs();
	}
}