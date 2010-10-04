package gov.nist.core;



import java.util.Properties;


/*
 * Conditions Of Use
 *
 * This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 Untied States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 *
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof, including but
 * not limited to the correctness, accuracy, reliability or usefulness of
 * the software.
 *
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 *
 */
/**
 * interface that loggers should implement so that the stack can log to various
 * loggers impl such as log4j, commons logging, sl4j, ...
 * 
 * @author Jean Deruelle
 *
 */
public interface StackLogger extends LogLevels {

    /**
     * log a stack trace. This helps to look at the stack frame.
     */
	public void logStackTrace();
	
	/**
	 * Log a stack trace if the current logging level exceeds 
	 * given trace level.
	 * @param traceLevel
	 */
	public void logStackTrace(int traceLevel);
	
	/**
     * Get the line count in the log stream.
     *
     * @return
     */
	public int getLineCount();
	
	/**
     * Log an exception.
     *
     * @param ex
     */
    public void logException(Throwable ex);
    /**
     * Log a message into the log file.
     *
     * @param message
     *            message to log into the log file.
     */
    public void logDebug(String message);
    /**
     * Log a message into the log file.
     *
     * @param message
     *            message to log into the log file.
     */
    public void logTrace(String message);
    /**
     * Log an error message.
     *
     * @param message --
     *            error message to log.
     */
    public void logFatalError(String message);
    /**
     * Log an error message.
     *
     * @param message --
     *            error message to log.
     *
     */
    public void logError(String message);
    /**
     * @return flag to indicate if logging is enabled.
     */
    public boolean isLoggingEnabled();
    /**
     * Return true/false if loging is enabled at a given level.
     *
     * @param logLevel
     */
    public boolean isLoggingEnabled(int logLevel);
    /**
     * Log an error message.
     *
     * @param message
     * @param ex
     */
    public void logError(String message, Exception ex);
    /**
     * Log a warning mesasge.
     *
     * @param string
     */
    public void logWarning(String string);
    /**
     * Log an info message.
     *
     * @param string
     */
    public void logInfo(String string);
    
   
    /**
     * Disable logging altogether.
     *
     */
    public void disableLogging();

    /**
     * Enable logging (globally).
     */
    public void enableLogging();
    
    /**
     * Set the build time stamp. This is logged into the logging stream.
     */
    public void setBuildTimeStamp(String buildTimeStamp);
    
    /**
     * Stack creation properties.
     * @param stackProperties
     */
    
    public void setStackProperties(Properties stackProperties);
    
    /**
     * The category for the logger.
     * @return
     */
    public String getLoggerName();
    
    
   
}
