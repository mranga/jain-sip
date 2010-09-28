package gov.nist.javax.sip;


import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.message.Response;

/**
 * Extension for ResponseEvent. 
 * 
 * @since v2.0
 */
public class ResponseEventExt extends ResponseEvent {
    /**
     * Constructor. This is just a stub for backwards compatibility.
     * 
     * @since 2.0
     * @param source - provider associated with this event.
     * @param clientTransaction - the client transaction
     * @param dialog - the dialog associated with this event.
     * @param response - the response associated with this event.
     */
    public ResponseEventExt(Object source, ClientTransactionExt clientTransaction, 
            Dialog dialog,  Response response) {
        super(source,clientTransaction,dialog,response);
      
    }

     /**
     * Return true if this is a forked response.
     * 
     * @return true if the response event is for a forked response.
     */
    public boolean isRetransmission() {
        return ((SIPResponse)getResponse()).isRetransmission();
    }
}
