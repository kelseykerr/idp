package com.impulsecontrol.idp.services;


import com.impulsecontrol.idp.core.SpMetadata;
import com.impulsecontrol.idp.core.User;
import org.apache.commons.ssl.util.Hex;
import org.bouncycastle.util.encoders.Base64;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml2.core.impl.StatusMessageBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;

import org.opensaml.xml.security.x509.BasicX509Credential;

import javax.xml.namespace.QName;

public class SamlService {
    public static final String KEYSTORE_FILE = "classpath:/certStore.jks";
    public static final String KEYSTORE_ALIAS = "certStore";
    public static final String KEYSTORE_KEYPASS = "idpTest";
    public static final String ATTRIBUTE_FIRSTNAME = "firstName";
    public static final String ATTRIBUTE_LASTNAME = "lastName";
    public static final String ATTRIBUTE_USERNAME = "username";
    public static final Integer SAML_RESPONSE_VALID_MINUTES = 1;
    private static XMLObjectBuilderFactory builderFactory;

    public SamlService() {

    }


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

    public Response buildResponse(User user, SpMetadata spMetadata) throws Exception {
        try {
            String acs = spMetadata.getAcsUrl();
            String issuerUrl = "http://localhost:8080";
            Response response = new org.opensaml.saml2.core.impl.ResponseBuilder().buildObject();
            Issuer issuer = buildSamlObject(Issuer.DEFAULT_ELEMENT_NAME);
            issuer.setValue(issuerUrl);
            response.setIssuer(issuer);
            byte[] buf = new byte[20];
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(buf);
            String id = new String(Hex.encode(buf));
            String responseId = String.valueOf(id);
            response.setID(responseId);
            response.setDestination(acs);
            response.setStatus(buildStatus(StatusCode.SUCCESS_URI, null));
            response.setVersion(SAMLVersion.VERSION_20);
            response.setIssueInstant(new DateTime());
            Assertion assertion = buildDefaultAssertion(user, responseId,
                    spMetadata.getAudienceRestriction(), issuerUrl);
            response.getAssertions().add(assertion);
            return response;
        } catch (Exception e) {
            throw new Exception("Unable to build SAML Response: " + e.getMessage());
        }
    }

    public <T extends SAMLObject> T buildSamlObject(QName qname) throws ConfigurationException {
        if (builderFactory == null) {
            // OpenSAML 2.3
            DefaultBootstrap.bootstrap();
            builderFactory = Configuration.getBuilderFactory();
        }

        SAMLObjectBuilder builder = (SAMLObjectBuilder) builderFactory.getBuilder(qname);
        T samlObject = (T) builder.buildObject();
        return samlObject;
    }

    public Status buildStatus(String status, String statMsg) {
        Status stat = new StatusBuilder().buildObject();
        StatusCode statCode = new StatusCodeBuilder().buildObject();
        statCode.setValue(status);
        stat.setStatusCode(statCode);
        if (statMsg != null) {
            StatusMessage statMesssage = new StatusMessageBuilder().buildObject();
            statMesssage.setMessage(statMsg);
            stat.setStatusMessage(statMesssage);
        }
        return stat;
    }

    public Assertion buildDefaultAssertion(User user, String responseId, String audienceUrl, String issuerUrl) {
        try {
            // Create the assertion
            Assertion assertion = buildSamlObject(Assertion.DEFAULT_ELEMENT_NAME);
            assertion.setID(responseId);
            assertion.setVersion(SAMLVersion.VERSION_20);
            // Add Issuer
            Issuer issuer = buildSamlObject(Issuer.DEFAULT_ELEMENT_NAME);
            issuer.setValue(issuerUrl);
            assertion.setIssuer(issuer);
            DateTime now = new DateTime();
            assertion.setIssueInstant(now);
            // Add Subject
            Subject subject = getSubject(user, now);
            assertion.setSubject(subject);
            // Add Authentication Statement
            AuthnStatement authnStatement = getAuthnStatement(now);
            assertion.getAuthnStatements().add(authnStatement);
            // Add Attribute Statement
            AttributeStatement attrStatement = getAttributeStatement(user);
            assertion.getAttributeStatements().add(attrStatement);
            // Add Conditions - do not cache, audience restriction
            Conditions conditions = getConditions(audienceUrl);
            assertion.setConditions(conditions);
            //get 509 credential and sign the assertion
            BasicX509Credential cred = getBasicX509Credential();
            return (Assertion) setSignature(assertion, cred);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Subject getSubject(User user, DateTime now) throws Exception {
        NameID nameId = buildSamlObject(NameID.DEFAULT_ELEMENT_NAME);
        nameId.setValue(user.getUsername());
        nameId.setFormat(NameID.UNSPECIFIED);

        SubjectConfirmationData confirmationMethod = buildSamlObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        confirmationMethod.setNotBefore(now);
        confirmationMethod.setNotOnOrAfter(now.plusMinutes(SAML_RESPONSE_VALID_MINUTES));

        SubjectConfirmation subjectConfirmation = buildSamlObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setSubjectConfirmationData(confirmationMethod);

        Subject subject = buildSamlObject(Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameId);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        return subject;
    }

    public AuthnStatement getAuthnStatement(DateTime now) throws Exception {
        try {
            AuthnStatement authnStatement = buildSamlObject(AuthnStatement.DEFAULT_ELEMENT_NAME);
            authnStatement.setAuthnInstant(now);
            authnStatement.setSessionNotOnOrAfter(now.plusMinutes(SAML_RESPONSE_VALID_MINUTES));

            AuthnContext authnContext = buildSamlObject(AuthnContext.DEFAULT_ELEMENT_NAME);
            AuthnContextClassRef authnContextClassRef = buildSamlObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
            authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);
            authnContext.setAuthnContextClassRef(authnContextClassRef);
            authnStatement.setAuthnContext(authnContext);
            return authnStatement;
        } catch (Exception e) {
            throw new Exception("Unable to generate authn statement for saml assertion: " + e.getMessage());
        }

    }

    public Conditions getConditions(String audienceUrl) throws Exception {
        try {
            Conditions conditions = buildSamlObject(Conditions.DEFAULT_ELEMENT_NAME);
            Condition condition = buildSamlObject(OneTimeUse.DEFAULT_ELEMENT_NAME);
            conditions.getConditions().add(condition);

            AudienceRestriction audienceRestriction = buildSamlObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);

            Audience audience = buildSamlObject(Audience.DEFAULT_ELEMENT_NAME);
            audience.setAudienceURI(audienceUrl);
            audienceRestriction.getAudiences().add(audience);
            conditions.getAudienceRestrictions().add(audienceRestriction);
            return conditions;
        } catch (Exception e) {
            throw new Exception("Unable to add conditions to saml assertion: " + e.getMessage());
        }
    }

    public AttributeStatement getAttributeStatement(User user) throws Exception {
        try {
            AttributeStatement attrStatement = buildSamlObject(AttributeStatement.DEFAULT_ELEMENT_NAME);
            attrStatement.getAttributes().add(buildStringAttribute(ATTRIBUTE_FIRSTNAME, user.getFirstName()));
            attrStatement.getAttributes().add(buildStringAttribute(ATTRIBUTE_LASTNAME, user.getLastName()));
            attrStatement.getAttributes().add(buildStringAttribute(ATTRIBUTE_USERNAME, user.getUsername()));
            return attrStatement;
        } catch (Exception e) {
            throw new Exception("Unable to add attributes to saml assertion: " + e.getMessage());
        }

    }

    public Attribute buildStringAttribute(String name, Object value) throws ConfigurationException {
        Attribute attr = buildSamlObject(Attribute.DEFAULT_ELEMENT_NAME);
        attr.setName(name);
        XMLObjectBuilder stringBuilder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
        XSString attrValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValue.setValue(value.toString());
        attr.getAttributeValues().add(attrValue);
        return attr;
    }

    public XMLObjectBuilderFactory getSAMLBuilder() throws ConfigurationException {
        if (builderFactory == null) {
            DefaultBootstrap.bootstrap();
            builderFactory = Configuration.getBuilderFactory();
        }
        return builderFactory;
    }

    public SignableXMLObject setSignature(SignableXMLObject signableXMLObject, X509Credential cred) throws Exception {
        DefaultBootstrap.bootstrap();
        SignatureBuilder signatureBuilder = (SignatureBuilder) getSAMLBuilder().getBuilder(Signature.DEFAULT_ELEMENT_NAME);
        Signature signature = (Signature) signatureBuilder.buildObject();
        signature.setSigningCredential(cred);

        SecurityConfiguration secConfig = Configuration.getGlobalSecurityConfiguration();
        SecurityHelper.prepareSignatureParams(signature, cred, secConfig, null);
        signableXMLObject.setSignature(signature);

        MarshallerFactory marshallerFactory = org.opensaml.xml.Configuration.getMarshallerFactory();
        Marshaller marshaller = marshallerFactory.getMarshaller(signableXMLObject);

        try {
            marshaller.marshall(signableXMLObject);
        } catch (MarshallingException e) {
            throw new Exception("Unable to marshall the request", e);
        }
        Signer.signObject(signature);
        return signableXMLObject;
    }

    public Response signSamlResponseObject(Response samlResponse) throws Exception {
        try {
            BasicX509Credential credential = getBasicX509Credential();
            return (Response) setSignature(samlResponse, credential);
        } catch (Exception e) {
            throw new Exception("Unable to sign the response: " + e.getMessage());
        }
    }


    public String encodeSamlResponse(String xmlResponse) throws Exception {
        try {
            xmlResponse = new String(Base64.encode(xmlResponse
                    .getBytes("UTF-8")), "UTF-8");
            xmlResponse = xmlResponse.trim();
            StringBuilder formattedResponse = new StringBuilder();
            // Splitting the final response text to 60 char lines
            for (int i = 0; i < xmlResponse.length(); i++) {
                if (((i % 60) == 0) && i != 0)
                    formattedResponse.append("\n");
                formattedResponse.append(xmlResponse.charAt(i));
            }
            xmlResponse = formattedResponse.toString();
            return xmlResponse;
        } catch (Exception e) {
            throw new Exception("Unable to encode SAML resposne: " + e.getMessage());
        }
    }

}
