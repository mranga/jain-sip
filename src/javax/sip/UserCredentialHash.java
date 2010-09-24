/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2003 Sun Microsystems, Inc. All rights reserved.
 * Copyright  210 Oracle inc., Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * Author        : M. Ranganathan
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0      09/24/2010 M. Ranganathan    Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip;

/**
 * Interface for those clients that only supply 
 * hash(user:domain:password). This is more secure than simply supplying
 * password because the password cannot be extracted. Implementations
 * tend to prefer to store information in user accounts using such a
 * hash rather than plain text passwords because it offers better security.
 * The SecureAccountManager if implemented, return instances of this class.
 * This class is implemented by the application and is used by the sip stack when 
 * dealing with challenge responses. The JAIN-SIP implementation provides
 * the SecureAuthenticationHelper implementation. The application provides
 * the implementation of SecureAccountManager. 
 * 
 * @since 2.0
 * @version 2.0
 * @see javax.sip.SipStack#getSecureAuthenticationHelper(SecureAccountManager, javax.sip.header.HeaderFactory)
 * @see javax.sip.SecureAccountManager
 * @see javax.sip.SecureAuthenticationHelper
 * 
 */
public interface UserCredentialHash {
    
    /**
     * Get the user name.
     * 
     * @return userName
     */
    public String getUserName();
    
    
    /**
     * Get the SipDomain.
     * 
     * @return the SIP Domain.
     */
    public String getSipDomain();
    
    
    /**
     * Get the MD5(userName:sipdomain:password)
     * 
     * @return the MD5 hash of userName:sipDomain:password.
     */
    public String getHashUserDomainPassword();

}
