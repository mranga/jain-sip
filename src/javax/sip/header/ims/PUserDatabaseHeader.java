package javax.sip.header.ims;

import javax.sip.header.Header;
import javax.sip.header.Parameters;

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
 * Author        : Aayush Bhatnagar
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0     09/06/2010  aayush.bhatnagar (Rancore Technologies Pvt Ltd, Mumbai India).
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
/*******************************************
 * Contributed by Rancore Technologies     *
 *******************************************/
/**
 * 
 *
 * P-User-Database 3GPP header.See <a href="http://www.rfc-editor.org/rfc/rfc4457.txt">RFC 4457</a> for details.
 * We only have one major value for this header, as per RFC 4457.
 * This value is the Database name. The DB here refers
 * to the IMS HSS. The DB name is encoded as a URI, delimited
 * by the "<" and ">" signs. There may be generic parameters for
 * this header encoded as URI parameters. They also lie between
 * the "<" and ">" delimiters. However, this URI is neither a SIP URI
 * nor a TEL URI. It is a DIAMETER AAA URI.The value of this AAA URI
 * is consumed by the S-CSCF. The S-CSCF can cache the value of the
 * HSS received in this header,thus optimizing the IMS registration
 * process.
 * 
 *@author Oracle Inc., NIST
 *
 */
public interface PUserDatabaseHeader extends Parameters,Header
{
    public final static String NAME = "P-User-Database";

    public String getDatabaseName();

    public void setDatabaseName(String name);


}
