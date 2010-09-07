package javax.sip.message;

import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentTypeHeader;

public interface Content {

    public abstract void setContent(Object content);
    
    public abstract Object getContent();
 
    public abstract ContentTypeHeader getContentTypeHeader();
    
    public abstract ContentDispositionHeader getContentDispositionHeader();

    public abstract String toString();
}
