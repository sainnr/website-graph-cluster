package org.sainnr.wgc.statistics;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import org.sainnr.wgc.statistics.gaapi.AnalyticsBuilder;
import org.sainnr.wgc.statistics.gaapi.AnalyticsService;
import org.sainnr.wgc.statistics.gaapi.DataQuery;
import org.sainnr.wgc.statistics.io.GaDataWriter;
import org.sainnr.wgc.statistics.oauth.Authorization;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Vladimir on 09.06.2015.
 */
public class StatisticsConsole {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        testGA();
    }

    public static void testCreds() throws IOException, GeneralSecurityException {
        Authorization loader = new Authorization();
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential cred = loader.authorize(transport);
    }

    public static void testGA() throws GeneralSecurityException, IOException {
        String accName = "Projekte extern";
        Authorization loader = new Authorization();
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Analytics analytics = (new AnalyticsBuilder(
                loader.authorize(transport),
                transport)).getAnalyticsInstance();
        DataQuery query = (new DataQuery(analytics, accName))
                .setStartDate("2015-01-01")
                .setEndDate("2015-05-31")
                .setMetrics("ga:pageviews,ga:timeOnPage")
                .setDimensions("ga:nextPagePath,ga:landingPagePath")
                .setSort("ga:landingPagePath")
                .setFilter("ga:pageviews>2;ga:timeOnPage>5")
                .setMaxResults(10000);
        GaData gaData = query.execute();
        (new GaDataWriter()).writeCsv(gaData);
    }
}
