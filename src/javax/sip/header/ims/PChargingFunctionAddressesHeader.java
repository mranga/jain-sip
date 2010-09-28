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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.sip.header.Header;
import javax.sip.header.Parameters;


/**
 * P-Charging-Function-Addresses header.
 *
 * See <a href="http://tools.ietf.org/html/rfc3455">RFC 3455 </a>
 * There is a need to inform each SIP proxy involved in a transaction about the common
 * charging functional entities to receive the generated charging records or charging events.
 * <ul>
 * <li>
 *   - CCF is used for off-line charging (e.g., for postpaid account charging).
 * <li>
 *   - ECF is used for on-line charging (e.g., for pre-paid account charging).
 * </ul>
 * Only one instance of the header MUST be present in a particular request or response.
 *
 * <pre>
 * P-Charging-Addr = "P-Charging-Function-Addresses" HCOLON
 *          charge-addr-params
 *          *(SEMI charge-addr-params)
 * charge-addr-params   = ccf / ecf / generic-param
 * ccf              = "ccf" EQUAL gen-value
 * ecf              = "ecf" EQUAL gen-value
 *
 * gen-value    = token / host / quoted-string
 *
 * host             =  hostname / IPv4address / IPv6reference
 * hostname         =  *( domainlabel "." ) toplabel [ "." ]
 * domainlabel      =  alphanum / alphanum *( alphanum / "-" ) alphanum
 * toplabel         =  ALPHA / ALPHA *( alphanum / "-" ) alphanum
 *
 *
 * example:
 *  P-Charging-Function-Addresses: ccf=192.1.1.1; ccf=192.1.1.2;
 *  ecf=192.1.1.3; ecf=192.1.1.4
 * </pre>
 *
 * @author Oracle Inc., NIST
 * @since 2.0
 */



public interface PChargingFunctionAddressesHeader extends Parameters, Header {

    /**
     * Name of PChargingFunctionAddressesHeader
     */
    public final static String NAME = "P-Charging-Function-Addresses";


    /**
     * <p>Set the Charging Collection Function (CCF) Address</p>
     * @param ccfAddress - the address to set in the CCF parameter
     * @throws ParseException
     */
    public void setChargingCollectionFunctionAddress(String ccfAddress) throws ParseException;

    /**
     * <p>Add another Charging Collection Function (CCF) Address to this header</p>
     * @param ccfAddress - the address to set in the CCF parameter
     * @throws ParseException
     */
    public void addChargingCollectionFunctionAddress(String ccfAddress) throws ParseException;

    /**
     * <p>Remove a Charging Collection Function (CCF) Address set in this header</p>
     * @param ccfAddress - the address in the CCF parameter to remove
     * @throws ParseException if the address was not removed
     */
    public void removeChargingCollectionFunctionAddress(String ccfAddress) throws ParseException;

    /**
     * <p>Get all the Charging Collection Function (CCF) Addresses set in this header</p>
     * @return ListIterator that constains all CCF addresses of this header
     */
    public ListIterator getChargingCollectionFunctionAddresses();

    /**
     * <p>Set the Event Charging Function (ECF) Address</p>
     * @param ecfAddress - the address to set in the ECF parameter
     * @throws ParseException
     */
    public void setEventChargingFunctionAddress(String ecfAddress)throws ParseException;

    /**
     * <p>Add another Event Charging Function (ECF) Address to this header</p>
     * @param ecfAddress - the address to set in the ECF parameter
     * @throws ParseException
     */
    public void addEventChargingFunctionAddress(String ecfAddress) throws ParseException;

    /**
     * <p>Remove a Event Charging Function (ECF) Address set in this header</p>
     * @param ecfAddress - the address in the ECF parameter to remove
     * @throws ParseException if the address was not removed
     */
    public void removeEventChargingFunctionAddress(String ecfAddress) throws ParseException;

    /**
     * <p>Get all the Event Charging Function (ECF) Addresses set in this header</p>
     * @return ListIterator that constains all CCF addresses of this header
     */
    public ListIterator getEventChargingFunctionAddresses();

}
