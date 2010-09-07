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

import javax.sip.InvalidArgumentException;
import javax.sip.header.Header;
import javax.sip.header.Parameters;


/**
 * "Security Mechanism Agreemet for SIP Sessions"
 *  - sec-agree: RFC 3329 + 3GPP TS33.203 (Annex H).
 *
 * <p>Headers: Security-Server + Security-Client + Security-Verify</p>
 *
 * @author Miguel Freitas (IT) PT-Inovacao
 */


public interface SecurityAgreeHeader extends Parameters, Header
{

    /**
     * Set security mechanism.
     * <p>eg: Security-Client: ipsec-3gpp</p>
     * @param secMech - security mechanism name
     */
    public void setSecurityMechanism(String secMech) throws ParseException;

    /**
     * Set Encryption Algorithm (ealg parameter)
     * @param ealg - encryption algorithm value
     * @throws ParseException
     */
    public void setEncryptionAlgorithm(String ealg) throws ParseException;

    /**
     * Set Algorithm (alg parameter)
     * @param alg - algorithm value
     * @throws ParseException
     */
    public void setAlgorithm(String alg) throws ParseException;

    /**
     * Set Protocol (prot paramater)
     * @param prot - protocol value
     * @throws ParseException
     */
    public void setProtocol(String prot) throws ParseException;

    /**
     * Set Mode (mod parameter)
     * @param mod - mode value
     * @throws ParseException
     */
    public void setMode(String mod) throws ParseException;

    /**
     * Set Client SPI (spi-c parameter)
     * @param spic - spi-c value
     * @throws InvalidArgumentException
     */
    public void setSPIClient(int spic) throws InvalidArgumentException;

    /**
     * Set Server SPI (spi-s parameter)
     * @param spis - spi-s value
     * @throws InvalidArgumentException - when value is not valid
     */
    public void setSPIServer(int spis) throws InvalidArgumentException;

    /**
     * Set Client Port (port-c parameter)
     * @param portC - port-c value
     * @throws InvalidArgumentException - when value is not valid
     */
    public void setPortClient(int portC) throws InvalidArgumentException;


    /**
     * Set Server Port (port-s parameter)
     * @param portS - port-s value
     * @throws InvalidArgumentException - when value is not valid
     */
    public void setPortServer(int portS) throws InvalidArgumentException;

    /**
     * Set Preference
     * @param q - q parameter value
     * @throws InvalidArgumentException - when value is not valid
     */
    public void setPreference(float q) throws InvalidArgumentException;



    /**
     * Get Security Mechanism
     * @return security mechanims value
     */
    public String getSecurityMechanism();

    /**
     * Get Encryption Algorithm
     * @return ealg parameter value
     */
    public String getEncryptionAlgorithm();

    /**
     * Get Algorithm
     * @return alg parameter value
     */
    public String getAlgorithm();

    /**
     * Get Protocol
     * @return prot parameter value
     */
    public String getProtocol();

    /**
     * Get Mode
     * @return mod parameter value
     */
    public String getMode();

    /**
     * Get Client SPI
     * @return spi-c parameter value
     */
    public int getSPIClient();

    /**
     * Get Server SPI
     * @return spi-s parameter value
     */
    public int getSPIServer();

    /**
     * Get Client Port
     * @return port-c parameter value
     */
    public int getPortClient();

    /**
     * Get Server Port
     * @return port-s parameter value
     */
    public int getPortServer();

    /**
     * Get Preference
     * @return q parameter value
     */
    public float getPreference();

}
