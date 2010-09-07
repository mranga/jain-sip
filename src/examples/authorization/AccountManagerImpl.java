package examples.authorization;

import javax.sip.AccountManager;
import javax.sip.ClientTransaction;
import javax.sip.UserCredentials;


public class AccountManagerImpl implements AccountManager {
    

    public UserCredentials getCredentials(ClientTransaction challengedTransaction, String realm) {
       return new UserCredentialsImpl("auth","nist.gov","pass");
    }

}
