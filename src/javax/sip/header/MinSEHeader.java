/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2010 Oracle inc., Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : MinSEHeader.java
 * Author        : M. Ranganathan
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0     09/05/2010  M. Ranganathan   Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip.header;

/**
 * Session timer Min-SE header as defined in
 *
 * <a href="http://www.faqs.org/rfcs/rfc4028.html">RFC 4028</a>
 *
 * Min-SE conveys the minimum allowed value for the session expiration.
 *
 *@version 2.0
 *@since 2.0
 * 
 */


public interface MinSEHeader extends  Parameters, Header {

    public final static String NAME = "Min-SE";

}

