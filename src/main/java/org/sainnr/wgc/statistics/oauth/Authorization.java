package org.sainnr.wgc.statistics.oauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.AnalyticsScopes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * @author sainnr
 * @since 29.05.14
 */
public class Authorization {

    private static final Log log = LogFactory.getLog(Authorization.class);
    private static final String RESOURCE_PATH = "client_secrets.json";
    private static final String CREDENTIALS_PATH = ".credentials/analytics.json";

    /**
     * Loads credentials from prepared JSON file with secrets
     * @throws java.io.IOException
     */
    public Credential authorize(HttpTransport transport) throws IOException {
        // load secrets and obtain creds
        Reader reader = new FileReader(Authorization.class.getClassLoader().getResource(RESOURCE_PATH).getFile());
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, reader);
        log.debug(clientSecrets.getDetails());
        // set up file credential store
        FileCredentialStore credentialStore = new FileCredentialStore(
                new File(CREDENTIALS_PATH),
                jsonFactory);
        // set up authorization code flow
        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, jsonFactory, clientSecrets,
                Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).setCredentialStore(
                credentialStore).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    }
}
