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
/******************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD).      *
 ******************************************************************************/
package gov.nist.javax.sip;

import gov.nist.core.HostPort;
import gov.nist.core.InternalErrorHandler;
import gov.nist.core.LogLevels;
import gov.nist.core.LogWriter;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.ReferTo;
import gov.nist.javax.sip.header.RetryAfter;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.MessageChannel;
import gov.nist.javax.sip.stack.SIPClientTransaction;
import gov.nist.javax.sip.stack.SIPDialog;
import gov.nist.javax.sip.stack.SIPServerTransaction;
import gov.nist.javax.sip.stack.SIPTransaction;
import gov.nist.javax.sip.stack.SIPTransactionStack;
import gov.nist.javax.sip.stack.ServerRequestInterface;
import gov.nist.javax.sip.stack.ServerResponseInterface;

import java.io.IOException;

import javax.sip.ClientTransaction;
import javax.sip.DialogState;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TransactionState;
import javax.sip.header.EventHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.ServerHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/*
 * Bug fix Contributions by Lamine Brahimi, Andreas Bystrom, Bill Roome, John Martin, Daniel
 * Machin Vasquez-Illa, Antonis Karydas, Joe Provino, Bruce Evangelder, Jeroen van Bemmel, Robert
 * S. Rosen.
 */
/**
 * An adapter class from the JAIN implementation objects to the NIST-SIP stack.
 * The primary purpose of this class is to do early rejection of bad messages
 * and deliver meaningful messages to the application. This class is essentially
 * a Dialog filter. It is a helper for the UAC Core. It checks for and rejects
 * requests and responses which may be filtered out because of sequence number,
 * Dialog not found, etc. Note that this is not part of the JAIN-SIP spec (it
 * does not implement a JAIN-SIP interface). This is part of the glue that ties
 * together the NIST-SIP stack and event model with the JAIN-SIP stack. This is
 * strictly an implementation class.
 * 
 * @version 1.2 $Revision: 1.88 $ $Date: 2010/08/19 21:44:51 $
 * 
 * @author M. Ranganathan
 */
class DialogFilter implements ServerRequestInterface, ServerResponseInterface {

    protected SIPTransaction transactionChannel;

    protected ListeningPointImpl listeningPoint;

    private SIPTransactionStack sipStack;

    public DialogFilter(SIPTransactionStack sipStack) {
        this.sipStack = sipStack;

    }

    /**
     * Send back a Request Pending response.
     * 
     * @param sipRequest
     * @param transaction
     */
    private void sendRequestPendingResponse(SIPRequest sipRequest,
            SIPServerTransaction transaction) {
        if (transaction.getState() != TransactionState.TERMINATED) {
            SIPResponse sipResponse = sipRequest
                    .createResponse(Response.REQUEST_PENDING);
            ServerHeader serverHeader = MessageFactoryImpl
                    .getDefaultServerHeader();
            if (serverHeader != null) {
                sipResponse.setHeader(serverHeader);
            }
            try {
                RetryAfter retryAfter = new RetryAfter();
                retryAfter.setRetryAfter(1);
                sipResponse.setHeader(retryAfter);
                if (sipRequest.getMethod().equals(Request.INVITE)) {
                    sipStack.addTransactionPendingAck(transaction);
                }
                transaction.sendResponse(sipResponse);
                transaction.releaseSem();
            } catch (Exception ex) {
                sipStack.getStackLogger().logError(
                        "Problem sending error response", ex);
                transaction.releaseSem();
                sipStack.removeTransaction(transaction);
            }
        }
    }

    /**
     * Send a BAD REQUEST response.
     * 
     * @param sipRequest
     * @param transaction
     * @param reasonPhrase
     */

    private void sendBadRequestResponse(SIPRequest sipRequest,
            SIPServerTransaction transaction, String reasonPhrase) {
        if (transaction.getState() != TransactionState.TERMINATED) {

            SIPResponse sipResponse = sipRequest
                    .createResponse(Response.BAD_REQUEST);
            if (reasonPhrase != null)
                sipResponse.setReasonPhrase(reasonPhrase);
            ServerHeader serverHeader = MessageFactoryImpl
                    .getDefaultServerHeader();
            if (serverHeader != null) {
                sipResponse.setHeader(serverHeader);
            }
            try {
                if (sipRequest.getMethod().equals(Request.INVITE)) {
                    sipStack.addTransactionPendingAck(transaction);
                }
                transaction.sendResponse(sipResponse);
                transaction.releaseSem();
            } catch (Exception ex) {
                sipStack.getStackLogger().logError(
                        "Problem sending error response", ex);
                transaction.releaseSem();
                sipStack.removeTransaction(transaction);

            }
        }
    }

    /**
     * Send a CALL OR TRANSACTION DOES NOT EXIST response.
     * 
     * @param sipRequest
     * @param transaction
     */

    private void sendCallOrTransactionDoesNotExistResponse(
            SIPRequest sipRequest, SIPServerTransaction transaction) {

        if (transaction.getState() != TransactionState.TERMINATED) {
            SIPResponse sipResponse = sipRequest
                    .createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);

            ServerHeader serverHeader = MessageFactoryImpl
                    .getDefaultServerHeader();
            if (serverHeader != null) {
                sipResponse.setHeader(serverHeader);
            }
            try {
                if (sipRequest.getMethod().equals(Request.INVITE)) {
                    sipStack.addTransactionPendingAck(transaction);
                }
                transaction.sendResponse(sipResponse);
                transaction.releaseSem();
            } catch (Exception ex) {
                sipStack.getStackLogger().logError(
                        "Problem sending error response", ex);
                transaction.releaseSem();
                sipStack.removeTransaction(transaction);

            }

        }
    }

    /**
     * Send back a LOOP Detected Response.
     * 
     * @param sipRequest
     * @param transaction
     * 
     */
    private void sendLoopDetectedResponse(SIPRequest sipRequest,
            SIPServerTransaction transaction) {
        SIPResponse sipResponse = sipRequest
                .createResponse(Response.LOOP_DETECTED);
        if (transaction.getState() != TransactionState.TERMINATED) {

            ServerHeader serverHeader = MessageFactoryImpl
                    .getDefaultServerHeader();
            if (serverHeader != null) {
                sipResponse.setHeader(serverHeader);
            }
            try {
                sipStack.addTransactionPendingAck(transaction);
                transaction.sendResponse(sipResponse);
                transaction.releaseSem();
            } catch (Exception ex) {
                sipStack.getStackLogger().logError(
                        "Problem sending error response", ex);
                transaction.releaseSem();
                sipStack.removeTransaction(transaction);

            }
        }

    }

    /**
     * Send back an error Response.
     * 
     * @param sipRequest
     * @param transaction
     */

    private void sendServerInternalErrorResponse(SIPRequest sipRequest,
            SIPServerTransaction transaction) {
        if (transaction.getState() != TransactionState.TERMINATED) {
            if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
                sipStack.getStackLogger().logDebug(
                        "Sending 500 response for out of sequence message");
            SIPResponse sipResponse = sipRequest
                    .createResponse(Response.SERVER_INTERNAL_ERROR);
            sipResponse.setReasonPhrase("Request out of order");
            if (MessageFactoryImpl.getDefaultServerHeader() != null) {
                ServerHeader serverHeader = MessageFactoryImpl
                        .getDefaultServerHeader();
                sipResponse.setHeader(serverHeader);
            }

            try {
                RetryAfter retryAfter = new RetryAfter();
                retryAfter.setRetryAfter(10);
                sipResponse.setHeader(retryAfter);
                sipStack.addTransactionPendingAck(transaction);
                transaction.sendResponse(sipResponse);
                transaction.releaseSem();
            } catch (Exception ex) {
                sipStack.getStackLogger().logError("Problem sending response",
                        ex);
                transaction.releaseSem();
                sipStack.removeTransaction(transaction);
            }
        }
    }

    /**
     * Process a request. Check for various conditions in the dialog that can
     * result in the message being dropped. Possibly return errors for these
     * conditions.
     * 
     * @exception SIPServerException
     *                is thrown when there is an error processing the request.
     */
    public void processRequest(SIPRequest sipRequest,
            MessageChannel incomingMessageChannel) {
        // Generate the wrapper JAIN-SIP object.
        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)
                && listeningPoint != null)
            sipStack.getStackLogger().logDebug(
                    "PROCESSING INCOMING REQUEST " + sipRequest
                            + " transactionChannel = " + transactionChannel
                            + " listening point = "
                            + listeningPoint.getIPAddress() + ":"
                            + listeningPoint.getPort());
        if (listeningPoint == null) {
            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                sipStack.getStackLogger().logDebug(
                        "Dropping message: No listening point registered!");
            return;
        }

        SIPTransactionStack sipStack = (SIPTransactionStack) transactionChannel
                .getSIPStack();

        SipProviderImpl sipProvider = listeningPoint.getProvider();
        if (sipProvider == null) {
            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                sipStack.getStackLogger().logDebug("No provider - dropping !!");
            return;
        }

        if (sipStack == null)
            InternalErrorHandler.handleException("Egads! no sip stack!");

        // Look for the registered SIPListener for the message channel.

        SIPServerTransaction transaction = (SIPServerTransaction) this.transactionChannel;
        if (transaction != null) {
            if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
                sipStack.getStackLogger().logDebug(
                        "transaction state = " + transaction.getState());
        }
        final String dialogId = sipRequest.getDialogId(true);
        SIPDialog dialog = sipStack.getDialog(dialogId);
        /*
         * Check if we got this request on the contact address of the dialog If
         * not the dialog does not belong to this request. We check this
         * condition if a contact address has been assigned to the dialog.
         * Forgive the sins of B2BUA's that like to record route ACK's
         */
        if (dialog != null && sipProvider != dialog.getSipProvider()) {
            final Contact contact = dialog.getMyContactHeader();
            if (contact != null) {
                SipUri contactUri = (SipUri) (contact.getAddress().getURI());
                String ipAddress = contactUri.getHost();
                int contactPort = contactUri.getPort();
                String contactTransport = contactUri.getTransportParam();
                if (contactTransport == null)
                    contactTransport = "udp";
                if (contactPort == -1) {
                    if (contactTransport.equals("udp")
                            || contactTransport.equals("tcp"))
                        contactPort = 5060;
                    else
                        contactPort = 5061;
                }
                // Check if the dialog contact is the same as the provider on
                // which we got the request. Otherwise, dont assign this
                // dialog to the request.
                if (ipAddress != null
                        && (!ipAddress.equals(listeningPoint.getIPAddress()) || contactPort != listeningPoint
                                .getPort())) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug(
                                "nulling dialog -- listening point mismatch!  "
                                        + contactPort + "  lp port = "
                                        + listeningPoint.getPort());

                    }
                    dialog = null;
                }

            }
        }

        /*
         * RFC 3261 8.2.2.2 Merged requests: If the request has no tag in the To
         * header field, the UAS core MUST check the request against ongoing
         * transactions. If the From tag, Call-ID, and CSeq exactly match those
         * associated with an ongoing transaction, but the request does not
         * match that transaction (based on the matching rules in Section
         * 17.2.3), the UAS core SHOULD generate a 482 (Loop Detected) response
         * and pass it to the server transaction. This support is only enabled
         * when the stack has been instructed to function with Automatic Dialog
         * Support.
         */
        if (sipProvider.isDialogErrorsAutomaticallyHandled()
                && sipRequest.getToTag() == null) {
            if (sipStack.findMergedTransaction(sipRequest)) {
                this.sendLoopDetectedResponse(sipRequest, transaction);
                return;
            }
        }

        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug("dialogId = " + dialogId);
            sipStack.getStackLogger().logDebug("dialog = " + dialog);
        }

        /*
         * RFC 3261 Section 16.4 If the first value in the Route header field
         * indicates this proxy,the proxy MUST remove that value from the
         * request .
         */

        // If the message is being processed
        // by a Proxy, then the proxy will take care of stripping the
        // Route header. If the request is being processed by an
        // endpoint, then the stack strips off the route header.
        if (sipRequest.getHeader(Route.NAME) != null
                && transaction.getDialog() != null) {
            final RouteList routes = sipRequest.getRouteHeaders();
            final Route route = (Route) routes.getFirst();
            final SipUri uri = (SipUri) route.getAddress().getURI();
            final HostPort hostPort = uri.getHostPort();
            int port;
            if (hostPort.hasPort()) {
                port = hostPort.getPort();
            } else {
                if (listeningPoint.getTransport().equalsIgnoreCase(
                        ListeningPoint.TLS))
                    port = 5061;
                else
                    port = 5060;
            }
            String host = hostPort.getHost().encode();
            if ((host.equals(listeningPoint.getIPAddress()) || host
                    .equalsIgnoreCase(listeningPoint.getSentBy()))
                    && port == listeningPoint.getPort()) {
                if (routes.size() == 1)
                    sipRequest.removeHeader(Route.NAME);
                else
                    routes.removeFirst();
            }
        }
        final String sipRequestMethod = sipRequest.getMethod();
        if (sipRequestMethod.equals(Request.REFER) && dialog != null
                && sipProvider.isDialogErrorsAutomaticallyHandled()) {
            /*
             * An agent responding to a REFER method MUST return a 400 (Bad
             * Request) if the request contained zero or more than one Refer-To
             * header field values.
             */
            ReferToHeader sipHeader = (ReferToHeader) sipRequest
                    .getHeader(ReferTo.NAME);
            if (sipHeader == null) {
                this.sendBadRequestResponse(sipRequest, transaction,
                        "Refer-To header is missing");
                return;

            }

            /*
             * A refer cannot be processed until previous transaction has been
             * completed.
             */
            SIPTransaction lastTransaction = ((SIPDialog) dialog)
                    .getLastTransaction();
            if (lastTransaction != null
                    && sipProvider.isDialogErrorsAutomaticallyHandled()) {               
                final String lastTransactionMethod = lastTransaction.getMethod();
                if (lastTransaction instanceof SIPServerTransaction) {
                    // Handle Pseudo State Trying on Server Transaction
                    if ((lastTransaction.getInternalState() == TransactionState._PROCEEDING
                                    || lastTransaction.getInternalState() == TransactionState._TRYING)
                            && lastTransactionMethod.equals(Request.INVITE)) {
                        this
                                .sendRequestPendingResponse(sipRequest,
                                        transaction);
                        return;
                    }
                } else if (lastTransaction != null
                        && lastTransaction instanceof SIPClientTransaction) {
                    if (lastTransactionMethod.equals(Request.INVITE)
                            && lastTransaction.getInternalState() != TransactionState._TERMINATED
                            && lastTransaction.getInternalState() != TransactionState._COMPLETED) {
                        this
                                .sendRequestPendingResponse(sipRequest,
                                        transaction);
                        return;
                    }
                }
            }

        } else if (sipRequestMethod.equals(Request.UPDATE)) {
            /*
             * Got an UPDATE method and the user dialog does not exist and the
             * user wants to be a User agent.
             */
            if (sipProvider.isAutomaticDialogSupportEnabled() && dialog == null) {
                this.sendCallOrTransactionDoesNotExistResponse(sipRequest,
                        transaction);
                return;
            }
        } else if (sipRequestMethod.equals(Request.ACK)) {

            if (transaction != null && transaction.isInviteTransaction()) {
                // This is an ack for a 3xx-6xx response. Just let the tx laer
                // take care of it.
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack.getStackLogger().logDebug(
                            "Processing ACK for INVITE Tx ");

            } else {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack.getStackLogger().logDebug(
                            "Processing ACK for dialog " + dialog);

                if (dialog == null) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug(
                                "Dialog does not exist "
                                        + sipRequest.getFirstLine()
                                        + " isServerTransaction = " + true);

                    }
                    SIPServerTransaction st = sipStack
                            .getRetransmissionAlertTransaction(dialogId);
                    if (st != null && st.isRetransmissionAlertEnabled()) {
                        st.disableRetransmissionAlerts();

                    }
                    /*
                     * JvB: must never drop ACKs that dont match a transaction!
                     * One cannot be sure if it isn't an ACK for a 2xx response
                     */
                    SIPServerTransaction ackTransaction = sipStack
                            .findTransactionPendingAck(sipRequest);
                    /*
                     * Found a transaction ( that we generated ) which is
                     * waiting for ACK. So ACK it and return.
                     */
                    if (ackTransaction != null) {
                        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                            sipStack.getStackLogger().logDebug(
                                    "Found Tx pending ACK");
                        try {
                            ackTransaction.setAckSeen();
                            sipStack.removeTransaction(ackTransaction);
                            sipStack
                                    .removeTransactionPendingAck(ackTransaction);
                        } catch (Exception ex) {
                            if (sipStack.isLoggingEnabled()) {
                                sipStack.getStackLogger().logError(
                                        "Problem terminating transaction", ex);
                            }
                        }
                        return;
                    }

                } else {
                    if (!dialog.handleAck(transaction)) {
                        if (!dialog.isSequnceNumberValidation()) {
                            if (sipStack.isLoggingEnabled()) {
                                sipStack.getStackLogger().logDebug(
                                        "Dialog exists with loose dialog validation "
                                                + sipRequest.getFirstLine()
                                                + " isServerTransaction = "
                                                + true + " dialog = "
                                                + dialog.getDialogId());

                            }
                            SIPServerTransaction st = sipStack
                                    .getRetransmissionAlertTransaction(dialogId);                           
                            if (st != null && st.isRetransmissionAlertEnabled()) {
                            	st.disableRetransmissionAlerts();

                            }
                            // Issue 319 : https://jain-sip.dev.java.net/issues/show_bug.cgi?id=319
                            // remove the pending ack to stop the transaction timer for transaction
                            // where the stack replied with a final error response.
                            SIPServerTransaction ackTransaction = sipStack
                                    .findTransactionPendingAck(sipRequest);
                            /*
                             * Found a transaction ( that we generated ) which is
                             * waiting for ACK. So ACK it and return.
                             */
                            if (ackTransaction != null) {
                                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                                    sipStack.getStackLogger().logDebug(
                                            "Found Tx pending ACK");
                                try {
                                    ackTransaction.setAckSeen();
                                    sipStack.removeTransaction(ackTransaction);
                                    sipStack
                                            .removeTransactionPendingAck(ackTransaction);
                                } catch (Exception ex) {
                                    if (sipStack.isLoggingEnabled()) {
                                        sipStack.getStackLogger().logError(
                                                "Problem terminating transaction", ex);
                                    }
                                }
                            }
                        } else {
                            if (sipStack
                                    .isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                                sipStack
                                        .getStackLogger()
                                        .logDebug(
                                                "Dropping ACK - cannot find a transaction or dialog");
                            }
                            SIPServerTransaction ackTransaction = sipStack
                                    .findTransactionPendingAck(sipRequest);
                            if (ackTransaction != null) {
                                if (sipStack.isLoggingEnabled())
                                    sipStack.getStackLogger().logDebug(
                                            "Found Tx pending ACK");
                                try {
                                    ackTransaction.setAckSeen();
                                    sipStack.removeTransaction(ackTransaction);
                                    sipStack
                                            .removeTransactionPendingAck(ackTransaction);
                                } catch (Exception ex) {
                                    if (sipStack.isLoggingEnabled()) {
                                        sipStack
                                                .getStackLogger()
                                                .logError(
                                                        "Problem terminating transaction",
                                                        ex);
                                    }
                                }
                            }
                            /*
                             * For test only we support a flag that will deliver
                             * retransmitted ACK for 200 OK responses to the
                             * listener.
                             */
                            if ((!sipStack
                                    .isDeliverRetransmittedAckToListener())
                                    || (ackTransaction != null && !sipStack
                                            .isNon2XXAckPassedToListener())) {
                                return;
                            }
                        }
                    } else {

                        dialog.addTransaction(transaction);
                        transaction.passToListener();
                        dialog.addRoute(sipRequest);
                        transaction.setDialog(dialog, dialogId);
                        if (sipRequest.getMethod().equals(Request.INVITE)
                                && sipProvider
                                        .isDialogErrorsAutomaticallyHandled()) {
                            sipStack.putInMergeTable(transaction, sipRequest);
                        }
                        /*
                         * Note that ACK is a pseudo transaction. It is never
                         * added to the stack and you do not get transaction
                         * terminated events on ACK.
                         */

                        if (sipStack.isDeliverTerminatedEventForAck()) {
                            try {
                                sipStack.addTransaction(transaction);
                                transaction.scheduleAckRemoval();
                            } catch (IOException ex) {

                            }
                        } else {
                            transaction.setMapped(true);
                        }

                    }
                }
            }
        } else if (sipRequestMethod.equals(Request.PRACK)) {

            /*
             * RFC 3262: A matching PRACK is defined as one within the same
             * dialog as the response, and whose method, CSeq-num, and
             * response-num in the RAck header field match, respectively, the
             * method from the CSeq, the sequence number from the CSeq, and the
             * sequence number from the RSeq of the reliable provisional
             * response.
             */

            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                sipStack.getStackLogger().logDebug(
                        "Processing PRACK for dialog " + dialog);

            if (dialog == null && sipProvider.isAutomaticDialogSupportEnabled()) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug(
                            "Dialog does not exist "
                                    + sipRequest.getFirstLine()
                                    + " isServerTransaction = " + true);

                }
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack
                            .getStackLogger()
                            .logDebug(
                                    "Sending 481 for PRACK - automatic dialog support is enabled -- cant find dialog!");
                }
                SIPResponse notExist = sipRequest
                        .createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);

                try {
                    sipProvider.sendResponse(notExist);
                } catch (SipException e) {
                    sipStack.getStackLogger().logError(
                            "error sending response", e);
                }
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    transaction.releaseSem();
                }
                return;

            } else if (dialog != null) {
                if (!dialog.handlePrack(sipRequest)) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                        sipStack.getStackLogger().logDebug(
                                "Dropping out of sequence PRACK ");
                    if (transaction != null) {
                        sipStack.removeTransaction(transaction);
                        transaction.releaseSem();
                    }
                    return;
                } else {
                    try {
                        sipStack.addTransaction(transaction);
                        dialog.addTransaction(transaction);
                        dialog.addRoute(sipRequest);
                        transaction.setDialog(dialog, dialogId);
                    } catch (Exception ex) {
                        InternalErrorHandler.handleException(ex);
                    }
                }
            } else {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack
                            .getStackLogger()
                            .logDebug(
                                    "Processing PRACK without a DIALOG -- this must be a proxy element");
            }

        } else if (sipRequestMethod.equals(Request.BYE)) {
            // Check for correct sequence numbering of the BYE
            if (dialog != null && !dialog.isRequestConsumable(sipRequest)) {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack.getStackLogger().logDebug(
                            "Dropping out of sequence BYE "
                                    + dialog.getRemoteSeqNumber() + " "
                                    + sipRequest.getCSeq().getSeqNumber());

                if (dialog.getRemoteSeqNumber() >= sipRequest.getCSeq()
                        .getSeqNumber()
                        && transaction.getInternalState() == TransactionState._TRYING) {

                    this.sendServerInternalErrorResponse(sipRequest,
                            transaction);

                }
                // If the stack knows about the tx, then remove it.
                if (transaction != null)
                    sipStack.removeTransaction(transaction);
                return;

            } else if (dialog == null
                    && sipProvider.isAutomaticDialogSupportEnabled()) {
                // Drop bye's with 481 if dialog does not exist.
                // If dialog support is enabled then
                // there must be a dialog associated with the bye
                // No dialog could be found and requests on this
                // provider. Must act like a user agent -- so drop the request.
                // NOTE: if Automatic dialog support is not enabled,
                // then it is the application's responsibility to
                // take care of this error condition possibly.

                SIPResponse response = sipRequest
                        .createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                response.setReasonPhrase("Dialog Not Found");

                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack
                            .getStackLogger()
                            .logDebug(
                                    "dropping request -- automatic dialog "
                                            + "support enabled and dialog does not exist!");
                try {
                    transaction.sendResponse(response);
                } catch (SipException ex) {
                    sipStack.getStackLogger().logError(
                            "Error in sending response", ex);
                }
                // If the stack knows about the tx, then remove it.
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    transaction.releaseSem();
                    transaction = null;
                }
                return;

            }

            // note that the transaction may be null (which
            // happens when no dialog for the bye was found.
            // and automatic dialog support is disabled (i.e. the app wants
            // to manage its own dialog layer.
            if (transaction != null && dialog != null) {
                try {
                    if (sipProvider == dialog.getSipProvider()) {
                        sipStack.addTransaction(transaction);
                        dialog.addTransaction(transaction);
                        transaction.setDialog(dialog, dialogId);
                    }

                } catch (IOException ex) {
                    InternalErrorHandler.handleException(ex);
                }
            }
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug(
                        "BYE Tx = " + transaction + " isMapped ="
                                + transaction.isTransactionMapped());
            }

        } else if (sipRequestMethod.equals(Request.CANCEL)) {

            SIPServerTransaction st = (SIPServerTransaction) sipStack
                    .findCancelTransaction(sipRequest, true);
            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug(
                        "Got a CANCEL, InviteServerTx = " + st
                                + " cancel Server Tx ID = " + transaction
                                + " isMapped = "
                                + transaction.isTransactionMapped());

            }
            // Processing incoming CANCEL.
            // Check if we can process the CANCEL request.
            if (sipRequest.getMethod().equals(Request.CANCEL)) {
                // If the CANCEL comes in too late, there's not
                // much that the Listener can do so just do the
                // default action and avoid bothering the listener.
                if (st != null
                        && st.getInternalState() == TransactionState._TERMINATED) {
                    // If transaction already exists but it is
                    // too late to cancel the transaction then
                    // just respond OK to the CANCEL and bail.
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                        sipStack.getStackLogger().logDebug(
                                "Too late to cancel Transaction");
                    // send OK and just ignore the CANCEL.
                    try {

                        transaction.sendResponse(sipRequest
                                .createResponse(Response.OK));
                    } catch (Exception ex) {
                        if (ex.getCause() != null
                                && ex.getCause() instanceof IOException) {
                            st.raiseIOExceptionEvent();
                        }
                    }
                    return;
                }
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack.getStackLogger().logDebug(
                            "Cancel transaction = " + st);

            }
            if (transaction != null && st != null && st.getDialog() != null) {
                // Found an invite tx corresponding to the CANCEL.
                // Set up the client tx and pass up to listener.
                transaction.setDialog((SIPDialog) st.getDialog(), dialogId);
                dialog = (SIPDialog) st.getDialog();
            } else if (st == null
                    && sipProvider.isAutomaticDialogSupportEnabled()
                    && transaction != null) {
                // Could not find a invite tx corresponding to the CANCEL.
                // Automatic dialog support is enabled so I must behave like
                // an endpoint on this provider.
                // Send the error response for the cancel.

                SIPResponse response = sipRequest
                        .createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack.getStackLogger().logDebug(
                            "dropping request -- automatic dialog support "
                                    + "enabled and INVITE ST does not exist!");
                }
                try {
                    sipProvider.sendResponse(response);
                } catch (SipException ex) {
                    InternalErrorHandler.handleException(ex);
                }
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    transaction.releaseSem();
                }
                return;

            }

            // INVITE was handled statefully so the CANCEL must also be
            // statefully handled.
            if (st != null) {
                try {
                    if (transaction != null) {
                        sipStack.addTransaction(transaction);
                        transaction.setPassToListener();
                        transaction.setInviteTransaction(st);
                        // Dont let the INVITE and CANCEL be concurrently
                        // processed.
                        st.acquireSem();

                    }

                } catch (Exception ex) {
                    InternalErrorHandler.handleException(ex);
                }
            }
        } else if (sipRequestMethod.equals(Request.INVITE)) {
            SIPTransaction lastTransaction = dialog == null ? null : dialog
                    .getInviteTransaction();

            /*
             * RFC 3261 Chapter 14. A UAS that receives a second INVITE before
             * it sends the final response to a first INVITE with a lower CSeq
             * sequence number on the same dialog MUST return a 500 (Server
             * Internal Error) response to the second INVITE and MUST include a
             * Retry-After header field with a randomly chosen value of between
             * 0 and 10 seconds.
             */

            if (dialog != null
                    && transaction != null
                    && lastTransaction != null
                    && sipRequest.getCSeq().getSeqNumber() > dialog
                            .getRemoteSeqNumber()
                    && lastTransaction instanceof SIPServerTransaction
                    && sipProvider.isDialogErrorsAutomaticallyHandled()
                    && dialog.isSequnceNumberValidation()
                    && lastTransaction.isInviteTransaction()
                    && lastTransaction.getInternalState() != TransactionState._COMPLETED
                    && lastTransaction.getInternalState() != TransactionState._TERMINATED
                    && lastTransaction.getInternalState() != TransactionState._CONFIRMED) {

                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack.getStackLogger().logDebug(
                            "Sending 500 response for out of sequence message");
                }
                this.sendServerInternalErrorResponse(sipRequest, transaction);
                return;

            }

            /*
             * Saw an interleaved invite before ACK was sent. RFC 3261 Chapter
             * 14. A UAS that receives an INVITE on a dialog while an INVITE it
             * had sent on that dialog is in progress MUST return a 491 (Request
             * Pending) response to the received INVITE.
             */
            lastTransaction = (dialog == null ? null : dialog
                    .getLastTransaction());

            if (dialog != null
                    && sipProvider.isDialogErrorsAutomaticallyHandled()
                    && lastTransaction != null
                    && lastTransaction.isInviteTransaction()
                    && lastTransaction instanceof ClientTransaction
                    && lastTransaction.getLastResponse() != null
                    && lastTransaction.getLastResponse().getStatusCode() == 200
                    && !dialog.isAckSent(lastTransaction.getLastResponse()
                            .getCSeq().getSeqNumber())) {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack
                            .getStackLogger()
                            .logDebug(
                                    "Sending 491 response for client Dialog ACK not sent.");
                }
                this.sendRequestPendingResponse(sipRequest, transaction);
                return;
            }

            if (dialog != null
                    && lastTransaction != null
                    && sipProvider.isDialogErrorsAutomaticallyHandled()
                    && lastTransaction.isInviteTransaction()
                    && lastTransaction instanceof ServerTransaction
                    // Handle Pseudo State Trying on Server Transaction
                    && (lastTransaction.getInternalState() == TransactionState._PROCEEDING
                                    || lastTransaction.getInternalState() == TransactionState._TRYING)) {
                // Note that the completed state will be reached when we have
                // sent an error
                // response and the terminated state will be reached when we
                // have sent an OK
                // response. We do not need to wait till the ACK to be seen.
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack
                            .getStackLogger()
                            .logDebug(
                                    "Sending 491 response. Last transaction is in PROCEEDING state.");
                    sipStack.getStackLogger().logDebug(
                            "last Transaction state = " + lastTransaction
                                    + " state " + lastTransaction.getState());
                }
                this.sendRequestPendingResponse(sipRequest, transaction);
                return;

            }
        }

        // Sequence numbers are supposed to be incremented
        // sequentially within a dialog for RFC 3261
        // Note BYE, CANCEL and ACK is handled above - so no check here.

        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    "CHECK FOR OUT OF SEQ MESSAGE " + dialog + " transaction "
                            + transaction);
        }

        if (dialog != null && transaction != null
                && !sipRequestMethod.equals(Request.BYE)
                && !sipRequestMethod.equals(Request.CANCEL)
                && !sipRequestMethod.equals(Request.ACK)
                && !sipRequestMethod.equals(Request.PRACK)) {

            if (!dialog.isRequestConsumable(sipRequest)) {

                /*
                 * RFC 3261: "UAS Behavior" section (12.2.2): If the remote
                 * sequence number was not empty, but the sequence number of the
                 * request is lower than the remote sequence number, the request
                 * is out of order and MUST be rejected with a 500 (Server
                 * Internal Error) response.
                 */

                // Drop the request
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack.getStackLogger().logDebug(
                            "Dropping out of sequence message "
                                    + dialog.getRemoteSeqNumber() + " "
                                    + sipRequest.getCSeq());
                }

                // send error when stricly higher, ignore when ==
                // (likely still processing, error would interrupt that)
                /*
                 * && (transaction.getState() == TransactionState.TRYING ||
                 * transaction .getState() == TransactionState.PROCEEDING)
                 */

                if (dialog.getRemoteSeqNumber() > sipRequest.getCSeq()
                        .getSeqNumber()
                        && sipProvider.isDialogErrorsAutomaticallyHandled()) {
                    this.sendServerInternalErrorResponse(sipRequest,
                            transaction);
                } else {
                    try {
                        transaction.terminate();
                    } catch (ObjectInUseException e) {
                        if (sipStack.isLoggingEnabled()) {
                            sipStack.getStackLogger().logError(
                                    "Unexpected exception", e);
                        }
                    }
                }
                return;
            }

            try {
                if (sipProvider == dialog.getSipProvider()) {
                    sipStack.addTransaction(transaction);
                    // This will set the remote sequence number.
                    if (!dialog.addTransaction(transaction)) {
                        return;
                    }
                    dialog.addRoute(sipRequest);
                    transaction.setDialog(dialog, dialogId);

                }
            } catch (IOException ex) {
                transaction.raiseIOExceptionEvent();
                sipStack.removeTransaction(transaction);
                return;
            }

        }

        RequestEvent sipEvent;

        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    sipRequest.getMethod() + " transaction.isMapped = "
                            + transaction.isTransactionMapped());
        }

        /*
         * RFC 3265: Each event package MUST specify whether forked SUBSCRIBE
         * requests are allowed to install multiple subscriptions. If such
         * behavior is not allowed, the first potential dialog- establishing
         * message will create a dialog. All subsequent NOTIFY messages which
         * correspond to the SUBSCRIBE message (i.e., match "To", "From", "From"
         * header "tag" parameter, "Call-ID", "CSeq", "Event", and "Event"
         * header "id" parameter) but which do not match the dialog would be
         * rejected with a 481 response. Note that the 200-class response to the
         * SUBSCRIBE can arrive after a matching NOTIFY has been received; such
         * responses might not correlate to the same dialog established by the
         * NOTIFY. Except as required to complete the SUBSCRIBE transaction,
         * such non-matching 200-class responses are ignored.
         */

        if (dialog == null && sipRequestMethod.equals(Request.NOTIFY)) {

            SIPClientTransaction pendingSubscribeClientTx = sipStack
                    .findSubscribeTransaction(sipRequest, listeningPoint);

            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug(
                        "PROCESSING NOTIFY  DIALOG == null "
                                + pendingSubscribeClientTx);
            }

            /*
             * RFC 3265: Upon receiving a NOTIFY request, the subscriber should
             * check that it matches at least one of its outstanding
             * subscriptions; if not, it MUST return a
             * "481 Subscription does not exist" response unless another 400- or
             * -class response is more appropriate.
             */
            if (sipProvider.isAutomaticDialogSupportEnabled()
                    && pendingSubscribeClientTx == null
                    && !sipStack.isDeliverUnsolicitedNotify()) {
                /*
                 * This is the case of the UAC receiving a Stray NOTIFY for
                 * which it has not previously sent out a SUBSCRIBE and for
                 * which it does not have an established dialog.
                 */
                try {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug(
                                "Could not find Subscription for Notify Tx.");
                    }
                    Response errorResponse = sipRequest
                            .createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                    errorResponse
                            .setReasonPhrase("Subscription does not exist");
                    sipProvider.sendResponse(errorResponse);
                    return;

                } catch (Exception ex) {
                    sipStack
                            .getStackLogger()
                            .logError(
                                    "Exception while sending error response statelessly",
                                    ex);
                    return;
                }

            }

            // If the server transaction cannot be found or if it
            // aleady has a dialog attached to it then just assign the
            // notify to this dialog and pass it up.
            if (pendingSubscribeClientTx != null) {
                // The response to the pending subscribe tx can try to create
                // a dialog at the same time that the notify is trying to
                // create a dialog. Thus we cannot process both at the
                // same time.

                transaction.setPendingSubscribe(pendingSubscribeClientTx);
                // The transaction gets assigned to the dialog from the
                // outgoing subscribe. First see if anybody claimed the
                // default Dialog for the outgoing Subscribe request.
                SIPDialog subscriptionDialog = (SIPDialog) pendingSubscribeClientTx
                        .getDefaultDialog();

                // TODO -- refactor this. Can probably be written far cleaner.
                if (subscriptionDialog == null
                        || subscriptionDialog.getDialogId() == null
                        || !subscriptionDialog.getDialogId().equals(dialogId)) {
                    // Notify came in before you could assign a response to
                    // the subscribe.
                    // grab the default dialog and assign it to the tags in
                    // the notify.
                    if (subscriptionDialog != null
                            && subscriptionDialog.getDialogId() == null) {
                        subscriptionDialog.setDialogId(dialogId);

                    } else {
                        subscriptionDialog = pendingSubscribeClientTx
                                .getDialog(dialogId);
                    }
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug(
                                "PROCESSING NOTIFY Subscribe DIALOG "
                                        + subscriptionDialog);
                    }

                    // The user could have createed a dialog before sending out
                    // the SUBSCRIBE on the subscribe tx.
                    if (subscriptionDialog == null
                            && (sipProvider.isAutomaticDialogSupportEnabled() || pendingSubscribeClientTx
                                    .getDefaultDialog() != null)) {
                        Event event = (Event) sipRequest
                                .getHeader(EventHeader.NAME);
                        if (sipStack.isEventForked(event.getEventType())) {

                            subscriptionDialog = SIPDialog.createFromNOTIFY(
                                    pendingSubscribeClientTx, transaction);

                        }

                    }
                    if (subscriptionDialog != null) {
                        transaction.setDialog(subscriptionDialog, dialogId);
                        if (subscriptionDialog.getState() != DialogState.CONFIRMED) {
                            subscriptionDialog
                                    .setPendingRouteUpdateOn202Response(sipRequest);

                        }
                        subscriptionDialog.setState(DialogState.CONFIRMED
                                .getValue());
                        sipStack.putDialog(subscriptionDialog);
                        pendingSubscribeClientTx.setDialog(subscriptionDialog,
                                dialogId);
                        if (!transaction.isTransactionMapped()) {
                            this.sipStack.mapTransaction(transaction);
                            // Let the listener see it if it just got
                            // created.
                            // otherwise, we have already processed the tx
                            // so
                            // we dont want the listener to see it.
                            transaction.setPassToListener();
                            try {
                                this.sipStack.addTransaction(transaction);
                            } catch (Exception ex) {
                            }
                        }
                    }
                } else {
                    // The subscription default dialog is our dialog.
                    // Found a subscrbe dialog for the NOTIFY
                    // So map the tx.
                    transaction.setDialog(subscriptionDialog, dialogId);
                    dialog = subscriptionDialog;
                    if (!transaction.isTransactionMapped()) {
                        this.sipStack.mapTransaction(transaction);
                        // Let the listener see it if it just got created.
                        // otherwise, we have already processed the tx so
                        // we dont want the listener to see it.
                        transaction.setPassToListener();
                        try {
                            this.sipStack.addTransaction(transaction);
                        } catch (Exception ex) {
                        }
                    }
                    sipStack.putDialog(subscriptionDialog);
                    if (pendingSubscribeClientTx != null) {
                        subscriptionDialog
                                .addTransaction(pendingSubscribeClientTx);
                        pendingSubscribeClientTx.setDialog(subscriptionDialog,
                                dialogId);

                    }
                }
                if (transaction != null
                        && ((SIPServerTransaction) transaction)
                                .isTransactionMapped()) {
                    // Shadow transaction has been created and the stack
                    // knows
                    // about it.
                    sipEvent = new RequestEvent((SipProvider) sipProvider,
                            (ServerTransaction) transaction,
                            subscriptionDialog, (Request) sipRequest);
                } else {
                    /*
                     * Shadow transaction has been created but the stack does
                     * not know about it.
                     */
                    sipEvent = new RequestEvent((SipProvider) sipProvider,
                            null, subscriptionDialog, (Request) sipRequest);
                }

            } else {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack.getStackLogger().logDebug(
                            "could not find subscribe tx");
                }

                // Got a notify out of the blue - just pass it up
                // for stateless handling by the application.
                sipEvent = new RequestEvent(sipProvider, null, null,
                        (Request) sipRequest);
            }

        } else {

            // For a dialog creating event - set the transaction to null.
            // The listener can create the dialog if needed.
            if (transaction != null
                    && (((SIPServerTransaction) transaction)
                            .isTransactionMapped())) {
                sipEvent = new RequestEvent(sipProvider,
                        (ServerTransaction) transaction, dialog,
                        (Request) sipRequest);
            } else {
                sipEvent = new RequestEvent(sipProvider, null, dialog,
                        (Request) sipRequest);
            }
        }
        sipProvider.handleEvent(sipEvent, transaction);

    }

    /**
     * Process the response.
     * 
     * @exception SIPServerException
     *                is thrown when there is an error processing the response
     * @param incomingMessageChannel
     *            -- message channel on which the response is received.
     */
    public void processResponse(SIPResponse response,
            MessageChannel incomingMessageChannel, SIPDialog dialog) {
        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    "PROCESSING INCOMING RESPONSE"
                            + response.encodeMessage(new StringBuilder()));
        }
        if (listeningPoint == null) {
            if (sipStack.isLoggingEnabled())
                sipStack.getStackLogger()
                        .logError(
                                "Dropping message: No listening point"
                                        + " registered!");
            return;
        }

        if (sipStack.checkBranchId()
                && !Utils.getInstance().responseBelongsToUs(response)) {
            if (sipStack.isLoggingEnabled()) {
                sipStack
                        .getStackLogger()
                        .logError(
                                "Dropping response - topmost VIA header does not originate from this stack");
            }
            return;
        }

        SipProviderImpl sipProvider = listeningPoint.getProvider();
        if (sipProvider == null) {
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logError(
                        "Dropping message:  no provider");
            }
            return;
        }
        if (sipProvider.getSipListener() == null) {
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logError(
                        "No listener -- dropping response!");
            }
            return;
        }

        SIPClientTransaction transaction = (SIPClientTransaction) this.transactionChannel;
        SIPTransactionStack sipStackImpl = sipProvider.sipStack;

        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
            sipStackImpl.getStackLogger().logDebug(
                    "Transaction = " + transaction);
        }

        if (transaction == null) {
            // Transaction is null but the dialog is not null. This means that
            // the transaction has been removed by the stack.
            // If the dialog exists, then it may need to retransmit ACK so
            // we cannot drop the response.
            if (dialog != null) {
                if (response.getStatusCode() / 100 != 2) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack
                                .getStackLogger()
                                .logDebug(
                                        "Response is not a final response and dialog is found for response -- dropping response!");
                    }
                    return;
                } else if (dialog.getState() == DialogState.TERMINATED) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug(
                                "Dialog is terminated -- dropping response!");
                    }
                    return;
                } else {
                    boolean ackAlreadySent = false;
                    if (dialog.isAckSeen() && dialog.getLastAckSent() != null) {
                        if (dialog.getLastAckSent().getCSeq().getSeqNumber() == response
                                .getCSeq().getSeqNumber()) {
                            // the last ack sent corresponded to this 200
                            ackAlreadySent = true;
                        }
                    }
                    // 200 retransmission for the final response.
                    if (ackAlreadySent
                            && response.getCSeq().getMethod().equals(
                                    dialog.getMethod())) {
                        try {
                            // Found the dialog - resend the ACK and
                            // dont pass up the null transaction
                            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                                sipStack
                                        .getStackLogger()
                                        .logDebug(
                                                "Retransmission of OK detected: Resending last ACK");
                            }
                            dialog.resendAck();
                            return;
                        } catch (SipException ex) {
                            // What to do here ?? kill the dialog?
                            sipStack.getStackLogger().logError(
                                    "could not resend ack", ex);
                        }
                    }
                }
            }

            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug(
                        "could not find tx, handling statelessly Dialog =  "
                                + dialog);
            }
            // Pass the response up to the application layer to handle
            // statelessly.

            ResponseEventExt sipEvent = new ResponseEventExt(sipProvider,
                    transaction, dialog, (Response) response);

            if (sipStack.getMaxForkTime() != 0
                    && response.getCSeqHeader().getMethod().equals(
                            Request.INVITE)) {
                SIPClientTransaction forked = this.sipStack
                        .getForkedTransaction(response.getForkId());
                if(dialog != null && forked != null && forked.getDefaultDialog() != null && !dialog.equals(forked.getDefaultDialog())) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug(
                                "forked dialog " + dialog + " original tx " + forked + " original dialog " + forked.getDefaultDialog());
                    }
                    sipEvent.setOriginalTransaction(forked);
                }
            }

            sipProvider.handleEvent(sipEvent, transaction);
            return;
        }

        ResponseEventExt responseEvent = null;

        // Here if there is an assigned dialog
        responseEvent = new ResponseEventExt(sipProvider,
                (ClientTransactionExt) transaction, dialog, (Response) response);
        if (sipStack.getMaxForkTime() != 0
                && response.getCSeqHeader().getMethod().equals(Request.INVITE)) {
            SIPClientTransaction forked = this.sipStack
                    .getForkedTransaction(response.getForkId());
            if(dialog != null && forked != null && forked.getDefaultDialog() != null && !dialog.equals(forked.getDefaultDialog())) {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack.getStackLogger().logDebug(
                            "forked dialog " + dialog + " original tx " + forked + " original dialog " + forked.getDefaultDialog());
                }
                responseEvent.setOriginalTransaction(forked);
            }
        }

        // Set the Dialog for the response.
        if (dialog != null && response.getStatusCode() != 100) {
            // set the last response for the dialog.
            dialog.setLastResponse(transaction, response);
            transaction.setDialog(dialog, dialog.getDialogId());
        }

        sipProvider.handleEvent(responseEvent, transaction);
    }

    /**
     * Just a placeholder. This is called from the stack for message logging.
     * Auxiliary processing information can be passed back to be written into
     * the log file.
     * 
     * @return auxiliary information that we may have generated during the
     *         message processing which is retrieved by the message logger.
     */
    public String getProcessingInfo() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.nist.javax.sip.stack.ServerResponseInterface#processResponse(gov.
     * nist.javax.sip.message.SIPResponse,
     * gov.nist.javax.sip.stack.MessageChannel)
     */
    public void processResponse(SIPResponse sipResponse,
            MessageChannel incomingChannel) {
        String dialogID = sipResponse.getDialogId(false);
        SIPDialog sipDialog = this.sipStack.getDialog(dialogID);

        String method = sipResponse.getCSeq().getMethod();
        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    "PROCESSING INCOMING RESPONSE: "
                            + sipResponse.encodeMessage(new StringBuilder()));
        }

        if (sipStack.checkBranchId()
                && !Utils.getInstance().responseBelongsToUs(sipResponse)) {
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logError(
                        "Detected stray response -- dropping");
            }
            return;
        }

        if (listeningPoint == null) {
            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                sipStack.getStackLogger()
                        .logDebug(
                                "Dropping message: No listening point"
                                        + " registered!");
            return;
        }

        SipProviderImpl sipProvider = listeningPoint.getProvider();
        if (sipProvider == null) {
            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug(
                        "Dropping message:  no provider");
            }
            return;
        }

        if (sipProvider.getSipListener() == null) {
            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug(
                        "Dropping message:  no sipListener registered!");
            }
            return;
        }

        SIPClientTransaction transaction = (SIPClientTransaction) this.transactionChannel;
        // This may be a dialog creating method for which the ACK has not yet
        // been sent
        // but the dialog has already been assigned ( happens this way for
        // 3PCC).
        if (sipDialog == null && transaction != null) {
            sipDialog = transaction.getDialog(dialogID);
            if (sipDialog != null
                    && sipDialog.getState() == DialogState.TERMINATED)
                sipDialog = null;
        }

        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
            sipStack.getStackLogger().logDebug(
                    "Transaction = " + transaction + " sipDialog = "
                            + sipDialog);

        if (this.transactionChannel != null) {
            String originalFrom = ((SIPRequest) this.transactionChannel
                    .getRequest()).getFromTag();
            if (originalFrom == null ^ sipResponse.getFrom().getTag() == null) {
                if (sipStack.isLoggingEnabled())
                    sipStack.getStackLogger().logDebug(
                            "From tag mismatch -- dropping response");
                return;
            }
            if (originalFrom != null
                    && !originalFrom.equalsIgnoreCase(sipResponse.getFrom()
                            .getTag())) {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                    sipStack.getStackLogger().logDebug(
                            "From tag mismatch -- dropping response");
                return;
            }

        }
        if (sipStack.isDialogCreated(method)
                && sipResponse.getStatusCode() != 100
                && sipResponse.getFrom().getTag() != null
                && sipResponse.getTo().getTag() != null && sipDialog == null) {
            // Issue 317 : for forked response even if automatic dialog support is not enabled
            // a dialog should be created in the case where the original Tx already have a default dialog
            // and the current dialog is null. This is also avoiding creating dialog automatically if the flag is not set
            boolean createDialog = false;
            if (sipProvider.isAutomaticDialogSupportEnabled()) {
                 createDialog = true;
            } else if(!sipProvider.isAutomaticDialogSupportEnabled() && sipResponse.getCSeq().getMethod().equals(Request.INVITE) && sipStack.getMaxForkTime() > 0 && sipDialog == null) {
                ClientTransactionExt originalTx = this.sipStack
                    .getForkedTransaction(sipResponse.getForkId());
                if(originalTx != null && originalTx.getDefaultDialog() != null) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                        sipStack.getStackLogger().logDebug(
                                "Need to create dialog for response = " + sipResponse);
                    createDialog = true;
                }
            } 
            if(createDialog) {
                if (this.transactionChannel != null) {
                    if (sipDialog == null) {
                        // There could be an existing dialog for this response.
                        sipDialog = sipStack.createDialog(
                                (SIPClientTransaction) this.transactionChannel,
                                sipResponse);

                        this.transactionChannel.setDialog(sipDialog,
                                sipResponse.getDialogId(false));
                    }
                } else {
                    sipDialog = this.sipStack.createDialog(sipProvider,
                            sipResponse);
                }
            }

        } else {
            // Have a dialog but could not find transaction.
            if (sipDialog != null && transaction == null
                    && sipDialog.getState() != DialogState.TERMINATED) {
                if (sipResponse.getStatusCode() / 100 != 2) {
                    if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                        sipStack.getStackLogger().logDebug(
                                "status code != 200 ; statusCode = "
                                        + sipResponse.getStatusCode());
                } else if (sipDialog.getState() == DialogState.TERMINATED) {
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug(
                                "Dialog is terminated -- dropping response!");
                    }
                    // Dialog exists but was terminated - just create and send
                    // an ACK for the OK.
                    // It could be late arriving.
                    if (sipResponse.getStatusCode() / 100 == 2
                            && sipResponse.getCSeq().getMethod().equals(
                                    Request.INVITE)) {
                        try {
                            Request ackRequest = sipDialog
                                    .createAck(sipResponse.getCSeq()
                                            .getSeqNumber());
                            sipDialog.sendAck(ackRequest);
                        } catch (Exception ex) {
                            sipStack.getStackLogger().logError(
                                    "Error creating ack", ex);
                        }
                    }
                    return;
                } else {
                    boolean ackAlreadySent = false;
                    if (/* sipDialog.isAckSeen()
                            && */ sipDialog.getLastAckSent() != null) {
                        if (sipDialog.getLastAckSent().getCSeq().getSeqNumber() == sipResponse
                                .getCSeq().getSeqNumber()
                                && sipResponse.getDialogId(false).equals(
                                        sipDialog.getLastAckSent().getDialogId(
                                                false))) {
                            // the last ack sent corresponded to this 200
                            ackAlreadySent = true;
                        }
                    }
                    // 200 retransmission for the final response.
                    if (ackAlreadySent
                            && sipResponse.getCSeq().getMethod().equals(
                                    sipDialog.getMethod())) {
                        try {
                            // Found the dialog - resend the ACK and
                            // dont pass up the null transaction
                            if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
                                sipStack.getStackLogger().logDebug(
                                        "resending ACK");

                            sipDialog.resendAck();
                            return;
                        } catch (SipException ex) {
                            // What to do here ?? kill the dialog?
                        }
                    }
                }
            }
            // Pass the response up to the application layer to handle
            // statelessly.

        }
        if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG))
            sipStack.getStackLogger().logDebug(
                    "sending response to TU for processing ");

        if (sipDialog != null && sipResponse.getStatusCode() != 100
                && sipResponse.getTo().getTag() != null) {
            sipDialog.setLastResponse(transaction, sipResponse);
        }

        ResponseEventExt responseEvent = new ResponseEventExt(sipProvider,
                (ClientTransactionExt) transaction, sipDialog,
                (Response) sipResponse);

        if (sipStack.getMaxForkTime() != 0
                && sipResponse.getCSeq().getMethod().equals(Request.INVITE)) {
            ClientTransactionExt originalTx = this.sipStack
                    .getForkedTransaction(sipResponse.getForkId());
            if(sipDialog != null && originalTx != null && originalTx.getDefaultDialog() != null && !sipDialog.equals(originalTx.getDefaultDialog())) {
                if (sipStack.isLoggingEnabled(LogLevels.TRACE_DEBUG)) {
                    sipStack.getStackLogger().logDebug(
                            "forked dialog " + sipDialog + " original tx " + originalTx + " original dialog " + originalTx.getDefaultDialog());
                }
                responseEvent.setOriginalTransaction(originalTx);
            }
        }

        sipProvider.handleEvent(responseEvent, transaction);

    }
}
