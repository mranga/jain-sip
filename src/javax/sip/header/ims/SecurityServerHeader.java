package javax.sip.header.ims;
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

import java.text.ParseException;

import javax.sip.InvalidArgumentException;


/**
 * Security-Server header
 * See <a href="http://www.faqs.org/rfcs/rfc3329.html"> RFC 3329 </a> + 3GPP TS33.203 (Annex H).
 * <p></p>
 *
 * @author Miguel Freitas (IT) PT-Inovacao
 * @version 2.0
 * @since 2.0
 */


public interface SecurityServerHeader extends SecurityAgreeHeader
{
    /**
     * Name of SecurityServerHeader
     */
    public final static String NAME = "Security-Server";



}
