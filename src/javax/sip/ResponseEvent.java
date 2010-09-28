/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Unpublished - rights reserved under the Copyright Laws of the United States.
 * Copyright  2003 Sun Microsystems, Inc. All rights reserved.
 * Copyright  2005 BEA Inc. All rights reserved.
 * Copyright  2010 Oracle  Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Module Name   : JSIP Specification
 * File Name     : ResponseEvent.java
 * Author        : Phelim O'Doherty
 *
 *  HISTORY
 *  Version   Date      Author              Comments
 *  1.1     08/10/2002  Phelim O'Doherty    Initial version
 *  1.2     12/15/2004  M. Ranganathan      Added getDialog method
 *  2.0     10/24/2010  Jean Deruelle       2.0 methods.
 *  2.0     10/24/2010  M. Ranganathan      2.0 methods.
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package javax.sip;


import java.util.*;
import javax.sip.message.Response;

/**
 * This class represents a Response event that is passed from a SipProvider to
 * its SipListener. This specification handles the passing of Response messages
 * to the application with the event model. An application (SipListener)
 * registers with the SIP protocol stack (SipProvider) and listens for Response
 * events from the SipProvider.
 * <p>
 * This specification defines a single Response event object to handle all
 * Response messages. The Response event encapsulates the Response message
 * that can be retrieved from {@link javax.sip.ResponseEvent#getResponse()}.
 * Therefore the event type of a Response event can be determined as follows:
 * <p>
 * <i>eventType == ResponseEvent.getResponse().getStatusCode();</i>
 * <p>
 * A Response event also encapsulates the client transaction upon which the
 * Response is correlated, i.e. the client transaction of the Request
 * message upon which this is a Response.
 * <p>
 * ResponseEvent contains the following elements:
 * <ul>
 * <li>source - the source of the event i.e. the SipProvider sending the
 * ResponseEvent.
 * <li>clientTransaction - the client transaction this ResponseEvent is
 * associated with.
 * <li>Response - the Response message received on the SipProvider
 * that needs passed to the application encapsulated in a ResponseEvent.
 * </ul>
 *
 * @author Oracle Inc., NIST
 * @since 1.1
 * @version 2.0
 */
public class ResponseEvent extends EventObject {

    private boolean isForked;

    /**
    * Constructs a ResponseEvent encapsulating the Response that has been received
    * by the underlying SipProvider. This ResponseEvent once created is passed to
    * {@link SipListener#processResponse(ResponseEvent)} method of the SipListener
    * for application processing.
    *
    * @param source - the source of ResponseEvent i.e. the SipProvider
    * @param clientTransaction - client transaction upon which
    * this Response was sent
    * @param response - the Response message received by the SipProvider
    */
    public ResponseEvent(Object source, ClientTransaction clientTransaction, Dialog dialog,  Response response) {
        super(source);
        m_response = response;
        m_transaction = clientTransaction;
        m_dialog = dialog;
        m_originalTransaction = clientTransaction;
    }

    /**
    * Gets the client transaction associated with this ResponseEvent
    *
    * @return client transaction associated with this ResponseEvent
    */
    public ClientTransaction getClientTransaction(){
        return m_transaction;
    }

    /**
    * Gets the Response message encapsulated in this ResponseEvent.
    *
    * @return the response associated with this ResponseEvent.
    */
    public Response getResponse() {
        return m_response;
    }

    /**
     * Gets the Dialog associated with the event or null if no dialog exists.
     * This method separates transaction support from dialog support. This
     * enables application developers to access the dialog associated to this
     * event without having to query the transaction associated to the event.
     * For example the transaction associated with the event may return 'null'
     * because the final response for the transaction has already been received
     * and the stack has no more record of the transaction. This situation can
     * occur when a UAC sends requests out through a forking proxy. Responses
     * that all refer to the same transaction may be sent by the targets of the
     * fork but each response may be stamped with a different To tag, thus
     * referring to different Dialogs on the UAC. The first final response
     * terminates the transaction but the UAC may want to create a Dialog on
     * a subsequent response.
     *
     * @return the dialog associated with the response event or null if there is no dialog.
     * 
     * @since v1.2
     */
    public Dialog getDialog() {
        return m_dialog;
    }

    /**
    * Set true if this is a forked response. This field is only 
    * to be set by the implementation (not the application).
    * 
    * @param forked - a boolean that is set in the response event indicating
    * that the response is a forked response.
    *
    * @since 2.0
    */
    public void setForkedResponse(boolean forked) {
        this.isForked= forked;
    }

    /**
     * Return true if this is a forked response. This query function
     * is used by applications to test if a received response
     * is a forked response.
     * 
     * @since 2.0
     * 
     * @return true if the response event is for a forked response.
     */
    public boolean isForkedResponse() {
        return  isForked;
    }

    /**
     * Set the original transaction for a forked response. This is set by the stack
     * implementation and can be retrieved by the application later.
     * 
     * @since 2.0
     * 
     * @param originalTransaction - the original transaction for which this response event.
     *  is a fork.
     */
    public void setOriginalTransaction(ClientTransaction originalTransaction) {
        m_originalTransaction = originalTransaction;
    }

    /**
     * Get the original transaction for which this is a forked response.
     * Note that this transaction can be in a TERMINATED state.
     * The Original transaction contains the context corresponding
     * to the forked response. This is useful for applications during
     * response processing. If a forked response is recieved by the
     * stack after the MAX_FORK_TIME setting for the SIP Stack,
     * this field can be set to null when the response is delivered
     * to the application.
     *
     * @since 2.0
     * 
     * @return the original clientTx for which this is a forked response.
     */
    public ClientTransaction getOriginalTransaction() {
        return this.m_originalTransaction;
    }
    // internal variables
    private Response m_response;
    private ClientTransaction m_transaction;
    private Dialog m_dialog;
    private ClientTransaction m_originalTransaction;
    
}

