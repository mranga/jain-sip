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
/*-----------------------------------------------
 * PRODUCT OF PT INOVACAO- EST DEPARTMENT       *
 *----------------------------------------------*/

package javax.sip.header.ims;

/**
 * <p>P-Associated-URI SIP Private Header. </p>
 * <p>An associated URI is a URI that the service provider
 * has allocated to a user for his own usage (address-of-record). </p>
 *
 * <p>sintax (RFC 3455): </p>
 * <pre>
 * P-Associated-URI  = "P-Associated-URI" HCOLON
 *                    (p-aso-uri-spec) *(COMMA p-aso-uri-spec)
 * p-aso-uri-spec    = name-addr *(SEMI ai-param)
 * ai-param          = generic-param
 * name-addr         =   [display-name] angle-addr
 * angle-addr        =   [CFWS] "<" addr-spec ">" [CFWS] / obs-angle-addr
 * </pre>
 *
 * @author Oracle Systems Inc., NIST
 * @version 2.0
 * @since 2.0
 */

import javax.sip.address.URI;
import javax.sip.header.Header;
import javax.sip.header.HeaderAddress;
import javax.sip.header.Parameters;



public interface PAssociatedURIHeader
    extends HeaderAddress, Parameters, Header
{

    /**
     * Name of PAssociatedURIHeader
     */
    public final static String NAME = "P-Associated-URI";


    /**
     * <p>Set the URI on this address</p>
     * @param associatedURI - GenericURI to be set in the address of this header
     * @throws NullPointerException when supplied URI is null
     */
    public void setAssociatedURI(URI associatedURI) throws NullPointerException;

    /**
     * <p>Get the address's URI</p>
     * @return URI set in the address of this header
     */
    public URI getAssociatedURI();

    //public void setAssociatedURI(AddressImpl associatedURI);
    //public AddressImpl getAssociatedURI();




}
