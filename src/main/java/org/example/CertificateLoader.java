package org.example;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertificateLoader {

    public static KeyPair loadFromKeystore(String keystorePath, String keystorePassword, String alias)
            throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (InputStream is = CertificateLoader.class.getClassLoader().getResourceAsStream(keystorePath)) {
            keystore.load(is, keystorePassword.toCharArray());
        }

        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
        X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);

        return new KeyPair(certificate, privateKey);
    }

    public static class KeyPair {
        public final X509Certificate certificate;
        public final PrivateKey privateKey;

        public KeyPair(X509Certificate certificate, PrivateKey privateKey) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }
    }
}
