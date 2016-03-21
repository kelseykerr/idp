package com.impulsecontrol.idp.services;


import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import org.opensaml.xml.security.x509.BasicX509Credential;

public class SamlService {
    public static final String KEYSTORE_FILE = "classpath:/certStore.jks";
    public static final String KEYSTORE_ALIAS = "certStore";
    public static final String KEYSTORE_KEYPASS = "idpTest";


    public BasicX509Credential getBasicX509Credential() throws Exception {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = KEYSTORE_KEYPASS.toCharArray();
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource storeFile = loader.getResource(KEYSTORE_FILE);
            InputStream is = storeFile.getInputStream();

            ks.load(is, password);
            is.close();

            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                    ks.getEntry(KEYSTORE_ALIAS, new
                            KeyStore.PasswordProtection(KEYSTORE_KEYPASS.toCharArray()));
            PrivateKey pk = pkEntry.getPrivateKey();
            java.security.cert.X509Certificate certificate = (java.security.cert.X509Certificate)
                    pkEntry.getCertificate();

            BasicX509Credential cred = new BasicX509Credential();
            cred.setEntityCertificate(certificate);
            cred.setPrivateKey(pk);
            return cred;
        } catch (Exception e) {
            throw new Exception("Unable to get x509 credential: " + e.getMessage());
        }

    }

}
