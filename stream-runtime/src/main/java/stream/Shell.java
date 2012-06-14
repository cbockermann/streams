package stream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import stream.runtime.rpc.RMINamingService;
import stream.service.NamingService;

public class Shell {

	static String prompt = "streams> ";
	static NamingService namingService;
	
	public static String eval( String line ){

		
		return "";
	}
	
	
	public static void repl( InputStream in, OutputStream out ) throws Exception {
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		PrintWriter writer = new PrintWriter( out );
		
		writer.print( prompt );
		writer.flush();
		String line = reader.readLine();
		while( line != null ){
			
			line = line.trim();
			if( line.equalsIgnoreCase("quit" ) || line.equalsIgnoreCase( "exit" ) ){
				break;
			}
			
			String output = eval( line );
			writer.println( output );
			writer.print( prompt );
			writer.flush();
			line = reader.readLine();
		}
		
		reader.close();
		writer.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println( "Connecting to RMI naming service at port 9105..." );
		namingService = new RMINamingService();
		System.out.println( "Naming service is: " + namingService );
		
		repl( System.in, System.out );
		
	}
}
