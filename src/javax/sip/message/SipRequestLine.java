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
package javax.sip.message;
import javax.sip.address.URI;

/**
 * An interface for the SIP Request Line.
 * 
 * @since 2.0
 * @version 2.0
 */
public interface SipRequestLine {

    /** get the Request-URI.
     *
     * @return the request URI
     */
    public  URI getUri();

    /**
     * Get the Method
     *
     * @return method string.
     */
    public  String getMethod();

    /**
     * Get the SIP version.
     *
     * @return String
     */
    public  String getSipVersion();

    /**
     * Set the URI.
     * 
     * @param uri URI to set.
     */
    public  void setUri(URI uri);

    /**
     * Set the method member
     *
     * @param method String to set
     */
    public  void setMethod(String method);

    /**
     * Set the sipVersion member
     *
     * @param version version String to set
     */
    public  void setSipVersion(String version);

    /**
     * Get the major verrsion number.
     *
     *@return String major version number
     */
    public  String getVersionMajor();

    /**
     * Get the minor version number.
     *
     *@return String minor version number
     *
     */
    public  String getVersionMinor();

}
