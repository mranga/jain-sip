package javax.sip.message;

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
 * @version 2.0
 * @since 2.0
 */
import java.util.Iterator;

import javax.sip.header.ContentTypeHeader;


public interface MultipartMimeContent {

    public  boolean add(Content content);

    /**
     * Return the Content type header to assign to the outgoing sip meassage.
     * 
     * @return - the content type header for this MltipartMime content.
     */
    public ContentTypeHeader getContentTypeHeader();

    /**
    * Convert to String.
    *
    * @return the string representation of this multipart mime body
    */
    public  String toString();

    /**
     * Set the content by its type.
     * 
     * @param content - content fragment.
     */
    public  void addContent( Content content);
    
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
