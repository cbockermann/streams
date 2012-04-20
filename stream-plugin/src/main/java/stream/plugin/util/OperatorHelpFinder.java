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
package stream.plugin.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.URLUtilities;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author chris
 *
 */
public class OperatorHelpFinder {
	
	static Logger log = LoggerFactory.getLogger( OperatorHelpFinder.class );

	public static String findOperatorHelp( Class<?> clazz ) throws Exception {
		return findOperatorHelp( clazz.getCanonicalName() );
	}
	
	
	public static String findOperatorHelp( String className ) throws Exception {

		String doc = "/" + className.replaceAll( "\\.", "/" ) + ".md";
		URL url = OperatorGenerator.class.getResource( doc );
		if( url != null ){
			String txt = URLUtilities.readContent( url );
			log.debug( "Found documentation at {}", url );

			MarkdownProcessor markdown = new MarkdownProcessor();
			String html = markdown.markdown( txt );

			if( html.startsWith( "<h1>" ) ){
				int end = html.indexOf( "</h1>" );
				if( end > 0 ){
					html = html.substring( end + "</h1>".length() );
				}
			}

			log.debug( "Html documentation:\n{}", html.trim() );
			return html;
		}
		
		return null;
	}
}
