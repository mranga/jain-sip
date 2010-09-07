package javax.sip.header;
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
 * Author        : M. Ranganathan
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0     09/06/2010   M. Ranganathan     Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/***************************************************************
 * PRODUCT OF ADVANCED NETWORKING TECHNOLOGIES DIVISION (NIST) *
 ***************************************************************/

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
/**
 * Header interface for the Sesison-Expires SIP header as defined in 
 * <a href="http://www.faqs.org/rfcs/rfc4028.html">RFF 4028</a>.
 *The Session-Expires header field establishes the upper bound for the 
 *session refresh interval; i.e., the time period after processing a 
 *request for which any session-stateful proxy must retain its state 
 *for this session.  Any proxy servicing this request can lower this 
 *value, but it is not allowed to decrease it below the value specified 
 *in the Min-SE header field.  
 *
 * @see javax.sip.header.MinSEHeader
 * @version 2.0
 * @since 2.0
 */

public interface SessionExpiresHeader extends Parameters, Header,
ExtensionHeader{

  
    public static final String REFRESHER_UAS = "uas";
    
    public static final String REFRESHER_UAC = "uac";
    
    /**
     * Get the expires parameter.
     * 
     * @return the expires parameter.
     */
    public int getExpires();

    /**
     * Set the expires parameter.
     * 
     * @param expires - the expires parameter to set.
     * 
     * @throws InvalidArgumentException
     */
    public void setExpires(int expires) throws InvalidArgumentException;

    /**
     * Get the Refresher.
     * 
     * @return the Refresher
     */
    public String getRefresher() ;

    
    /**
     * Set the Refresher parameter.
     * 
     * @param refresher - the Refresher to set. Must be "uas" or "uac". 
     */
    public void setRefresher(String refresher) throws ParseException;
    
    public final static String NAME = "Session-Expires";


}
