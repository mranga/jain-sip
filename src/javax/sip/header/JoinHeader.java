/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2010 Oracle inc., Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : JoinHeader.java
 * Author        : Jean Deruelle
 *
 *  HISTORY
 *  Version   Date      Author              	   Comments
 *  2.0     09/05/2010  Jean Deruelle (Red Hat)    Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip.header;
import java.text.ParseException;
/**
 * Interface for the Join Header. See 
 * <a href="http://www.ietf.org/rfc/rfc3911.txt"> RFC 3911 </a>
 * for further details.
 * 
 * @author Oracle Inc., NIST
 * 
 * @version 2.0
 * @since 2.0
 *
 */
public interface JoinHeader extends Parameters, Header {
    /**
     * Set the to tag for the Join Header
     * @param tag -- the tag to set.
     * @throws ParseException if there is a problem with the tag.
     * @throws NullPointerException if the parameter is null.
     */
    public void setToTag(String tag) throws ParseException;
    /**
     * Set the from tag for the Join header.
     * @param tag -- the tag to set.
     * @throws ParseException if there is a problem with the tag
     * @throws NullPointerException if the parameter is null
     * 
     */
    public void setFromTag(String tag) throws ParseException;
    /**
     * Get the to To tag.
     * 
     * @return the to tag. Null if not set.
     * 
     */
    public String getToTag();
    /**
     * Get the from tag.
     * 
     * @return the from tag. Null if not set.
     */
    public String getFromTag();
    
    /**
     * Set the call Id.
     * @param callId - the call Id for the header.
     * @throws ParseException - if the character set is unacceptable.
     * @throws NullPointerException if the parameter is null.
     */
    public void setCallId(String callId) throws ParseException;
    
    /**
     * Get the call ID field.
     * 
     * @return the call Id field. Null if not set.
     */
    public String getCallId();
    public final static String NAME = "Join";

}


