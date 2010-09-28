/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2010 Oracle  Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * Author        : Emil Ivov
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0      09/24/2010  Emil Ivov   Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip;

/**
* The class is used whenever user credentials for a particular realm (site
* server or service) are necessary
* @version 2.0
* @since 2.0
* @author Oracle, NIST
*/

public interface UserCredentials
{
   

   /**
    * Returns the name of the user that these credentials relate to.
    * @return the user name.
    */
   public String getUserName();
   

   /**
    * Returns a password associated with this set of credentials.
    *
    * @return a password associated with this set of credentials.
    */
   public String getPassword();
   
   
   /**
    * Returns the SIP Domain for this username password combination.
    * 
    * @return the sip domain
    */
   public String getSipDomain();
   
   
  
}


