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

import javax.sip.header.Header;
import javax.sip.header.HeaderAddress;


/**
 * P-Preferred-Identity header -
 * SIP Private Header: RFC 3325
 *
 * <ul>
 * <li>
 * . is used from a user agent to a trusted proxy to carry the identity the
 * user sending the SIP message wishes to be used for the P-Asserted-Header
 * field value that the trusted element will insert.
 * <li>
 * . If there are two values, one value MUST be a sip or sips URI and the other
 * MUST be a tel URI.
 * </ul>
 *
 * <p>Sintax: </p>
 * <pre>
 * PPreferredID = "P-Preferred-Identity" HCOLON PPreferredID-value
 *                 *(COMMA PPreferredID-value)
 * PPreferredID-value = name-addr / addr-spec
 * </pre>
 *
 * @author ALEXANDRE MIGUEL SILVA SANTOS - Nú 10045401
 */


public interface PPreferredIdentityHeader extends HeaderAddress, Header {

     /**
     * Name of PreferredIdentityHeader
     */
    public final static String NAME = "P-Preferred-Identity";

}
