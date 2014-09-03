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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class TrustAllManager 
	implements X509TrustManager, TrustManager 
{
	private static Logger log = LoggerFactory.getLogger( TrustAllManager.class );
	
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {

		log.debug( "checkClientTrusted: \n");
		
		for( X509Certificate cert : chain ){
			log.info("-------------------------------------------------------");
			log.debug( " SubjectDN = "+cert.getSubjectDN() );
			log.debug( " Issuer = " + cert.getIssuerDN() );
		}		
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		
		log.debug( "checkServerTrusted: \n");
		
		for( X509Certificate cert : chain ){
			log.debug("-------------------------------------------------------");
			log.debug( " SubjectDN = "+cert.getSubjectDN() );
			log.debug( " Issuer = " + cert.getIssuerDN() );
		}		
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}