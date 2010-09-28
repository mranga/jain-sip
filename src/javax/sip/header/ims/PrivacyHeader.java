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
 * PRODUCT OF PT INOVACAO- EST DEPARTMENT *
 *******************************************/


package javax.sip.header.ims;



import java.text.ParseException;

import javax.sip.header.Header;


/**
 * Privacy Header.
 *See <a href="http://www.faqs.org/rfcs/rfc3325.html"> RFC 3323 </a> for more details.
 *
 *<pre>
 * Privacy-hdr  = "Privacy" HCOLON priv-value *(";" priv-value)
 * priv-value   = "header" / "session" / "user" /
 *                "id" / "none" / "critical" / token
 * example:
 *           Privacy: id
 * </pre>
 *
 * @author Oracle Inc., NIST
 * @since 2.0
 * @version 2.0
 * @see PAssertedIdentityHeader
 * @see PPreferredIdentityHeader
 */


public interface PrivacyHeader extends Header
{

    /**
     * Name of PrivacyHeader
     */
    public final static String NAME = "Privacy";


    /**
     * Set Privacy header value
     * @param  privacy -- privacy type to set.
     */
    public void setPrivacy(String privacy) throws ParseException;

    /**
     * Get Privacy header value
     * @return privacy token name
     */
    public String getPrivacy();


}

