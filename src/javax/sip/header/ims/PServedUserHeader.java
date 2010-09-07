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
 * P-Served-User 3-GPP Header definition See <a href="http://www.faqs.org/rfcs/rfc5502.html"> RFC 5502 </a>.
 *
 * The ABNF of the P-Served-User Header is as follows:
 *
 * P-Served-User              = "P-Served-User" HCOLON PServedUser-value
 *                              *(SEMI served-user-param)
 * served-user-param          = sessioncase-param
 *                              / registration-state-param
 *                              / generic-param
 * PServedUser-value          = name-addr / addr-spec
 * sessioncase-param          = "sescase" EQUAL "orig" / "term"
 * registration-state-param   = "regstate" EQUAL "unreg" / "reg"
 *
 * Eg: P-Served-User: <sip:foo@bar.com>; sescase=orig; regstate=reg
 * 
 * @author  Oracle Inc, NIST
 *
 * @version 2.0
 *
 */
public interface PServedUserHeader {

    public static final String NAME = "P-Served-User";

    public void setSessionCase(String sessionCase);

    public String getSessionCase();

    public void setRegistrationState(String registrationState);

    public String getRegistrationState();


}
