/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2010 Oracle Inc. All rights reserved.
 *
 * U.S. Government Rights - Commercial software. Government users are subject 
 * to the Sun Microsystems, Inc. standard license agreement and applicable 
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. Sun, 
 * Sun Microsystems, the Sun logo, Java, Jini and JAIN are trademarks or 
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other 
 * countries.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JAIN SIP Specification
 * File Name     : ReplacesHeader.java
 * Author        : Peter Musgrave
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  1.1     08/10/2002  Peter Musgrave
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip.header;



import java.text.ParseException;

/**
 * Defines an interface for the SIP Replaces Header as defined in
 * <a href="http://www.ietf.org/rfc/rfc3891.txt">RFC 3891</a>.
 * The  Replaces header is used to logically replace an existing SIP dialog
 * with a new SIP dialog.  This primitive can be used to enable a
 * variety of features, for example: "Attended Transfer" and "Call
 * Pickup".
 *
 * @since 2.0
 * @version 2.0
 * @author Oracle Inc., NIST
 */

public interface ReplacesHeader extends Parameters, Header {
    /**
     *set the to tag of the Replaces header.
     *@param tag - the tag to set.
     *@throws NullPointerException if null tag is set.
     *@throws ParseException if invalid characters are in the tag.
     */
    public void setToTag(String tag) throws ParseException,NullPointerException;

    /**
     *Set the From tag of the Replaces header.
     *@param tag - the tag to set.
     *@throws NullPointerException if null tag is set.
     *@throws ParseException if invalid characters are in the tag.
     */
    public void setFromTag(String tag) throws ParseException,NullPointerException;

    /**
    *Get the previously set to tag or <it>Null</it> if no tag set.
    */
    public String getToTag();

    /**
    *Get the previously set From tag or <it>Null</it> if no tag set.
    */
    public String getFromTag();
    
    /**
     *Set the CallId of the Replaces header.
     *@param callId - the callId to set.
     *@throws NullPointerException if null tag is set.
     *@throws ParseException if invalid characters are in the tag.
     */
    public void setCallId(String callId) throws ParseException,NullPointerException;

    /**
     * get the previously set call Id or Null if nothing has been set.
     */
    public String getCallId();

    /**
     * The header NAME.
     */
    public final static String NAME = "Replaces";

}


