package org.sainnr.wgc.statistics;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import org.sainnr.wgc.statistics.gaapi.AnalyticsBuilder;
import org.sainnr.wgc.statistics.gaapi.DataQuery;
import org.sainnr.wgc.statistics.io.GaDataWriter;
import org.sainnr.wgc.statistics.oauth.Authorization;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Vladimir on 13.06.2015.
 */
public class Statistics {

    String domain;
    public static final int MIN_VISITS = 0;
    public static final int MIN_TIME = 2;

    public Statistics(String domain) {
        this.domain = domain;
    }

    public String getVisitedPages(String startDate, String endDate) throws GeneralSecurityException, IOException {
        return (new GaDataWriter("visited", domain, startDate, endDate)).writeCsv(getVisitedPagesRaw(startDate, endDate));
    }

    public GaData getVisitedPagesRaw(String startDate, String endDate) throws GeneralSecurityException, IOException {
        Authorization loader = new Authorization();
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Analytics analytics = (new AnalyticsBuilder(
                loader.authorize(transport),
                transport)).getAnalyticsInstance();
        DataQuery query = (new DataQuery(analytics, resolveAccount()))
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setMetrics("ga:pageviews,ga:timeOnPage")
                .setDimensions("ga:nextPagePath,ga:landingPagePath")
                .setSort("-ga:pageviews")
                .setFilter("ga:pageviews>"+MIN_VISITS+";ga:timeOnPage>"+MIN_TIME)
//                .setFilter("ga:timeOnPage>1")
                .setMaxResults(100000);
        return query.execute();
    }

    public String resolveAccount(){
        if (domain == null) {
            return "";
        }
        if ("aksw.org".equals(domain)){
            return "Projekte extern";
        }
        if ("museum.seun.ru".equals(domain)){
            return "museum.seun.ru";
        }
        if ("sstu.ru".equals(domain)){
            return "sstu.ru";
        }
        return "";
    }
}
