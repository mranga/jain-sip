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
package gov.nist.javax.sip.stack;

import gov.nist.core.InternalErrorHandler;
import gov.nist.core.LogWriter;
import gov.nist.core.NameValueList;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.Expires;
import gov.nist.javax.sip.header.RecordRoute;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.TimeStamp;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.SipException;
import javax.sip.Timeout;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionState;
import javax.sip.address.Hop;
import javax.sip.address.SipURI;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.TimeStampHeader;
import javax.sip.message.Request;

/*
 * Jeff Keyser -- initial. Daniel J. Martinez Manzano --Added support for TLS message channel.
 * Emil Ivov -- bug fixes. Chris Beardshear -- bug fix. Andreas Bystrom -- bug fixes. Matt Keller
 * (Motorolla) -- bug fix.
 */

/**
 * Represents a client transaction. Implements the following state machines. (From RFC 3261)
 * 
 * <pre>
 *                   
 *                    
 *                     
 *                      
 *                      
 *                      
 *                                                     |INVITE from TU
 *                                   Timer A fires     |INVITE sent
 *                                   Reset A,          V                      Timer B fires
 *                                   INVITE sent +-----------+                or Transport Err.
 *                                     +---------|           |---------------+inform TU
 *                                     |         |  Calling  |               |
 *                                     +--------&gt;|           |--------------&gt;|
 *                                               +-----------+ 2xx           |
 *                                                  |  |       2xx to TU     |
 *                                                  |  |1xx                  |
 *                          300-699 +---------------+  |1xx to TU            |
 *                         ACK sent |                  |                     |
 *                      resp. to TU |  1xx             V                     |
 *                                  |  1xx to TU  -----------+               |
 *                                  |  +---------|           |               |
 *                                  |  |         |Proceeding |--------------&gt;|
 *                                  |  +--------&gt;|           | 2xx           |
 *                                  |            +-----------+ 2xx to TU     |
 *                                  |       300-699    |                     |
 *                                  |       ACK sent,  |                     |
 *                                  |       resp. to TU|                     |
 *                                  |                  |                     |      NOTE:
 *                                  |  300-699         V                     |
 *                                  |  ACK sent  +-----------+Transport Err. |  transitions
 *                                  |  +---------|           |Inform TU      |  labeled with
 *                                  |  |         | Completed |--------------&gt;|  the event
 *                                  |  +--------&gt;|           |               |  over the action
 *                                  |            +-----------+               |  to take
 *                                  |              &circ;   |                     |
 *                                  |              |   | Timer D fires       |
 *                                  +--------------+   | -                   |
 *                                                     |                     |
 *                                                     V                     |
 *                                               +-----------+               |
 *                                               |           |               |
 *                                               | Terminated|&lt;--------------+
 *                                               |           |
 *                                               +-----------+
 *                      
 *                                       Figure 5: INVITE client transaction
 *                      
 *                      
 *                                                         |Request from TU
 *                                                         |send request
 *                                     Timer E             V
 *                                     send request  +-----------+
 *                                         +---------|           |-------------------+
 *                                         |         |  Trying   |  Timer F          |
 *                                         +--------&gt;|           |  or Transport Err.|
 *                                                   +-----------+  inform TU        |
 *                                      200-699         |  |                         |
 *                                      resp. to TU     |  |1xx                      |
 *                                      +---------------+  |resp. to TU              |
 *                                      |                  |                         |
 *                                      |   Timer E        V       Timer F           |
 *                                      |   send req +-----------+ or Transport Err. |
 *                                      |  +---------|           | inform TU         |
 *                                      |  |         |Proceeding |------------------&gt;|
 *                                      |  +--------&gt;|           |-----+             |
 *                                      |            +-----------+     |1xx          |
 *                                      |              |      &circ;        |resp to TU   |
 *                                      | 200-699      |      +--------+             |
 *                                      | resp. to TU  |                             |
 *                                      |              |                             |
 *                                      |              V                             |
 *                                      |            +-----------+                   |
 *                                      |            |           |                   |
 *                                      |            | Completed |                   |
 *                                      |            |           |                   |
 *                                      |            +-----------+                   |
 *                                      |              &circ;   |                         |
 *                                      |              |   | Timer K                 |
 *                                      +--------------+   | -                       |
 *                                                         |                         |
 *                                                         V                         |
 *                                   NOTE:           +-----------+                   |
 *                                                   |           |                   |
 *                               transitions         | Terminated|&lt;------------------+
 *                               labeled with        |           |
 *                               the event           +-----------+
 *                               over the action
 *                               to take
 *                      
 *                                       Figure 6: non-INVITE client transaction
 *                      
 *                      
 *                      
 *                      
 *                     
 *                    
 * </pre>
 * 
 * 
 * @author M. Ranganathan
 * 
 * @version 1.2 $Revision: 1.137 $ $Date: 2010/08/20 11:14:23 $
 */
public class SIPClientTransaction extends SIPTransaction implements ServerResponseInterface,
        javax.sip.ClientTransaction, gov.nist.javax.sip.ClientTransactionExt {

    // a SIP Client transaction may belong simultaneously to multiple
    // dialogs in the early state. These dialogs all have
    // the same call ID and same From tag but different to tags.
    
    //jeand : we don't keep the ref to the dialogs but only to their id to save on memory
    private Set<String> sipDialogs;

    private SIPRequest lastRequest;

    private int viaPort;

    private String viaHost;

    // Real ResponseInterface to pass messages to
    private transient ServerResponseInterface respondTo;

    // jeand: ref to the default dialog id to allow nullying the ref to the dialog quickly
    // and thus saving on mem
    private String defaultDialogId;
    private SIPDialog defaultDialog;

    private Hop nextHop;

    private boolean notifyOnRetransmit;

    private boolean timeoutIfStillInCallingState;

    private int callingStateTimeoutCount;
    
    private SIPStackTimerTask transactionTimer;
    
    // jeand/ avoid keeping the full Original Request in memory
	private String originalRequestFromTag;
	private String originalRequestCallId;
	private Event originalRequestEventHeader;
	private Contact originalRequestContact;
	private String originalRequestScheme;
	
	private Object transactionTimerLock = new Object();
	private AtomicBoolean timerKStarted = new AtomicBoolean(false);
	private boolean transactionTimerCancelled = false;
	
	

    public class TransactionTimer extends SIPStackTimerTask {

        public TransactionTimer() {

        }

        public void runTask() {
            SIPClientTransaction clientTransaction;
            SIPTransactionStack sipStack;
            clientTransaction = SIPClientTransaction.this;
            sipStack = clientTransaction.sipStack;

            // If the transaction has terminated,
            if (clientTransaction.isTerminated()) {                             

                try {
                	sipStack.getTimer().cancel(this);

                } catch (IllegalStateException ex) {
                    if (!sipStack.isAlive())
                        return;
                }

                cleanUpOnTerminated();

            } else {
                // If this transaction has not
                // terminated,
                // Fire the transaction timer.
                clientTransaction.fireTimer();
                                
            }

        }

    }
    
    class ExpiresTimerTask extends SIPStackTimerTask {
        
        public ExpiresTimerTask() {
            
        }

        @Override
        public void runTask() {
            SIPClientTransaction ct = SIPClientTransaction.this;
            SipProviderImpl provider = ct.getSipProvider();
     
            if (ct.getState() != TransactionState.TERMINATED ) {
                TimeoutEvent tte = new TimeoutEvent(SIPClientTransaction.this.getSipProvider(), 
                        SIPClientTransaction.this, Timeout.TRANSACTION);
                provider.handleEvent(tte, ct);
            } else {
                if ( SIPClientTransaction.this.getSIPStack().getStackLogger().isLoggingEnabled(LogWriter.TRACE_DEBUG) ) {
                    SIPClientTransaction.this.getSIPStack().getStackLogger().logDebug("state = " + ct.getState());
                }
            }
        }
        
    }

    /**
     * Creates a new client transaction.
     * 
     * @param newSIPStack Transaction stack this transaction belongs to.
     * @param newChannelToUse Channel to encapsulate.
     * @return the created client transaction.
     */
    protected SIPClientTransaction(SIPTransactionStack newSIPStack, MessageChannel newChannelToUse) {
        super(newSIPStack, newChannelToUse);
        // Create a random branch parameter for this transaction
        setBranch(Utils.getInstance().generateBranchId());
        this.messageProcessor = newChannelToUse.messageProcessor;
        this.setEncapsulatedChannel(newChannelToUse);
        this.notifyOnRetransmit = false;
        this.timeoutIfStillInCallingState = false;

        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug("Creating clientTransaction " + this);
            sipStack.getStackLogger().logStackTrace();
        }
        // this.startTransactionTimer();
        this.sipDialogs = new CopyOnWriteArraySet<String>();
    }

    /**
     * Sets the real ResponseInterface this transaction encapsulates.
     * 
     * @param newRespondTo ResponseInterface to send messages to.
     */
    public void setResponseInterface(ServerResponseInterface newRespondTo) {
        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    "Setting response interface for " + this + " to " + newRespondTo);
            if (newRespondTo == null) {
                sipStack.getStackLogger().logStackTrace();
                sipStack.getStackLogger().logDebug("WARNING -- setting to null!");
            }
        }

        respondTo = newRespondTo;

    }

    /**
     * Returns this transaction.
     */
    public MessageChannel getRequestChannel() {

        return this;

    }

    /**
     * Deterines if the message is a part of this transaction.
     * 
     * @param messageToTest Message to check if it is part of this transaction.
     * 
     * @return true if the message is part of this transaction, false if not.
     */
    public boolean isMessagePartOfTransaction(SIPMessage messageToTest) {

        // List of Via headers in the message to test
        Via topMostViaHeader = messageToTest.getTopmostVia();
        // Flags whether the select message is part of this transaction
        boolean transactionMatches;
        String messageBranch = topMostViaHeader.getBranch();
        boolean rfc3261Compliant = getBranch() != null
                && messageBranch != null
                && getBranch().toLowerCase().startsWith(
                        SIPConstants.BRANCH_MAGIC_COOKIE_LOWER_CASE)
                && messageBranch.toLowerCase().startsWith(
                        SIPConstants.BRANCH_MAGIC_COOKIE_LOWER_CASE);

        transactionMatches = false;
        if (TransactionState._COMPLETED == this.getInternalState()) {
            if (rfc3261Compliant) {
                transactionMatches = getBranch().equalsIgnoreCase(
                		topMostViaHeader.getBranch())
                        && getMethod().equals(messageToTest.getCSeq().getMethod());
            } else {
                transactionMatches = getBranch().equals(messageToTest.getTransactionId());
            }
        } else if (!isTerminated()) {
            if (rfc3261Compliant) {
                if (topMostViaHeader != null) {
                    // If the branch parameter is the
                    // same as this transaction and the method is the same,
                    if (getBranch().equalsIgnoreCase(topMostViaHeader.getBranch())) {
                        transactionMatches = getMethod().equals(
                                messageToTest.getCSeq().getMethod());

                    }
                }
            } else {
                // not RFC 3261 compliant.
                if (getBranch() != null) {
                    transactionMatches = getBranch().equalsIgnoreCase(
                            messageToTest.getTransactionId());
                } else {
                    transactionMatches = getOriginalRequest().getTransactionId()
                            .equalsIgnoreCase(messageToTest.getTransactionId());
                }

            }

        }
        return transactionMatches;

    }

    /**
     * Send a request message through this transaction and onto the client.
     * 
     * @param messageToSend Request to process and send.
     */
    public void sendMessage(SIPMessage messageToSend) throws IOException {

        try {
            // Message typecast as a request
            SIPRequest transactionRequest;

            transactionRequest = (SIPRequest) messageToSend;

            // Set the branch id for the top via header.
            Via topVia = (Via) transactionRequest.getTopmostVia();
            // Tack on a branch identifier to match responses.
            try {
                topVia.setBranch(getBranch());
            } catch (java.text.ParseException ex) {
            }

            if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug("Sending Message " + messageToSend);
                sipStack.getStackLogger().logDebug("TransactionState " + this.getState());
            }
            // If this is the first request for this transaction,
            if (TransactionState._PROCEEDING == getInternalState()
                    || TransactionState._CALLING == getInternalState()) {

                // If this is a TU-generated ACK request,
                if (transactionRequest.getMethod().equals(Request.ACK)) {

                    // Send directly to the underlying
                    // transport and close this transaction
                    if (isReliable()) {
                        this.setState(TransactionState._TERMINATED);
                    } else {
                        this.setState(TransactionState._COMPLETED);
                    }
                    cleanUpOnTimer();
                    // BUGBUG -- This suppresses sending the ACK uncomment this
                    // to
                    // test 4xx retransmission
                    // if (transactionRequest.getMethod() != Request.ACK)
                    super.sendMessage(transactionRequest);
                    return;

                }

            }
            try {

                // Send the message to the server
                lastRequest = transactionRequest;
                if (getInternalState() < 0) {
                    // Save this request as the one this transaction
                    // is handling
                    setOriginalRequest(transactionRequest);
                    // Change to trying/calling state
                    // Set state first to avoid race condition..

                    if (transactionRequest.getMethod().equals(Request.INVITE)) {
                        this.setState(TransactionState._CALLING);
                    } else if (transactionRequest.getMethod().equals(Request.ACK)) {
                        // Acks are never retransmitted.
                        this.setState(TransactionState._TERMINATED);
                        cleanUpOnTimer();
                    } else {
                        this.setState(TransactionState._TRYING);
                    }
                    if (!isReliable()) {
                        enableRetransmissionTimer();
                    }
                    if (isInviteTransaction()) {
                        enableTimeoutTimer(TIMER_B);
                    } else {
                        enableTimeoutTimer(TIMER_F);
                    }
                }
                // BUGBUG This supresses sending ACKS -- uncomment to test
                // 4xx retransmission.
                // if (transactionRequest.getMethod() != Request.ACK)
                super.sendMessage(transactionRequest);

            } catch (IOException e) {

                this.setState(TransactionState._TERMINATED);
                throw e;

            }
        } finally {
            this.isMapped = true;
            this.startTransactionTimer();

        }

    }

    /**
     * Process a new response message through this transaction. If necessary, this message will
     * also be passed onto the TU.
     * 
     * @param transactionResponse Response to process.
     * @param sourceChannel Channel that received this message.
     */
    public synchronized void processResponse(SIPResponse transactionResponse,
            MessageChannel sourceChannel, SIPDialog dialog) {

        // If the state has not yet been assigned then this is a
        // spurious response.

        if (getInternalState() < 0)
            return;

        // Ignore 1xx
        if ((TransactionState._COMPLETED == this.getInternalState() || TransactionState._TERMINATED == this
                .getInternalState())
                && transactionResponse.getStatusCode() / 100 == 1) {
            return;
        }

        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    "processing " + transactionResponse.getFirstLine() + "current state = "
                            + getState());
            sipStack.getStackLogger().logDebug("dialog = " + dialog);
        }

        this.lastResponse = transactionResponse;

        /*
         * JvB: this is now duplicate with code in the other processResponse
         * 
         * if (dialog != null && transactionResponse.getStatusCode() != 100 &&
         * (transactionResponse.getTo().getTag() != null || sipStack .isRfc2543Supported())) { //
         * add the route before you process the response. dialog.setLastResponse(this,
         * transactionResponse); this.setDialog(dialog, transactionResponse.getDialogId(false)); }
         */

        try {
            if (isInviteTransaction())
                inviteClientTransaction(transactionResponse, sourceChannel, dialog);
            else
                nonInviteClientTransaction(transactionResponse, sourceChannel, dialog);
        } catch (IOException ex) {
            if (sipStack.isLoggingEnabled())
                sipStack.getStackLogger().logException(ex);
            this.setState(TransactionState._TERMINATED);
            raiseErrorEvent(SIPTransactionErrorEvent.TRANSPORT_ERROR);
        }
    }

    /**
     * Implements the state machine for invite client transactions.
     * 
     * <pre>
     *                   
     *                    
     *                     
     *                      
     *                      
     *                                                         |Request from TU
     *                                                         |send request
     *                                     Timer E             V
     *                                     send request  +-----------+
     *                                         +---------|           |-------------------+
     *                                         |         |  Trying   |  Timer F          |
     *                                         +--------&gt;|           |  or Transport Err.|
     *                                                   +-----------+  inform TU        |
     *                                      200-699         |  |                         |
     *                                      resp. to TU     |  |1xx                      |
     *                                      +---------------+  |resp. to TU              |
     *                                      |                  |                         |
     *                                      |   Timer E        V       Timer F           |
     *                                      |   send req +-----------+ or Transport Err. |
     *                                      |  +---------|           | inform TU         |
     *                                      |  |         |Proceeding |------------------&gt;|
     *                                      |  +--------&gt;|           |-----+             |
     *                                      |            +-----------+     |1xx          |
     *                                      |              |      &circ;        |resp to TU   |
     *                                      | 200-699      |      +--------+             |
     *                                      | resp. to TU  |                             |
     *                                      |              |                             |
     *                                      |              V                             |
     *                                      |            +-----------+                   |
     *                                      |            |           |                   |
     *                                      |            | Completed |                   |
     *                                      |            |           |                   |
     *                                      |            +-----------+                   |
     *                                      |              &circ;   |                         |
     *                                      |              |   | Timer K                 |
     *                                      +--------------+   | -                       |
     *                                                         |                         |
     *                                                         V                         |
     *                                   NOTE:           +-----------+                   |
     *                                                   |           |                   |
     *                               transitions         | Terminated|&lt;------------------+
     *                               labeled with        |           |
     *                               the event           +-----------+
     *                               over the action
     *                               to take
     *                      
     *                                       Figure 6: non-INVITE client transaction
     *                      
     *                      
     *                     
     *                    
     * </pre>
     * 
     * @param transactionResponse -- transaction response received.
     * @param sourceChannel - source channel on which the response was received.
     */
    private void nonInviteClientTransaction(SIPResponse transactionResponse,
            MessageChannel sourceChannel, SIPDialog sipDialog) throws IOException {
        int statusCode = transactionResponse.getStatusCode();
        if (TransactionState._TRYING == this.getInternalState()) {
            if (statusCode / 100 == 1) {
                this.setState(TransactionState._PROCEEDING);
                enableRetransmissionTimer(MAXIMUM_RETRANSMISSION_TICK_COUNT);
                enableTimeoutTimer(TIMER_F);
                // According to RFC, the TU has to be informed on
                // this transition.
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    this.semRelease();
                }
            } else if (200 <= statusCode && statusCode <= 699) {
                if (!isReliable()) {
                    this.setState(TransactionState._COMPLETED);
                    scheduleTimerK(TIMER_K);        
                } else {
                    this.setState(TransactionState._TERMINATED);
                }
                // Send the response up to the TU.
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    this.semRelease();
                }
                if (isReliable()
                    && TransactionState._TERMINATED == getInternalState()) {
                    cleanUpOnTerminated();
                }
                cleanUpOnTimer();
            }
        } else if (TransactionState._PROCEEDING == this.getInternalState()) {
            if (statusCode / 100 == 1) {
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    this.semRelease();
                }
            } else if (200 <= statusCode && statusCode <= 699) {
                disableRetransmissionTimer();
                disableTimeoutTimer();
                if (!isReliable()) {
                    this.setState(TransactionState._COMPLETED);
                    scheduleTimerK(TIMER_K);
                } else {                    
                    this.setState(TransactionState._TERMINATED);
                }
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    this.semRelease();
                }
                if (isReliable()
                    && TransactionState._TERMINATED == getInternalState()) {
                    cleanUpOnTerminated();
                }
                cleanUpOnTimer();
            }
        } else {
            if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug(
                        " Not sending response to TU! " + getState());
            }
            this.semRelease();
        }
    }    

    // avoid re-scheduling the transaction timer every 500ms while we know we have to wait for TIMER_K * 500 ms
	private void scheduleTimerK(long time) {
		if(transactionTimer != null &&  timerKStarted.compareAndSet(false, true)) {
			synchronized (transactionTimerLock) {
				if(!transactionTimerCancelled) {
					sipStack.getTimer().cancel(transactionTimer);
					transactionTimer = null;
					if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                        sipStack.getStackLogger().logDebug("starting TransactionTimerK() : " + getTransactionId() + " time " + time);
                    }
					sipStack.getTimer().schedule(new SIPStackTimerTask () {                        	
		                
		                public void runTask() {
		                    if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                                sipStack.getStackLogger().logDebug("executing TransactionTimerJ() : " + getTransactionId());
                            }
		                    fireTimeoutTimer();                                  
		                    cleanUpOnTerminated();
		                }
		            }, time * BASE_TIMER_INTERVAL);
					transactionTimerCancelled =true;
				}
			}        	        	
        }            
	}

	/**
     * Implements the state machine for invite client transactions.
     * 
     * <pre>
     *                   
     *                    
     *                     
     *                      
     *                      
     *                                                     |INVITE from TU
     *                                   Timer A fires     |INVITE sent
     *                                   Reset A,          V                      Timer B fires
     *                                   INVITE sent +-----------+                or Transport Err.
     *                                     +---------|           |---------------+inform TU
     *                                     |         |  Calling  |               |
     *                                     +--------&gt;|           |--------------&gt;|
     *                                               +-----------+ 2xx           |
     *                                                  |  |       2xx to TU     |
     *                                                  |  |1xx                  |
     *                          300-699 +---------------+  |1xx to TU            |
     *                         ACK sent |                  |                     |
     *                      resp. to TU |  1xx             V                     |
     *                                  |  1xx to TU  -----------+               |
     *                                  |  +---------|           |               |
     *                                  |  |         |Proceeding |--------------&gt;|
     *                                  |  +--------&gt;|           | 2xx           |
     *                                  |            +-----------+ 2xx to TU     |
     *                                  |       300-699    |                     |
     *                                  |       ACK sent,  |                     |
     *                                  |       resp. to TU|                     |
     *                                  |                  |                     |      NOTE:
     *                                  |  300-699         V                     |
     *                                  |  ACK sent  +-----------+Transport Err. |  transitions
     *                                  |  +---------|           |Inform TU      |  labeled with
     *                                  |  |         | Completed |--------------&gt;|  the event
     *                                  |  +--------&gt;|           |               |  over the action
     *                                  |            +-----------+               |  to take
     *                                  |              &circ;   |                     |
     *                                  |              |   | Timer D fires       |
     *                                  +--------------+   | -                   |
     *                                                     |                     |
     *                                                     V                     |
     *                                               +-----------+               |
     *                                               |           |               |
     *                                               | Terminated|&lt;--------------+
     *                                               |           |
     *                                               +-----------+
     *                      
     *                      
     *                     
     *                    
     * </pre>
     * 
     * @param transactionResponse -- transaction response received.
     * @param sourceChannel - source channel on which the response was received.
     */

    private void inviteClientTransaction(SIPResponse transactionResponse,
            MessageChannel sourceChannel, SIPDialog dialog) throws IOException {
        int statusCode = transactionResponse.getStatusCode();
       
        if (TransactionState._TERMINATED == this.getInternalState()) {
            boolean ackAlreadySent = false;
           // if (dialog != null  && dialog.isAckSeen() && dialog.getLastAckSent() != null) 
            if ( dialog!= null && dialog.isAckSent(transactionResponse.getCSeq().getSeqNumber())) {
                if (dialog.getLastAckSent().getCSeq().getSeqNumber() == transactionResponse.getCSeq()
                        .getSeqNumber()
                        && transactionResponse.getFromTag().equals(
                                dialog.getLastAckSent().getFromTag())) {
                    // the last ack sent corresponded to this response
                    ackAlreadySent = true;
                }
            }
            // retransmit the ACK for this response.
            if (dialog!= null && ackAlreadySent
                    && transactionResponse.getCSeq().getMethod().equals(dialog.getMethod())) {
                try {
                    // Found the dialog - resend the ACK and
                    // dont pass up the null transaction
                    if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
                        sipStack.getStackLogger().logDebug("resending ACK");

                    dialog.resendAck();
                } catch (SipException ex) {
                    // What to do here ?? kill the dialog?
                }
            }

            this.semRelease();
            return;
        } else if (TransactionState._CALLING == this.getInternalState()) {
            if (statusCode / 100 == 2) {

                // JvB: do this ~before~ calling the application, to avoid
                // retransmissions
                // of the INVITE after app sends ACK
                disableRetransmissionTimer();
                disableTimeoutTimer();
                this.setState(TransactionState._TERMINATED);

                // 200 responses are always seen by TU.
                if (respondTo != null)
                    respondTo.processResponse(transactionResponse, this, dialog);
                else {
                    this.semRelease();
                }

            } else if (statusCode / 100 == 1) {
                disableRetransmissionTimer();
                disableTimeoutTimer();
                this.setState(TransactionState._PROCEEDING);

                if (respondTo != null)
                    respondTo.processResponse(transactionResponse, this, dialog);
                else {
                    this.semRelease();
                }

            } else if (300 <= statusCode && statusCode <= 699) {
                // Send back an ACK request

                try {
                    sendMessage((SIPRequest) createErrorAck());

                } catch (Exception ex) {
                    sipStack.getStackLogger().logError(
                            "Unexpected Exception sending ACK -- sending error AcK ", ex);

                }

                /*
                 * When in either the "Calling" or "Proceeding" states, reception of response with
                 * status code from 300-699 MUST cause the client transaction to transition to
                 * "Completed". The client transaction MUST pass the received response up to the
                 * TU, and the client transaction MUST generate an ACK request.
                 */

                if (this.getDialog() != null &&  ((SIPDialog)this.getDialog()).isBackToBackUserAgent()) {
                    ((SIPDialog) this.getDialog()).releaseAckSem();
                }

                if (!isReliable()) {
                    this.setState(TransactionState._COMPLETED);
                    enableTimeoutTimer(TIMER_D);
                } else {
                    // Proceed immediately to the TERMINATED state.
                    this.setState(TransactionState._TERMINATED);
                }
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, dialog);
                } else {
                    this.semRelease();
                }
                cleanUpOnTimer();
            }
        } else if (TransactionState._PROCEEDING == this.getInternalState()) {
            if (statusCode / 100 == 1) {
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, dialog);
                } else {
                    this.semRelease();
                }
            } else if (statusCode / 100 == 2) {
                this.setState(TransactionState._TERMINATED);
                if (respondTo != null) {
                    respondTo.processResponse(transactionResponse, this, dialog);
                } else {
                    this.semRelease();
                }

            } else if (300 <= statusCode && statusCode <= 699) {
                // Send back an ACK request
                try {
                    sendMessage((SIPRequest) createErrorAck());
                } catch (Exception ex) {
                    InternalErrorHandler.handleException(ex);
                }

                if (this.getDialog() != null) {
                    ((SIPDialog) this.getDialog()).releaseAckSem();
                }
                // JvB: update state before passing to app
                if (!isReliable()) {
                    this.setState(TransactionState._COMPLETED);
                    this.enableTimeoutTimer(TIMER_D);
                } else {
                    this.setState(TransactionState._TERMINATED);
                }
                cleanUpOnTimer();

                // Pass up to the TU for processing.
                if (respondTo != null)
                    respondTo.processResponse(transactionResponse, this, dialog);
                else {
                    this.semRelease();
                }

                // JvB: duplicate with line 874
                // if (!isReliable()) {
                // enableTimeoutTimer(TIMER_D);
                // }
            }
        } else if (TransactionState._COMPLETED == this.getInternalState()) {
            if (300 <= statusCode && statusCode <= 699) {
                // Send back an ACK request
                try {
                    sendMessage((SIPRequest) createErrorAck());
                } catch (Exception ex) {
                    InternalErrorHandler.handleException(ex);
                } finally {
                    this.semRelease();
                }
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.ClientTransaction#sendRequest()
     */
    public void sendRequest() throws SipException {
        SIPRequest sipRequest = this.getOriginalRequest();

        if (this.getInternalState() >= 0)
            throw new SipException("Request already sent");

        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug("sendRequest() " + sipRequest);
        }

        try {
            sipRequest.checkHeaders();
        } catch (ParseException ex) {
        	if (sipStack.isLoggingEnabled())
        		sipStack.getStackLogger().logError("missing required header");
            throw new SipException(ex.getMessage());
        }

        if (getMethod().equals(Request.SUBSCRIBE)
                && sipRequest.getHeader(ExpiresHeader.NAME) == null) {
            /*
             * If no "Expires" header is present in a SUBSCRIBE request, the implied default is
             * defined by the event package being used.
             * 
             */
        	if (sipStack.isLoggingEnabled())
        		sipStack.getStackLogger().logWarning(
                    "Expires header missing in outgoing subscribe --"
                            + " Notifier will assume implied value on event package");
        }
        try {
            /*
             * This check is removed because it causes problems for load balancers ( See issue
             * 136) reported by Raghav Ramesh ( BT )
             * 
             */
            if (this.getMethod().equals(Request.CANCEL)
                    && sipStack.isCancelClientTransactionChecked()) {
                SIPClientTransaction ct = (SIPClientTransaction) sipStack.findCancelTransaction(
                        this.getOriginalRequest(), false);
                if (ct == null) {
                    /*
                     * If the original request has generated a final response, the CANCEL SHOULD
                     * NOT be sent, as it is an effective no-op, since CANCEL has no effect on
                     * requests that have already generated a final response.
                     */
                    throw new SipException("Could not find original tx to cancel. RFC 3261 9.1");
                } else if (ct.getInternalState() < 0) {
                    throw new SipException(
                            "State is null no provisional response yet -- cannot cancel RFC 3261 9.1");
                } else if (!ct.getMethod().equals(Request.INVITE)) {
                    throw new SipException("Cannot cancel non-invite requests RFC 3261 9.1");
                }
            } else if (this.getMethod().equals(Request.BYE)
                    || this.getMethod().equals(Request.NOTIFY)) {
                SIPDialog dialog = sipStack.getDialog(this.getOriginalRequest()
                        .getDialogId(false));
                // I want to behave like a user agent so send the BYE using the
                // Dialog
                if (this.getSipProvider().isAutomaticDialogSupportEnabled() && dialog != null) {
                    throw new SipException(
                            "Dialog is present and AutomaticDialogSupport is enabled for "
                                    + " the provider -- Send the Request using the Dialog.sendRequest(transaction)");
                }
            }
            // Only map this after the fist request is sent out.
            if (this.getMethod().equals(Request.INVITE)) {
                SIPDialog dialog = this.getDefaultDialog();

                if (dialog != null && dialog.isBackToBackUserAgent()) {
                    // Block sending re-INVITE till we see the ACK.
                    if ( ! dialog.takeAckSem() ) {
                        throw new SipException ("Failed to take ACK semaphore");
                    }

                }
            }
            this.isMapped = true;
         // Time extracted from the Expires header.
            int expiresTime = -1;

           if ( sipRequest.getHeader(ExpiresHeader.NAME) != null ) {
                Expires expires = (Expires) sipRequest.getHeader(ExpiresHeader.NAME);
                expiresTime = expires.getExpires();
            } 
            // This is a User Agent. The user has specified an Expires time. Start a timer
            // which will check if the tx is terminated by that time.
            if ( this.getDefaultDialog() != null  &&  getMethod().equals(Request.INVITE) &&
                    expiresTime != -1 && expiresTimerTask == null ) {
                this.expiresTimerTask = new ExpiresTimerTask();
                sipStack.getTimer().schedule(expiresTimerTask, expiresTime * 1000);
                
            }
            this.sendMessage(sipRequest);
            

        } catch (IOException ex) {
            this.setState(TransactionState._TERMINATED);
            if ( this.expiresTimerTask != null ) {
                sipStack.getTimer().cancel(this.expiresTimerTask);
            }
            throw new SipException(
                    ex.getMessage() == null ? "IO Error sending request" : ex.getMessage(),
                    ex);
        }

    }

    /**
     * Called by the transaction stack when a retransmission timer fires.
     */
    protected void fireRetransmissionTimer() {

        try {

            // Resend the last request sent
            if (this.getInternalState() < 0 || !this.isMapped)
                return;

            boolean inv = isInviteTransaction();
            int s = this.getInternalState();

            // JvB: INVITE CTs only retransmit in CALLING, non-INVITE in both TRYING and
            // PROCEEDING
            // Bug-fix for non-INVITE transactions not retransmitted when 1xx response received
            if ((inv && TransactionState._CALLING == s)
                    || (!inv && (TransactionState._TRYING == s || TransactionState._PROCEEDING == s))) {
                // If the retransmission filter is disabled then
                // retransmission of the INVITE is the application
                // responsibility.

                if (lastRequest != null) {
                    if (sipStack.generateTimeStampHeader
                            && lastRequest.getHeader(TimeStampHeader.NAME) != null) {
                        long milisec = System.currentTimeMillis();
                        TimeStamp timeStamp = new TimeStamp();
                        try {
                            timeStamp.setTimeStamp(milisec);
                        } catch (InvalidArgumentException ex) {
                            InternalErrorHandler.handleException(ex);
                        }
                        lastRequest.setHeader(timeStamp);
                    }
                    super.sendMessage(lastRequest);
                    if (this.notifyOnRetransmit) {
                        TimeoutEvent txTimeout = new TimeoutEvent(this.getSipProvider(), this,
                                Timeout.RETRANSMIT);
                        this.getSipProvider().handleEvent(txTimeout, this);
                    }
                    if (this.timeoutIfStillInCallingState
                            && this.getInternalState() == TransactionState._CALLING) {
                        this.callingStateTimeoutCount--;
                        if (callingStateTimeoutCount == 0) {
                            TimeoutEvent timeoutEvent = new TimeoutEvent(this.getSipProvider(),
                                    this, Timeout.RETRANSMIT);
                            this.getSipProvider().handleEvent(timeoutEvent, this);
                            this.timeoutIfStillInCallingState = false;
                        }

                    }
                }

            }
        } catch (IOException e) {
            this.raiseIOExceptionEvent();
            raiseErrorEvent(SIPTransactionErrorEvent.TRANSPORT_ERROR);
        }

    }

    /**
     * Called by the transaction stack when a timeout timer fires.
     */
    protected void fireTimeoutTimer() {

        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
            sipStack.getStackLogger().logDebug("fireTimeoutTimer " + this);

        SIPDialog dialog = (SIPDialog) this.getDialog();
        if (TransactionState._CALLING == this.getInternalState()
                || TransactionState._TRYING == this.getInternalState()
                || TransactionState._PROCEEDING == this.getInternalState()) {
            // Timeout occured. If this is asociated with a transaction
            // creation then kill the dialog.
            if (dialog != null
                    && (dialog.getState() == null || dialog.getState() == DialogState.EARLY)) {
                if (((SIPTransactionStack) getSIPStack()).isDialogCreated(this.getMethod())) {
                    // If this is a re-invite we do not delete the dialog even
                    // if the
                    // reinvite times out. Else
                    // terminate the enclosing dialog.
                    dialog.delete();
                }
            } else if (dialog != null) {
                // Guard against the case of BYE time out.

                if (this.getMethod().equalsIgnoreCase(Request.BYE)
                        && dialog.isTerminatedOnBye()) {
                    // Terminate the associated dialog on BYE Timeout.
                    dialog.delete();
                }
            }
        }
        if (TransactionState._COMPLETED != this.getInternalState() && TransactionState._TERMINATED != this.getInternalState()) {
            raiseErrorEvent(SIPTransactionErrorEvent.TIMEOUT_ERROR);
            // Got a timeout error on a cancel.
            if (this.getMethod().equalsIgnoreCase(Request.CANCEL)) {
                SIPClientTransaction inviteTx = (SIPClientTransaction) this.getOriginalRequest()
                        .getInviteTransaction();
                if (inviteTx != null
                        && ((inviteTx.getInternalState() == TransactionState._CALLING || inviteTx
                                .getInternalState() == TransactionState._PROCEEDING))
                        && inviteTx.getDialog() != null) {
                    /*
                     * A proxy server should have started TIMER C and take care of the Termination
                     * using transaction.terminate() by itself (i.e. this is not the job of the
                     * stack at this point but we do it to be nice.
                     */
                    inviteTx.setState(TransactionState._TERMINATED);

                }
            }

        } else {
            this.setState(TransactionState._TERMINATED);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.ClientTransaction#createCancel()
     */
    public Request createCancel() throws SipException {
        SIPRequest originalRequest = this.getOriginalRequest();
        if (originalRequest == null)
            throw new SipException("Bad state " + getState());
        if (!originalRequest.getMethod().equals(Request.INVITE))
            throw new SipException("Only INIVTE may be cancelled");

        if (originalRequest.getMethod().equalsIgnoreCase(Request.ACK))
            throw new SipException("Cannot Cancel ACK!");
        else {
            SIPRequest cancelRequest = originalRequest.createCancelRequest();
            cancelRequest.setInviteTransaction(this);
            return cancelRequest;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.sip.ClientTransaction#createAck()
     */
    public Request createAck() throws SipException {
        SIPRequest originalRequest = this.getOriginalRequest();
        if (originalRequest == null)
            throw new SipException("bad state " + getState());
        if (getMethod().equalsIgnoreCase(Request.ACK)) {
            throw new SipException("Cannot ACK an ACK!");
        } else if (lastResponse == null) {
            throw new SipException("bad Transaction state");
        } else if (lastResponse.getStatusCode() < 200) {
            if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug("lastResponse = " + lastResponse);
            }
            throw new SipException("Cannot ACK a provisional response!");
        }
        SIPRequest ackRequest = originalRequest.createAckRequest((To) lastResponse.getTo());
        // Pull the record route headers from the last reesponse.
        RecordRouteList recordRouteList = lastResponse.getRecordRouteHeaders();
        if (recordRouteList == null) {
            // If the record route list is null then we can
            // construct the ACK from the specified contact header.
            // Note the 3xx check here because 3xx is a redirect.
            // The contact header for the 3xx is the redirected
            // location so we cannot use that to construct the
            // request URI.
            if (lastResponse.getContactHeaders() != null
                    && lastResponse.getStatusCode() / 100 != 3) {
                Contact contact = (Contact) lastResponse.getContactHeaders().getFirst();
                javax.sip.address.URI uri = (javax.sip.address.URI) contact.getAddress().getURI()
                        .clone();
                ackRequest.setRequestURI(uri);
            }
            return ackRequest;
        }

        ackRequest.removeHeader(RouteHeader.NAME);
        RouteList routeList = new RouteList();
        // start at the end of the list and walk backwards
        ListIterator<RecordRoute> li = recordRouteList.listIterator(recordRouteList.size());
        while (li.hasPrevious()) {
            RecordRoute rr = (RecordRoute) li.previous();

            Route route = new Route();
            route.setAddress((AddressImpl) ((AddressImpl) rr.getAddress()).clone());
            route.setParameters((NameValueList) rr.getParameters().clone());
            routeList.add(route);
        }

        Contact contact = null;
        if (lastResponse.getContactHeaders() != null) {
            contact = (Contact) lastResponse.getContactHeaders().getFirst();
        }

        if (!((SipURI) ((Route) routeList.getFirst()).getAddress().getURI()).hasLrParam()) {

            // Contact may not yet be there (bug reported by Andreas B).

            Route route = null;
            if (contact != null) {
                route = new Route();
                route.setAddress((AddressImpl) ((AddressImpl) (contact.getAddress())).clone());
            }

            Route firstRoute = (Route) routeList.getFirst();
            routeList.removeFirst();
            javax.sip.address.URI uri = firstRoute.getAddress().getURI();
            ackRequest.setRequestURI(uri);

            if (route != null)
                routeList.add(route);

            ackRequest.addHeader(routeList);
        } else {
            if (contact != null) {
                javax.sip.address.URI uri = (javax.sip.address.URI) contact.getAddress().getURI()
                        .clone();
                ackRequest.setRequestURI(uri);
                ackRequest.addHeader(routeList);
            }
        }
        return ackRequest;

    }

    /*
     * Creates an ACK for an error response, according to RFC3261 section 17.1.1.3
     * 
     * Note that this is different from an ACK for 2xx
     */
    private final Request createErrorAck() throws SipException, ParseException {
        SIPRequest originalRequest = this.getOriginalRequest();
        if (originalRequest == null)
            throw new SipException("bad state " + getState());
        if (!getMethod().equals(Request.INVITE)) {
            throw new SipException("Can only ACK an INVITE!");
        } else if (lastResponse == null) {
            throw new SipException("bad Transaction state");
        } else if (lastResponse.getStatusCode() < 200) {
            if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
                sipStack.getStackLogger().logDebug("lastResponse = " + lastResponse);
            }
            throw new SipException("Cannot ACK a provisional response!");
        }
        return originalRequest.createErrorAck((To) lastResponse.getTo());
    }

    /**
     * Set the port of the recipient.
     */
    public void setViaPort(int port) {
        this.viaPort = port;
    }

    /**
     * Set the port of the recipient.
     */
    public void setViaHost(String host) {
        this.viaHost = host;
    }

    /**
     * Get the port of the recipient.
     */
    public int getViaPort() {
        return this.viaPort;
    }

    /**
     * Get the host of the recipient.
     */
    public String getViaHost() {
        return this.viaHost;
    }

    /**
     * get the via header for an outgoing request.
     */
    public Via getOutgoingViaHeader() {
        return this.getMessageProcessor().getViaHeader();
    }

    /**
     * This is called by the stack after a non-invite client transaction goes to completed state.
     */
    public void clearState() {
        // reduce the state to minimum
        // This assumes that the application will not need
        // to access the request once the transaction is
        // completed.
        // TODO -- revisit this - results in a null pointer
        // occuring occasionally.
        // this.lastRequest = null;
        // this.originalRequest = null;
        // this.lastResponse = null;
    }

    /**
     * Sets a timeout after which the connection is closed (provided the server does not use the
     * connection for outgoing requests in this time period) and calls the superclass to set
     * state.
     */
    public void setState(int newState) {
        // Set this timer for connection caching
        // of incoming connections.
        if (newState == TransactionState._TERMINATED && this.isReliable()
                && (!getSIPStack().cacheClientConnections)) {
            // Set a time after which the connection
            // is closed.
            this.collectionTime = TIMER_J;

        }
        if (super.getInternalState() != TransactionState._COMPLETED
                && (newState == TransactionState._COMPLETED || newState == TransactionState._TERMINATED)) {
            sipStack.decrementActiveClientTransactionCount();
        }
        super.setState(newState);
    }

    /**
     * Start the timer task.
     */
    protected  void startTransactionTimer() {
        if (this.transactionTimerStarted.compareAndSet(false, true)) {        	
	        if ( sipStack.getTimer() != null ) {
	        	synchronized (transactionTimerLock) {
	        		if(!transactionTimerCancelled) {
	        			transactionTimer = new TransactionTimer();
	        			sipStack.getTimer().scheduleWithFixedDelay(transactionTimer, BASE_TIMER_INTERVAL, BASE_TIMER_INTERVAL);
	        		}
				}	        	
	        }
        }
    }

    /*
     * Terminate a transaction. This marks the tx as terminated The tx scanner will run and remove
     * the tx. (non-Javadoc)
     * 
     * @see javax.sip.Transaction#terminate()
     */
    public void terminate() throws ObjectInUseException {
        this.setState(TransactionState._TERMINATED);
     
    }
    
    /**
     * Stop the ExPIRES timer if it is running.
     */
    public void stopExpiresTimer() {
        if ( this.expiresTimerTask != null ) {
            sipStack.getTimer().cancel(this.expiresTimerTask);
            this.expiresTimerTask = null;
        }
    }

    /**
     * Check if the From tag of the response matches the from tag of the original message. A
     * Response with a tag mismatch should be dropped if a Dialog has been created for the
     * original request.
     * 
     * @param sipResponse the response to check.
     * @return true if the check passes.
     */
    public boolean checkFromTag(SIPResponse sipResponse) {
        String originalFromTag = getOriginalRequestFromTag();
        if (this.defaultDialog != null) {
            if (originalFromTag == null ^ sipResponse.getFrom().getTag() == null) {
            	if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
            		sipStack.getStackLogger().logDebug("From tag mismatch -- dropping response");
                return false;
            }
            if (originalFromTag != null
                    && !originalFromTag.equalsIgnoreCase(sipResponse.getFrom().getTag())) {
            	if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
            		sipStack.getStackLogger().logDebug("From tag mismatch -- dropping response");
                return false;
            }
        }
        return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.nist.javax.sip.stack.ServerResponseInterface#processResponse(gov.nist.javax.sip.message.SIPResponse,
     *      gov.nist.javax.sip.stack.MessageChannel)
     */
    public void processResponse(SIPResponse sipResponse, MessageChannel incomingChannel) {

        // If a dialog has already been created for this response,
        // pass it up.
        SIPDialog dialog = null;
        String method = sipResponse.getCSeq().getMethod();
        String dialogId = sipResponse.getDialogId(false);
        if (method.equals(Request.CANCEL) && lastRequest != null) {
            // JvB for CANCEL: use invite CT in CANCEL request to get dialog
            // (instead of stripping tag)
            SIPClientTransaction ict = (SIPClientTransaction) lastRequest.getInviteTransaction();
            if (ict != null) {
                dialog = ict.defaultDialog;
            }
        } else {
            dialog = this.getDialog(dialogId);
        }

        // JvB: Check all conditions required for creating a new Dialog
        if (dialog == null) {
            int code = sipResponse.getStatusCode();
            if ((code > 100 && code < 300)
            /* skip 100 (may have a to tag */
            && (sipResponse.getToTag() != null || sipStack.isRfc2543Supported())
                    && sipStack.isDialogCreated(method)) {

                /*
                 * Dialog cannot be found for the response. This must be a forked response. no
                 * dialog assigned to this response but a default dialog has been assigned. Note
                 * that if automatic dialog support is configured then a default dialog is always
                 * created.
                 */

                synchronized (this) {
                    /*
                     * We need synchronization here because two responses may compete for the
                     * default dialog simultaneously
                     */
                    if (defaultDialog != null) {
                        if (sipResponse.getFromTag() != null) {
                            String defaultDialogId = defaultDialog.getDialogId();
                            if (defaultDialog.getLastResponseMethod() == null
                                    || (method.equals(Request.SUBSCRIBE)
                                            && defaultDialog.getLastResponseMethod().equals(
                                                    Request.NOTIFY) && defaultDialogId
                                            .equals(dialogId))) {
                                // The default dialog has not been claimed yet.
                                defaultDialog.setLastResponse(this, sipResponse);
                                dialog = defaultDialog;
                            } else {
                                /*
                                 * check if we have created one previously (happens in the case of
                                 * REINVITE processing. JvB: should not happen, this.defaultDialog
                                 * should then get set in Dialog#sendRequest line 1662
                                 */

                                dialog = sipStack.getDialog(dialogId);
                                if (dialog == null) {
                                    if (defaultDialog.isAssigned()) {
                                        /*
                                         * Nop we dont have one. so go ahead and allocate a new
                                         * one.
                                         */
                                        dialog = sipStack.createDialog(this, sipResponse);

                                    }
                                }

                            }
                            if ( dialog != null ) {
                                this.setDialog(dialog, dialog.getDialogId());
                            } else {
                                sipStack.getStackLogger().logError("dialog is unexpectedly null",new NullPointerException());
                            }
                        } else {
                            throw new RuntimeException("Response without from-tag");
                        }
                    } else {
                        // Need to create a new Dialog, this becomes default
                        // JvB: not sure if this ever gets executed
                        if (sipStack.isAutomaticDialogSupportEnabled) {
                            dialog = sipStack.createDialog(this, sipResponse);
                            this.setDialog(dialog, dialog.getDialogId());
                        }
                    }
                } // synchronized
            } else {
                dialog = defaultDialog;
            }
        } else {
            dialog.setLastResponse(this, sipResponse);
        }
        this.processResponse(sipResponse, incomingChannel, dialog);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.nist.javax.sip.stack.SIPTransaction#getDialog()
     */
    public  Dialog getDialog() {
        // This is for backwards compatibility.
        Dialog retval = null;
        // get it in a local variable because the last response can be nullified and the if condition
        // can throw NPE
        SIPResponse localLastResponse = this.lastResponse;
        if(localLastResponse != null && localLastResponse.getFromTag() != null
                && localLastResponse.getToTag() != null
                && localLastResponse.getStatusCode() != 100) {
            String dialogId = localLastResponse.getDialogId(false);
            retval = (Dialog) getDialog(dialogId);
        }

        if (retval == null) {
            retval = (Dialog) this.getDefaultDialog();

        }
        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    " sipDialogs =  " + sipDialogs + " default dialog " + this.getDefaultDialog()
                            + " retval " + retval);
        }
        return retval;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.nist.javax.sip.stack.SIPTransaction#setDialog(gov.nist.javax.sip.stack.SIPDialog,
     *      gov.nist.javax.sip.message.SIPMessage)
     */
    public SIPDialog getDialog(String dialogId) {
    	SIPDialog retval = null;
    	if(sipDialogs != null && sipDialogs.contains(dialogId)) {
    		retval = this.sipStack.getDialog(dialogId);
    		if(retval == null) {
    			retval = this.sipStack.getEarlyDialog(dialogId);
    		}
    	}
        return retval;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.nist.javax.sip.stack.SIPTransaction#setDialog(gov.nist.javax.sip.stack.SIPDialog,
     *      gov.nist.javax.sip.message.SIPMessage)
     */
    public void setDialog(SIPDialog sipDialog, String dialogId) {
        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
            sipStack.getStackLogger().logDebug(
                    "setDialog: " + dialogId + " sipDialog = " + sipDialog);

        if (sipDialog == null) {
        	if (sipStack.isLoggingEnabled(LogWriter.TRACE_ERROR))
        		sipStack.getStackLogger().logError("NULL DIALOG!!");
            throw new NullPointerException("bad dialog null");
        }
        if (this.defaultDialog == null) {
            this.defaultDialog = sipDialog;
            if ( this.getMethod().equals(Request.INVITE) && this.getSIPStack().getMaxForkTime() != 0) {
                this.getSIPStack().addForkedClientTransaction(this);
            }
        }
        if (dialogId != null && sipDialog.getDialogId() != null && sipDialogs != null) {
            this.sipDialogs.add(dialogId);
        }

    }

    public SIPDialog getDefaultDialog() {
    	SIPDialog dialog = defaultDialog;
    	// jeand if the dialog has been nullified then get the dialog from the saved dialog id
    	if(dialog == null && defaultDialogId != null) {
    		dialog = this.sipStack.getDialog(defaultDialogId);    		
    	}
    	return dialog;
    }

    /**
     * Set the next hop ( if it has already been computed).
     * 
     * @param hop -- the hop that has been previously computed.
     */
    public void setNextHop(Hop hop) {
        this.nextHop = hop;

    }

    /**
     * Reeturn the previously computed next hop (avoid computing it twice).
     * 
     * @return -- next hop previously computed.
     */
    public Hop getNextHop() {
        return nextHop;
    }

    /**
     * Set this flag if you want your Listener to get Timeout.RETRANSMIT notifications each time a
     * retransmission occurs.
     * 
     * @param notifyOnRetransmit the notifyOnRetransmit to set
     */
    public void setNotifyOnRetransmit(boolean notifyOnRetransmit) {
        this.notifyOnRetransmit = notifyOnRetransmit;
    }

    /**
     * @return the notifyOnRetransmit
     */
    public boolean isNotifyOnRetransmit() {
        return notifyOnRetransmit;
    }

    public void alertIfStillInCallingStateBy(int count) {
        this.timeoutIfStillInCallingState = true;
        this.callingStateTimeoutCount = count;
    }

    // jeand method use to cleanup eagerly all structures that won't be needed anymore once the tx passed in the COMPLETED state
    protected void cleanUpOnTimer() {
    	if(isReleaseReferences()) {
	    	if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
	            sipStack.getStackLogger().logDebug("cleanupOnTimer: "
	                    + getTransactionId());
	        }       
	    	// we release the ref to the dialog asap and just keep the id of the dialog to look it up in the dialog table
	    	if(defaultDialog != null) {
		    	String dialogId = defaultDialog.getDialogId();
		    	// we nullify the ref only if it can be find in the dialog table (not always true if the dialog is in null state, check challenge unittest of the testsuite)
		    	if(dialogId != null && sipStack.getDialog(dialogId) != null) {
		    		defaultDialogId = dialogId;
		    		defaultDialog = null;	    		
		    	}
	    	}
	    	if(originalRequest != null) {
		    	originalRequest.setTransaction(null);
		    	originalRequest.setInviteTransaction(null);
		    	originalRequest.cleanUp();
		    	// we keep the request in a byte array to be able to recreate it
		    	// no matter what to keep API backward compatibility
		    	if(originalRequestBytes == null) {
		    	    originalRequestBytes = originalRequest.encodeAsBytes(this.getTransport());   
		    	}		    	
		    	if(!getMethod().equalsIgnoreCase(Request.INVITE) && !getMethod().equalsIgnoreCase(Request.CANCEL)) {	    			
	    			originalRequestFromTag = originalRequest.getFromTag();
	    			originalRequestCallId = originalRequest.getCallId().getCallId();
	    			originalRequestEventHeader = (Event) originalRequest.getHeader("Event");
	    			originalRequestContact = originalRequest.getContactHeader();
	    			originalRequestScheme = originalRequest.getRequestURI().getScheme();
	    			originalRequest = null;    			
	    		}  
	    	}
	    	// for subscribe Tx we need to keep the last response longer to be able to create notify from dialog
	    	if(!getMethod().equalsIgnoreCase(Request.SUBSCRIBE)) {
	    		lastResponse = null;
	    	}    	 
	    	lastRequest = null;
    	}
	}
    
    //jeand : cleanup method to clear the state of the tx once it has been removed from the stack
    @Override    
    public void cleanUp() {
    	if(isReleaseReferences()) {
	    	// release the connection associated with this transaction.
	        if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
	            sipStack.getStackLogger().logDebug("cleanup : "
	                    + getTransactionId());
	        }        
	    	if(defaultDialog != null) {
	    		defaultDialogId = defaultDialog.getDialogId();
	    		defaultDialog = null;
	    	}
	    	// we keep the request in a byte array to be able to recreate it
            // no matter what to keep API backward compatibility
	    	if(originalRequest != null && originalRequestBytes == null) {
	    	    originalRequestBytes = originalRequest.encodeAsBytes(this.getTransport());
	    	}
		    originalRequest = null;		    
	    	cleanUpOnTimer();
	    	// commented out because the application can hold on a ref to the tx
	    	// after it has been removed from the stack
	    	// and want to get the request or branch from it
//			originalRequestBytes = null;
//		    originalRequestBranch = null;
		    originalRequestCallId = null;
		    originalRequestEventHeader = null;
		    originalRequestFromTag = null;
		    originalRequestContact = null;
		    originalRequestScheme = null;
	    	if(sipDialogs != null) {
		    	sipDialogs.clear();	    	
	    	}
	    	respondTo = null;
	    	transactionTimer = null;
	    	lastResponse = null;
	    	transactionTimerLock = null;
	    	transactionTimerStarted = null;
	    	timerKStarted = null;    	
    	}
    }
    
    // jeand cleanup called after the ctx timer or the timer k has fired
    protected void cleanUpOnTerminated() {
    	if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG)) {
            sipStack.getStackLogger().logDebug(
                    "removing  = " + this + " isReliable "
                            + isReliable());
        }  
    	if(isReleaseReferences()) {
			
			if(originalRequest == null && originalRequestBytes != null) {
	        	try {
					originalRequest = (SIPRequest) sipStack.getMessageParserFactory().createMessageParser(sipStack).parseSIPMessage(originalRequestBytes, true, false, null);
//					originalRequestBytes = null;
				} catch (ParseException e) {
					sipStack.getStackLogger().logError("message " + originalRequestBytes + " could not be reparsed !");
				}
			}   
    	}
	       
    	sipStack.removeTransaction(this);           
	
        // Client transaction terminated. Kill connection if
        // this is a TCP after the linger timer has expired.
        // The linger timer is needed to allow any pending requests to
        // return responses.                
        if ((!sipStack.cacheClientConnections) && isReliable()) {

            int newUseCount = --getMessageChannel().useCount;
            if (newUseCount <= 0) {
                // Let the connection linger for a while and then close
                // it.
            	SIPStackTimerTask myTimer = new LingerTimer();
                sipStack.getTimer().schedule(myTimer,
                        SIPTransactionStack.CONNECTION_LINGER_TIME * 1000);
            }

        } else {
            // Cache the client connections so dont close the
            // connection. This keeps the connection open permanently
            // until the client disconnects.
            if (sipStack.isLoggingEnabled() && isReliable()) {
               	int useCount = getMessageChannel().useCount;
               	if (sipStack.isLoggingEnabled(LogWriter.TRACE_DEBUG))
               		sipStack.getStackLogger().logDebug("Client Use Count = " + useCount);
            }                    
            // Let the connection linger for a while and then close
            // it.
            if(((SipStackImpl)getSIPStack()).isReEntrantListener() && isReleaseReferences()) {
            	cleanUp();     
            } 
            // Commented out for Issue 298 : not to break backward compatibility
            // this piece of code was not present before aggressive optimizations
            // see sipx-stable-420 branch
//            else {
//            	SIPStackTimerTask myTimer = new LingerTimer();
//                sipStack.getTimer().schedule(myTimer,
//                        SIPTransactionStack.CONNECTION_LINGER_TIME * 1000);
//            }
        }
    	
	}

	/**
	 * @return the originalRequestFromTag
	 */
	public String getOriginalRequestFromTag() {
		if(originalRequest == null) {
			return originalRequestFromTag;
		}
		return originalRequest.getFromTag();
	}
	
	/**
	 * @return the originalRequestFromTag
	 */
	public String getOriginalRequestCallId() {
		if(originalRequest == null) {
			return originalRequestCallId;
		}
		return originalRequest.getCallId().getCallId();
	}
	
	/**
	 * @return the originalRequestFromTag
	 */
	public Event getOriginalRequestEvent() {
		if(originalRequest == null) {
			return originalRequestEventHeader;
		}
		return (Event) originalRequest.getHeader(EventHeader.NAME);
	}
	
	/**
	 * @return the originalRequestFromTag
	 */
	public Contact getOriginalRequestContact() {
		if(originalRequest == null) {
			return originalRequestContact;
		}
		return originalRequest.getContactHeader();
	}
	
	/**
	 * @return the originalRequestFromTag
	 */
	public String getOriginalRequestScheme() {
		if(originalRequest == null) {
			return originalRequestScheme;
		}
		return originalRequest.getRequestURI().getScheme();
	}
   
}
