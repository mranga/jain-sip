package javax.sip.header.ims;
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
import javax.sip.header.Header;
import javax.sip.header.Parameters;



/**
 * P-Visited-Network-ID SIP Private Header: 
 * See <a href="http://tools.ietf.org/html/rfc3455">RFC 3455.</a>
 *
 * <ul>
 * <li>
 * . One of the conditions for a home network to accept the registration of a UA roaming to a
 * particular visited network, is the existence of a roaming agreement between the home and
 * the visited network. There is a need to indicate to the home network which one is the visited
 * network that is providing services to the roaming UA.
 * <li>
 * . user agents always register to the home network. The REGISTER request is proxied by
 * one or more proxies located in the visited network towards the home network
 * <li>
 * . the visited network includes an identification that is known at the home network
 * <li>
 * . This identification should be globally unique, and takes the form of a quoted text string or a token
 * <li>
 * . In case a REGISTER or other request is traversing different administrative domains
 * (e.g., different visited networks), a SIP proxy MAY insert a NEW P-Visited-Network-ID header
 * if the request does not contain a P-Visited-Network-ID header with the same network
 * identifier as its own network identifier
 * </ul>
 *
 * <p>Sintax: </p>
 *
 * <pre>
 * P-Visited-Network-ID = "P-Visited-Network-ID" HCOLON
 *                         vnetwork-spec
 *                         *(COMMA vnetwork-spec)
 * vnetwork-spec        = (token / quoted-string)
 *                         *(SEMI vnetwork-param)
 * vnetwork-param       = generic-param
 *
 *
 * eg: P-Visited-Network-ID: other.net, "Visited network number 1"
 * </pre>
 *
 * @author Oracle Inc., NIST
 * @version 2.0
 */




public interface PVisitedNetworkIDHeader extends Parameters, Header {

    /**
     * Name of VisitedNetworkIDHeader
     */
    public final static String NAME = "P-Visited-Network-ID";


    /**
     * Set the visited network ID as a string. The value will be quoted in the header.
     * @param networkID - string value
     */
    public void setVisitedNetworkID(String networkID);

    
    /**
     * Get the visited network ID value of this header
     */
    public String getVisitedNetworkID();

}
