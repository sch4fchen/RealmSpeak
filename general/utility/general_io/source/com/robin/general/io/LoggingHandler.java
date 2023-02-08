package com.robin.general.io;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.logging.*;

/**
 * Sends logging messages to the console, either System.out or System.err, depending on the level of the LogRecord
 */
public class LoggingHandler extends Handler {
	
	private static Logger logger = Logger.getLogger(LoggingHandler.class.getName());
	
	private static int ERR_LEVEL_VALUE_THRESHOLD = Level.WARNING.intValue(); // at this level and above, output sent to System.err; below sent to System.out
	private boolean doneHeader = false; // has formatter header been printed
	
	public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String msg;
        try {
            msg = getFormatter().format(record);
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        try {
        		PrintStream stream = null;
        		if (record.getLevel().intValue() >= ERR_LEVEL_VALUE_THRESHOLD) {
        			stream = System.err;
        		} else {
        			stream = System.out;
         		}
            if (!doneHeader) {
                stream.print(getFormatter().getHead(this));
                doneHeader = true;
            }
            stream.print(msg);
         } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
	}

	public void flush() {
		// not needed
	}

	public void close() throws SecurityException {
		// if the formatter head has been printed, print the tail, but don't need to close System.out or System.err
		// not sure whether to print tail to out or err.  Just do out for now.
		if (doneHeader) {
			try {
				System.out.print(getFormatter().getTail(this));
			} catch (Exception ex) {
	            // We don't want to throw an exception here, but we
	            // report the exception to any registered ErrorManager.
				reportError("Failure writing tail", ex, ErrorManager.WRITE_FAILURE);
			}
		}
	}
	public static void initLogging() {
		LoggingHandler loggingHandler = new LoggingHandler();
		loggingHandler.setLevel(Level.ALL);
		loggingHandler.setFormatter(new LoggingFormatter());
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[0]);
		}
		rootLogger.addHandler(loggingHandler);
		rootLogger.setLevel(Level.WARNING); // default to warning
		
		for (Iterator i=System.getProperties().keySet().iterator();i.hasNext();) {
			String propertyKey = (String)i.next();
			if (propertyKey.startsWith("com")) {
				Logger aLogger = Logger.getLogger(propertyKey);
				String property = System.getProperty(propertyKey);
				aLogger.setLevel(Level.parse(property));
System.out.println("Logging for "+propertyKey+": "+property);
			}
		}
	}
	
	public static void main(String[] args) {
		initLogging();
		logger.info("info test");
		logger.fine("fine test");
		logger.finer("finer test");
		logger.finest("finest test");
	}
}