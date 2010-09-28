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

import javax.sip.header.Header;
import javax.sip.header.Parameters;




/**
 * <p>P-Charging-Vector header SIP Private Header. </p>
 * 
 *See <a href="http://tools.ietf.org/html/rfc3455">RFC 3455 </a>
 * <p> Syntax (RFC 3455): </p>
 * <pre>
 * P-Charging-Vector   = "P-Charging-Vector" HCOLON icid-value (SEMI charge-params)
 * charge-params        = icid-gen-addr / orig-ioi / term-ioi / generic-param
 * icid-value           = "icid-value" EQUAL gen-value
 * icid-gen-addr        = "icid-generated-at" EQUAL host
 * orig-ioi             = "orig-ioi" EQUAL gen-value
 * term-ioi             = "term-ioi" EQUAL gen-value
 * </pre>
 *
 * <p>syntax from RFC3261: </p>
 * <pre>
 * generic-param       = token [ EQUAL gen-value ]
 * gen-value           = token / host / quoted-string
 * host                = hostname / IPv4address / Ipv6reference
 * </pre>
 *
 *
 * <p> syntax as in 3GPP TS 24.229-720 (2005-12) :
 *
 *    The access-network-charging-info parameter is an instance of generic-param
 *    from the current charge-params: </p>
 *
 * <pre>
 * access-network-charging-info   = (gprs-charging-info / i-wlan-charging-info / xdsl-charging-info / generic-param)
 * gprs-charging-info          = ggsn SEMI auth-token [SEMI pdp-info-hierarchy] *(SEMI extension-param)
 * ggsn                        = "ggsn" EQUAL gen-value
 * pdp-info-hierarchy          = "pdp-info" EQUAL LDQUOT pdp-info *(COMMA pdp-info) RDQUOT
 * pdp-info                    = pdp-item SEMI pdp-sig SEMI gcid [SEMI flow-id]
 * pdp-item                    = "pdp-item" EQUAL DIGIT
 * pdp-sig                     = "pdp-sig" EQUAL ("yes" / "no")
 * gcid                        = "gcid" EQUAL 1*HEXDIG
 * auth-token                  = "auth-token" EQUAL 1*HEXDIG
 * flow-id                     = "flow-id" EQUAL "(" "{" 1*DIGIT COMMA 1*DIGIT "}" *(COMMA "{" 1*DIGIT COMMA 1*DIGIT"}")")"
 * extension-param             = token [EQUAL token]
 * i-wlan-charging-info        = "pdg"
 * xdsl-charging-info          = bras SEMI auth-token [SEMI xDSL-bearer-info] *(SEMI extension-param)
 * bras                        = "bras" EQUAL gen-value
 * xDSL-bearer-info            = "dsl-bearer-info" EQUAL LDQUOT dsl-bearer-info *(COMMA dsl-bearer-info) RDQUOT
 * dsl-bearer-info             = dsl-bearer-item SEMI dsl-bearer-sig SEMI dslcid [SEMI flow-id]
 * dsl-bearer-item             = "dsl-bearer-item" EQUAL DIGIT
 * dsl-bearer-sig              = "dsl-bearer-sig"
 * </pre>
 *
 *
 * <p>example:
 * P-Charging-Vector: icid-value=1234bc9876e; icid-generated-at=192.0.6.8; orig-ioi=home1.net </p>
 *
 *
 *
 * @author Oracle Systems Inc., NIST
 */



public interface PChargingVectorHeader extends Header, Parameters {

    /**
     * Name of PChargingVectorHeader
     */
    public final static String NAME = "P-Charging-Vector";


    /**
     * @return -- icid value.
     */
    public String getICID();


    /**
     * @param icid
     * @throws ParseException
     */
    public void setICID(String icid) throws ParseException;

    /**
     * @return -- the ICID generatedAt field.
     */
    public String getICIDGeneratedAt();


    /**
     * @param host -- set the icid host value.
     *
     * @throws ParseException -- if bad host value.
     */
    public void setICIDGeneratedAt(String host) throws ParseException;


    /**
     *
     * @return the originating IOI
     */
    public String getOriginatingIOI();


    /**
     * @param origIOI
     * @throws ParseException
     *
     */
    public void setOriginatingIOI(String origIOI) throws ParseException;


    /**
     * @return -- the terminating IOI field
     */
    public String getTerminatingIOI();


    /**
     * @param termIOI -- the terminating IOI field to set.
     * @throws ParseException
     */
    public void setTerminatingIOI(String termIOI) throws ParseException;


}
