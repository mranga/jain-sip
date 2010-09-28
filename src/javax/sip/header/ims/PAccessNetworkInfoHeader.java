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


/**
 * <p>P-Access-Network-Info P-Header. </p>
 * <p>This header carries information relating to the access network between
 * the UAC and its serving proxy in the home network.</p>
 *
 * See <a href="http://tools.ietf.org/html/rfc3455">RFC 3455 </a>
 * <p>IETF RFC3455 + 3GPP TS 24.229-720 (2005-12)</p>
 * <p>syntax: </p>
 * <pre>
 * P-Access-Network-Info  = "P-Access-Network-Info": access-type *(; access-info)
 *
 * access-type    = "IEEE-802.11a" / "IEEE-802.11b" / "3GPP-GERAN" / "3GPP-UTRAN-FDD" /
 *                   "3GPP-UTRAN-TDD" / "ADSL" / "ADSL2" / "ADSL2+" / "RADSL" / "SDSL" /
 *                   "HDSL" / "HDSL2" / "G.SHDSL" / "VDSL" / "IDSL" / "3GPP2-1X" /
 *                   "3GPP2-1XHRPD" /token
 *
 * access-info            = cgi-3gpp / utran-cell-id-3gpp / dsl-location /
 *                          ci-3gpp2 / extension-access-info
 * cgi-3gpp               = "cgi-3gpp" EQUAL (token / quoted-string)
 * utran-cell-id-3gpp     = "utran-cell-id-3gpp" EQUAL (token / quoted-string)
 * dsl-location           = "dsl-location" EQUAL (token / quoted-string)
 * ci-3gpp2               = "ci-3gpp2" EQUAL (token / quoted-string)
 * extension-access-info  = gen-value
 * gen-value              = token / host / quoted-string
 * </pre>
 *
 * @author Oracle Inc., NIST
 * @version 2.0
 * @since 2.0
 */


import java.text.ParseException;

import javax.sip.header.Header;
import javax.sip.header.Parameters;

public interface PAccessNetworkInfoHeader extends Parameters, Header
{

    public final static String NAME = "P-Access-Network-Info";

    // access type
    public static final String IEEE_802_11 = "IEEE-802.11";
    public static final String IEEE_802_11A = "IEEE-802.11a";
    public static final String IEEE_802_11B = "IEEE-802.11b";
    public static final String IEEE_802_11G = "IEEE-802.11g";
    public static final String GGGPP_GERAN = "3GPP-GERAN";
    public static final String GGGPP_UTRAN_FDD = "3GPP-UTRAN-FDD";
    public static final String GGGPP_UTRAN_TDD = "3GPP-UTRAN-TDD";
    public static final String GGGPP_CDMA2000 = "3GPP-CDMA2000";
    public static final String ADSL = "ADSL";
    public static final String ADSL2 = "ADSL2";
    public static final String ADSL2p = "ADSL2+";
    public static final String RADSL = "RADSL";
    public static final String SDSL = "SDSL";
    public static final String HDSL = "HDSL";
    public static final String HDSL2 = "HDSL2";
    public static final String GSHDSL = "G.SHDSL";
    public static final String VDSL = "VDSL";
    public static final String IDSL = "IDSL";
    public static final String GGGPP2_1X = "3GPP2-1X";
    public static final String GGGPP2_1XHRPD = "3GPP2-1XHRPD";



    /**
     * Set the access type.
     * 
     * @param accessTypeVal
     * 
     * @throws ParseException if invalid string
     */
    public void setAccessType(String accessTypeVal) throws ParseException;
    
    /**
     * Get the access type.
     *
     * @return the accessType
     */
    public String getAccessType();


    /**
     * Set the CGI value.
     * 
     * @param cgi
     * 
     * @throws ParseException - if invalid argument is presented.
     */
    public void setCGI3GPP(String cgi) throws ParseException;
    
    /**
     * Get the CGI value.
     * 
     * @return get the cgi value
     */
    public String getCGI3GPP();


    /**
     * Set the Cell ID.
     * 
     * @param utranCellID - the cell ID.
     * 
     * @throws ParseException
     */
    public void setUtranCellID3GPP(String utranCellID) throws ParseException;
    
    /**
     * Get the cell ID.
     * 
     * @return the cell ID
     */
    public String getUtranCellID3GPP();


    /**
     * Set the DSL Location.
     * 
     * @param dslLocation - the dsl location to set.
     * 
     * @throws ParseException if invalid argument is presented.
     */
    public void setDSLLocation(String dslLocation) throws ParseException;
    
    /**
     * Get the DSL Location.
     * 
     * @return DSL location
     */
    public String getDSLLocation();


    public void setCI3GPP2(String ci2Gpp2) throws ParseException;
    
    /**
     * Get CI value.
     * 
     * @return the CI value.
     */
    public String getCI3GPP2();


    /**
     * Set Extension Access Info.
     * 
     * @param extendAccessInfo
     * 
     * @throws ParseException if invalid String presented.
     */
    public void setExtensionAccessInfo(String extendAccessInfo) throws ParseException;
    
    /**
     * Get the Extension Access Info.
     * 
     * @return the extension Access Info.
     */
    public String getExtensionAccessInfo();




}
