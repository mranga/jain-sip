package javax.sip.message;
/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2003 Sun Microsystems, Inc. All rights reserved.
 * Copyright  2005 BEA Systems.,  All rights reserved.
 * Copyright  2010 Oracle Inc., All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : RequestLine.java
 * Author        :  M. Ranganathan
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0     09/06/2010   M. Ranganathan     Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/**
 * An interface for the SIP Status Line.
 * 
 * @since 2.0
 * @version 2.0
 */
public interface SipStatusLine {

    /** get the Sip Version
     * @return SipVersion
     */
    public String getSipVersion();

    /** get the Status Code
     * @return StatusCode
     */
    public int getStatusCode();

    /** get the ReasonPhrase field
     * @return  ReasonPhrase field
     */
    public String getReasonPhrase();

    /**
     * Set the sipVersion member
     * @param sipVersion String to set
     * @throws ParseException - if illegal argument supplied.
     */
    public void setSipVersion(String sipVersion) throws ParseException;

    /**
     * Set the statusCode member
     * @param statusCode  to set
     * @throws InvalidArgumentException - if illegal argument supplied.
     */
    public void setStatusCode(int statusCode) throws InvalidArgumentException;

    /**
     * Set the reasonPhrase member
     * @param reasonPhrase String to set
     * @throws ParseException - if illegal string parsed.
     */
    public void setReasonPhrase(String reasonPhrase) throws ParseException ;

    /**
     * Get the major version number.
     *@return String major version number
     */
    public  String getVersionMajor();

    /**
     * Get the minor version number.
     *@return String minor version number
     */
    public  String getVersionMinor();

}
