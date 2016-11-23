package org.codeforafrica.citizenreporter.starreports.networking;

public class OAuthAuthenticatorFactoryDefault implements OAuthAuthenticatorFactoryAbstract {
    public OAuthAuthenticator make() {
        return new OAuthAuthenticator();
    }
}
