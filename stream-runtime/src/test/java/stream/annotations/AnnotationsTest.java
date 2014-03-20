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
package stream.annotations;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * @author Kai
 * 
 */
public class AnnotationsTest {

    private TestAppender appender = new TestAppender();

    @Before
    public void setupLogger(){
        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        rootLogger.addAppender(appender);
    }

    @After
    public void removeLogger(){
        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        rootLogger.removeAppender(appender);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testValidParameters() throws Exception {
		System.setProperty("process.multiply", "true");
		URL url = AnnotationsTest.class.getResource("/annotations_test.xml");
		stream.run.main(url);
	}

    @Test
    public void testInvalidParameters() throws Exception {
        thrown.expect(ParameterException.class);
        thrown.expectMessage("missing parameter");
        System.setProperty("process.multiply", "true");
        URL url = AnnotationsTest.class.getResource("/annotations_invalid_test.xml");
        stream.run.main(url);
    }

    @Test
    public void testMissingSetter() throws Exception {
        thrown.expect(ParameterException.class);
        thrown.expectMessage("missing setter");
        System.setProperty("process.multiply", "true");
        URL url = AnnotationsTest.class.getResource("/missing_setter_test.xml");
        stream.run.main(url);
    }

    @Test
    public void testConflictingFlags() throws Exception {
        System.setProperty("process.multiply", "true");
        URL url = AnnotationsTest.class.getResource("/annotations_conflicting_flags_test.xml");
        stream.run.main(url);
        assertTrue("Expected a log warning containg the string \"conflicting annotations\" " ,
                expectedLogOutput(appender.getLog(), "conflicting", Level.WARN));
    }

    //takes a log and a message string and checks wether the log contains a message that contains the string
    private boolean expectedLogOutput(List<LoggingEvent> log, String expectedMessage, Level l){
        for (LoggingEvent e : log){
            if (e.getLevel() == l){
                if(e.getRenderedMessage().contains(expectedMessage)){
                    return true;
                }
            }
        }
        return false;
    }

    class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        public List<LoggingEvent> getLog() {
            return new ArrayList<LoggingEvent>(log);
        }
    }



}