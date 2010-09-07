package gov.nist.javax.sip;

import gov.nist.core.net.AddressResolver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collection;

import javax.sip.AccountManager;
import javax.sip.AuthenticationHelper;
import javax.sip.Dialog;
import javax.sip.SecureAccountManager;
import javax.sip.SipStack;
import javax.sip.header.HeaderFactory;
import javax.sip.header.JoinHeader;
import javax.sip.header.ReplacesHeader;

/**
 * SIP Stack extensions to be added to the next spec revision. Only these may be safely used in
 * the interim between now and the next release. SipStackImpl implements this interface.
 * 
 * The following new stack initialization flags are defined (not the gov.nist prefix will be
 * dropped when the spec is updated):
 * 
 * <ul>
 *<li>gov.nist.javax.sip.AUTOMATIC_DIALOG_ERROR_HANDLING
 *<li>gov.nist.javax.sip.IS_BACK_TO_BACK_USER_AGENT
 *<li>gov.nist.javax.sip.DELIVER_TERMINATED_EVENT_FOR_NULL_DIALOG
 *<li>gov.nist.javax.sip.MAX_FORK_TIME_SECONDS 
 * </ul>
 * @author M. Ranganathan
 * 
 */
public interface SipStackExt extends SipStack {

   
    /**
     * Set the address resolution interface. The address resolver allows you to register custom
     * lookup schemes ( for example DNS SRV lookup ) that are not directly supported by the JDK.
     *
     * @param addressResolver -- the address resolver to set.
     *
     * @since 2.0
     */
    public void setAddressResolver(AddressResolver addressResolver);
    
    
    /**
     * Creates and binds, if necessary, a TCP socket connected to the specified
     * destination address and port and then returns its local address. This is a
     * NIST-SIP only extension.
     *
     * @param dst the destination address that the socket would need to connect
     *            to.
     * @param dstPort the port number that the connection would be established
     * with.
     * @param localAddress the address that we would like to bind on
     * (null for the "any" address).
     * @param localPort the port that we'd like our socket to bind to (0 for a
     * random port).
     *
     * @return the SocketAddress that this handler would use when connecting to
     * the specified destination address and port.
     * 
     *
     * @throws IOException
     */
    public SocketAddress obtainLocalAddress(InetAddress dst, int dstPort,
                    InetAddress localAddress, int localPort)
        throws IOException;

}
