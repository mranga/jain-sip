package gov.nist.javax.sip.header;

import java.text.ParseException;

import javax.sip.header.HeaderFactory;

import gov.nist.javax.sip.header.extensions.ReferencesHeader;
import gov.nist.javax.sip.header.ims.PAssertedServiceHeader;
import gov.nist.javax.sip.header.ims.PPreferredServiceHeader;


/**
 * Header factory extensions. 
 * 
 * @since 2.0
 *
 */
public interface HeaderFactoryExt extends HeaderFactory {
    /**
     * Create a P-Preferred-Service header.
     * @since 2.0
     * 
     * @return The newly created P-Preferred-Service Header.
     */
    public PPreferredServiceHeader createPPreferredServiceHeader();

    /**
     * Create an AssertedService Header
     * @since 2.0
     *
     * @return The newly created P-Asserted-Service Header.
     */
    public PAssertedServiceHeader createPAssertedServiceHeader();
   
   
    /**
     * Create a References header.
     * @since 2.0
     * 
     * @param callId -- the referenced call Id.
     * @param rel -- the rel parameter of the references header.
     * 
     * @return the newly created References header.
     */
    public ReferencesHeader createReferencesHeader(String callId, String rel) throws ParseException;
}
