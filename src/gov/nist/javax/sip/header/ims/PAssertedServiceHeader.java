/*
* Conditions Of Use
*
* This software was developed by employees of the National Institute of
* Standards and Technology (NIST), an agency of the Federal Government.
* Pursuant to title 15 Untied States Code Section 105, works of NIST
* employees are not subject to copyright protection in the United States
* and are considered to be in the public domain.  As a result, a formal
* license is not needed to use the software.
*
* This software is provided by NIST as a service and is expressly
* provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
* OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
* AND DATA ACCURACY.  NIST does not warrant or make any representations
* regarding the use of the software or the results thereof, including but
* not limited to the correctness, accuracy, reliability or usefulness of
* the software.
*
* Permission to use this software is contingent upon your acceptance
* of the terms of this agreement
*
* .
*
*/
package gov.nist.javax.sip.header.ims;

import javax.sip.header.Header;

/**
 *

 * The ABNF for this header is all follows:
 *
 *  PAssertedService = "P-Asserted-Service"
 *                       HCOLON PAssertedService-value
 *
 *  PAssertedService-value = Service-ID *(COMMA Service-ID)
 *
 *  where,
 *
 *     Service-ID      = "urn:urn-7:" urn-service-id
 *     urn-service-id  = top-level *("." sub-service-id)
 *     top-level       = let-dig [ *26let-dig ]
 *     sub-service-id  = let-dig [ *let-dig ]
 *     let-dig         = ALPHA / DIGIT / "-"
 *
 * Egs: P-Asserted-Service: urn:urn-7:3gpp-service.exampletelephony.version1
 *      P-Asserted-Service: urn:urn-7:3gpp-application.exampletelephony.version1
 *      
 * @author aayush.bhatnagar
 * Rancore Technologies Pvt Ltd, Mumbai India.
 *
 *
 */
public interface PAssertedServiceHeader extends Header{

    public static final String NAME = "P-Asserted-Service";
    
    /**
     * set the subservice identifiers.
     * 
     * @param subservices
     */

    public void setSubserviceIdentifiers(String subservices);
    
    /**
     * get the subservice identifiers.
     * 
     * @return
     */

    public String getSubserviceIdentifiers();

    /**
     * Set the application identifiers.
     * 
     * @param appids
     */
    public void setApplicationIdentifiers(String appids);
    
    /**
     * Get the application identifiers.
     * 
     * @return
     */

    public String getApplicationIdentifiers();
}
