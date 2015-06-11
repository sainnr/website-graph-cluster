package org.sainnr.wgc.statistics.gaapi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Builder encapsulates all the weird stuff with authentication and obtaining Analytics instance.
 * @author sainnr
 * @since 07.05.14
 */
public class AnalyticsBuilder {

    private static final String APPLICATION_NAME = "VisualAnalytics";
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final Log log = LogFactory.getLog(AnalyticsBuilder.class);
    private Analytics analyticsInstance;


    /**
     * Creates new instance of central Analytics class
     * @param credential needed to receive client secrets from app context
     * @throws java.io.IOException
     */
    public AnalyticsBuilder(Credential credential, HttpTransport transport) throws IOException {
        this.analyticsInstance = new Analytics.Builder(
                transport,
                JSON_FACTORY,
                credential).setApplicationName(APPLICATION_NAME).build();
    }


    /**
     * @return just created Analytics instance
     */
    public Analytics getAnalyticsInstance(){
        return analyticsInstance;
    }
}
