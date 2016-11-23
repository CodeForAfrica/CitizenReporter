package org.wordpress.android.mocks;

import org.codeforafrica.citizenreporter.starreports.networking.AuthenticatorRequest;
import org.codeforafrica.citizenreporter.starreports.networking.OAuthAuthenticator;
import org.codeforafrica.citizenreporter.starreports.models.AccountHelper;

public class OAuthAuthenticatorEmptyMock extends OAuthAuthenticator {
    public void authenticate(AuthenticatorRequest request) {
        AccountHelper.getDefaultAccount().setAccessToken("dead-parrot");
    }
}
