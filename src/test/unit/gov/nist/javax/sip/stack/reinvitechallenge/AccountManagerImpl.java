package test.unit.gov.nist.javax.sip.stack.reinvitechallenge;

import javax.sip.AccountManager;
import javax.sip.ClientTransaction;
import javax.sip.UserCredentials;




public class AccountManagerImpl implements AccountManager {

	
	public UserCredentials getCredentials(
			ClientTransaction challengedTransaction, String realm) {
			return new UserCredentialsImpl();
	}

}
