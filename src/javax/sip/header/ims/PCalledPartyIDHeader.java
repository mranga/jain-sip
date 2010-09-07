
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
import javax.sip.header.Parameters;


/**
 * P-Called-Party-ID header - Private Header: 
 * <a href="http://tools.ietf.org/html/rfc3455"> See RFC 3455 </a>.
 * <p>A proxy server inserts a P-Called-Party-ID header, typically in an INVITE request,
 * en-route to its destination. The header is populated with the Request-URI received
 * by the proxy in the request. </p>
 * <p>Both the business SIP URI and the personal SIP URI are registered in the SIP registrar,
 * so both URIs can receive invitations to new sessions. When the user receives an invitation
 * to join a session, he/she should be aware of which of the several registered SIP URIs this
 * session was sent to. </p>
 *
 * <pre>
 * P-Called-Party-ID    = "P-Called-Party-ID" HCOLON
 *                        called-pty-id-spec
 * called-pty-id-spec   = name-addr *(SEMI cpid-param)
 * cpid-param           = generic-param
 * </pre>
 *
 * @author Oracle Inc., NIST
 * @version 2.0
 * @since 2.0
 *
 */


public interface PCalledPartyIDHeader extends HeaderAddress, Parameters, Header {

    /**
     * Name of CalledPartyIDHeader
     */
    public final static String NAME = "P-Called-Party-ID";

}
