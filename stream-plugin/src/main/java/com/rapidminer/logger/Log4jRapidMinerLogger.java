/**
 * 
 */
package com.rapidminer.logger;

import java.util.logging.Logger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author chris
 * 
 */
public class Log4jRapidMinerLogger extends AppenderSkeleton {

	/**
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent arg0) {
		Logger log = Logger.getLogger(arg0.fqnOfCategoryClass);
		Level level = arg0.getLevel();
	}
}
