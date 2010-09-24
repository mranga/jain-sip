/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2005 Oracle inc., Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : AccountManager.java
 * Author        : M. Ranganathan
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip;

/**
 * Account Manager helper class for the AuthenticationHelper.
 * 
 * @author Oracle Inc., NIST
 * @since 2.0
 * @version 2.0
 *
 */

public interface AccountManager {

    /**
     * Returns the user credentials for a given SIP Domain.
     * You can implement any desired method (such as popping up a dialog for example )
     * to retrieve the credentials.
     *
     * @param challengedTransaction - the transaction that is being challenged.
     * @param realm - the realm that is being challenged for which a credential should be
     *         returned.
     * @return -- the user credentials associated with the domain.
     */

    UserCredentials getCredentials(ClientTransaction challengedTransaction, String realm);

}
