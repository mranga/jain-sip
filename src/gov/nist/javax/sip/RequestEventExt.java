package gov.nist.javax.sip;

import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.message.Request;


/**
 * Extension of the RequestEvent.
 * A place holder for future extension and backwards
 * compatiblity.
 */

public class RequestEventExt extends RequestEvent {
    public RequestEventExt(Object source, ListeningPointExt listeningPoint, ServerTransaction serverTransaction, Dialog dialog, Request request) {
        	super(source,listeningPoint,serverTransaction,dialog,request);
    }
}
