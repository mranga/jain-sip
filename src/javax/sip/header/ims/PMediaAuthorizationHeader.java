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

import javax.sip.InvalidArgumentException;
import javax.sip.header.Header;


/**
 * The P-Media-Authorization SIP Private Header - RFC 3313.
 *
 * <p>syntax:</p>
 * <pre>
 * P-Media-Authorization   = "P-Media-Authorization" HCOLON
 *                            P-Media-Authorization-Token
 *                            *(COMMA P-Media-Authorization-Token)
 * P-Media-Authorization-Token = 1*HEXDIG
 * </pre>
 *
 * @author Oracle Inc., NIST
 */

public interface PMediaAuthorizationHeader extends Header
{

    /**
     * Name of PMediaAuthorizationHeader
     */
    public final static String NAME = "P-Media-Authorization";

    /**
     * Set the media authorization token.
     * @param token - media authorization token to set
     * @throws InvalidArgumentException - if token is null or empty
     */
    public void setMediaAuthorizationToken(String token) throws InvalidArgumentException;

    /**
     * Get the media authorization token.
     * @return token
     */
    public String getToken();


}
