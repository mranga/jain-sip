package gov.nist.javax.sip.message;

import java.text.ParseException;

import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;

/**
 *
 * @author jean.deruelle@gmail.com
 *
 */
public interface MessageExt extends Message {
    /*
     * Aliases (misnomers) for corresponding methods in javax.sip.message.Message
     * retained for backwards compatibility.
     */
    public ContentLengthHeader getContentLengthHeader();
    
    public ToHeader getToHeader();
    
    public CallIdHeader getCallIdHeader();
    
    public  CSeqHeader getCSeqHeader();
    
    public ContentTypeHeader getContentTypeHeader();
      
    
}
