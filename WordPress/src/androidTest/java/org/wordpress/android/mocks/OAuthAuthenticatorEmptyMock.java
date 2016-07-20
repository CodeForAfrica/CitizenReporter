package org.wordpress.android.mocks;

import org.codeforafrica.citizenreporter.eNCA.networking.AuthenticatorRequest;
import org.codeforafrica.citizenreporter.eNCA.networking.OAuthAuthenticator;
import org.codeforafrica.citizenreporter.eNCA.models.AccountHelper;

public class OAuthAuthenticatorEmptyMock extends OAuthAuthenticator {
    public void authenticate(AuthenticatorRequest request) {
        AccountHelper.getDefaultAccount().setAccessToken("dead-parrot");
    }
}
