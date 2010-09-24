package javax.sip;
/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2003 Sun Microsystems, Inc. All rights reserved.
 * Copyright  2005 Oracle inc., Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : SecureAccountManager.java
 * Author        : M. Ranganathan
 *
 *  HISTORY
 *  Version   Date          Author              Comments
 *   1.0      Sep 5, 2010   M. Ranganathan      Initial.
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */


/**
 * Interface that implements the hashed password account manager.
 *
 * @version 2.0
 * @since  2.0
 *
 *
 */
public interface SecureAccountManager  {
    /**
     * Return the user Credentials for a given SIP Domain.
     * The application is expected to implement this interface and 
     * register it with the stack. This is an application callback that works
     * along with the AuthenticationHelper to implement SIP Digest authentication.
     * The application can implement any desired method to retrieve the credentials.
     *
     * @param challengedTransaction - the transaction that is being challenged.
     * @param realm - the realm that is being challenged for which a credential should be
     *         returned.
     * @return -- the user credentials associated with the domain.
     * 
     * @see SipStack#getSecureAuthenticationHelper(SecureAccountManager, javax.sip.header.HeaderFactory)
     */

    UserCredentialHash getCredentialHash(ClientTransaction challengedTransaction, String realm);

}
