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

import javax.sip.header.WWWAuthenticateHeader;


/**
 * Extension to WWW-authenticate header (3GPP TS 24229-5d0).
 *
 * <p>Defines a new authentication parameter (auth-param) for the WWW-Authenticate header
 * used in a 401 (Unauthorized) response to the REGISTER request.
 * For more information, see RFC 2617 [21] subclause 3.2.1.</p>
 *
 * <pre>
 *  auth-param = 1#( integrity-key / cipher-key )
 *  integrity-key = "ik" EQUAL ik-value
 *  cipher-key = "ck" EQUAL ck-value
 *  ik-value = LDQUOT *(HEXDIG) RDQUOT
 *  ck-value = LDQUOT *(HEXDIG) RDQUOT
 * </pre>
 *
 * @author Oracle Inc., NIST
 * @since 2.0
 * @version 2.0
 */


public interface WWWAuthenticateHeaderIms extends WWWAuthenticateHeader
{
    public static final String IK = "ik";
    public static final String CK = "ck";


    public void setIK(String ik) throws ParseException;

    public String getIK();

    public void setCK(String ck) throws ParseException;

    public String getCK();

}
