package org.wordpress.android.mocks;

import android.content.Context;

import org.codeforafrica.citizenreporter.starreports.networking.OAuthAuthenticator;
import org.codeforafrica.citizenreporter.starreports.networking.OAuthAuthenticatorFactoryAbstract;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;

public class OAuthAuthenticatorFactoryTest implements OAuthAuthenticatorFactoryAbstract {
    public enum Mode {EMPTY}

    public static Mode sMode = Mode.EMPTY;
    public static Context sContext;

    public OAuthAuthenticator make() {
        switch (sMode) {
            case EMPTY:
            default:
                AppLog.v(T.TESTS, "make: OAuthAuthenticatorEmptyMock");
                return new OAuthAuthenticatorEmptyMock();
        }
    }
}
