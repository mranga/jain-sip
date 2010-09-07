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
 * Author        :  Alexandre Miguel Silva Santos
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
 * P-Asserted-Identity header
 * Private Header: See RFC 3455.
 * Contains a URI (commonly a SIP URI) and an optional display-name
 * enable a network of trusted SIP servers to assert
 * the identity of authenticated users, and the application of existing
 * privacy mechanisms to the identity problem.
 * The use of this extension is only applicable inside an administrative
 * domain with previously agreed-upon policies for generation,
 * transport and usage of such information.
 *
 * @author Oracle Inc., NIST
 * @version 2.0
 * @since 2.0
 *
 *
 */



public interface PAssertedIdentityHeader extends HeaderAddress, Header {

    /**
     * Name of AssertIdentityHeader
     */
    public final static String NAME = "P-Asserted-Identity";

}
