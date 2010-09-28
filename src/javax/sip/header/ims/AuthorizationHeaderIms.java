/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2003 Sun Microsystems, Inc. All rights reserved.
 * Copyright  2005 BEA Systems, Inc. All rights reserved.
 * Copyright  2010 Oracle inc., Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * Author         ALEXANDRE MIGUEL SILVA SANTOS
 *
 *  HISTORY
 *  Version   Date         Author              Comments
 *  2.0       06/09/2010   ALEXANDRE MIGUEL SILVA SANTOS
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*******************************************
 * PRODUCT OF PT INOVACAO - EST DEPARTMENT *
 *******************************************/

package javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.AuthorizationHeader;


/**
 *
 * Extension to Authorization header (3GPP TS 24299-5d0).
 * This extension defines a new auth-param for the Authorization header used
 * in REGISTER requests.
 * For more information, see RFC 2617 [21] subclause 3.2.2.
 * 
 * 
 * @author Oracle Inc., NIST
 * @since 2.0
 * @version 2.0
 */

public interface AuthorizationHeaderIms extends AuthorizationHeader
{

    public static final String YES  = "yes";
    public static final String NO   = "no";



    /**
     * set the IntegrityProtected value.
     * 
     * @param integrityProtected
     * @throws ParseException
     */
    public void setIntegrityProtected(String integrityProtected) throws InvalidArgumentException, ParseException;


   /**
   * get the IntegrityProtected value.
   *
   * @return the integrityProtected value.
   */

    public String getIntegrityProtected();

}
