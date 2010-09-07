package javax.sip.header;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2010 Oracle Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties. 
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : MultipartMimeContent.java
 * Author        : M. Ranganathan
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  2.0     09/05/2010  M. Ranganathan   Initial version
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

/**
 * Represents a MultiPart MIME content.
 * 
 * @author Oracle Inc., NIST
 */
import java.util.Iterator;
import java.util.List;

import javax.sip.message.Content;


public interface MultipartMimeContent {

    public abstract boolean add(Content content);

    /**
     * Return the Content type header to assign to the outgoing sip meassage.
     * 
     * @return
     */
    public abstract ContentTypeHeader getContentTypeHeader();

    public abstract String toString();

    /**
     * Set the content by its type.
     * 
     * @param content
     */
    public abstract void addContent( Content content);
    
    /**
     * Retrieve the list of Content that is part of this MultitypeMime content.
     * 
     * @return - the content iterator. Returns an empty iterator if no content list present.
     */
    public Iterator<Content> getContents();
    
    /**
     * Get the number of Content parts.
     * 
     * @return - the content parts.
     */
    public int getContentCount();

}
