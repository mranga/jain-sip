/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2010 Oracle Inc., All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : OrganizationHeader.java
 * Author        : Alexandre Miguel Silva Santos
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0     09/06/2010   Alexandre Miguel Silva Santos   
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*******************************************
 * PRODUCT OF PT INOVACAO- EST DEPARTMENT  *
 *******************************************/
package javax.sip.header;



/**
 * SIP Path Header. 
 * The Path extension header field allows accumulating and transmitting
 * the list of proxies between UA1 and REGISTRAR. 
 * This mechanism is in many ways similar to the
 * operation of Record-Route in dialog-initiating requests.
 * <a href= "http://www.faqs.org/rfcs/rfc3327.html"> See RFC 3327 for details </a>
 *
 * @author Oracle Inc., NIST
 * @version 2.0
 * @since 2.0
 *
 */



public interface PathHeader extends HeaderAddress, Parameters, Header {

    /**
     * Name of PathHeader
     */
    public final static String NAME = "Path";

}
