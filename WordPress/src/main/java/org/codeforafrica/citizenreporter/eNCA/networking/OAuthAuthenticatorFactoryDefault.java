package org.codeforafrica.citizenreporter.eNCA.networking;

public class OAuthAuthenticatorFactoryDefault implements OAuthAuthenticatorFactoryAbstract {
    public OAuthAuthenticator make() {
        return new OAuthAuthenticator();
    }
}
