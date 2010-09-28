package javax.sip.header.ims;

import javax.sip.header.Header;
import javax.sip.header.HeaderAddress;
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
 * File Name     : ProfileKeyHeader.java
 * Author        : Aayush Bhatnagar
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  1.1     09/06/2010  Aayush Bhatnagar    Initial version
 *                      Rancore Technologies
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

/**
 *
 * For further description of this header see 
 * <a href="http://tools.ietf.org/html/rfc5002">RFC 5002 </a>
 * 
 * The ABNF syntax of this header is as follows:
 * P-Profile-Key            = "P-Profile-Key" HCOLON {name-addr / addr-spec}
 *                          *{ SEMI generic-param }
 *
 * Eg: P-Profile-Key: <sip:chatroom-!.*!@example.com>
 * 
 * @author Oracle inc., NIST
 *
 */
public interface PProfileKeyHeader extends HeaderAddress, Header{

    public final static String NAME = "P-Profile-Key";

}
