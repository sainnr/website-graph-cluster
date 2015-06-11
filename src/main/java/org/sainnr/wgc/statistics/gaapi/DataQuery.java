package org.sainnr.wgc.statistics.gaapi;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;

import java.io.IOException;

/**
 * @author sainnr
 * @since 07.05.14
 */
public class DataQuery {
    private Analytics analytics;
    private String profileId;
    private String startDate;
    private String endDate;
    private String metrics;
    private String dimensions;
    private String sort;
    private String filter;
    private int maxResults;

    public DataQuery(Analytics analytics, String accountName) throws IOException {
        this.analytics = analytics;
        this.profileId = (new AnalyticsService(analytics)).getProfileId(accountName);
    }

    public GaData execute() throws IOException {
        return analytics.data().ga().get(
                "ga:" + profileId,
                startDate,
                endDate,
                metrics)
                .setDimensions(dimensions)
                .setSort(sort)
                .setFilters(filter)
                .setMaxResults(maxResults)
                .execute();
    }

    public DataQuery setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public DataQuery setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public DataQuery setMetrics(String metrics) {
        this.metrics = metrics;
        return this;
    }

    public DataQuery setDimensions(String dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public DataQuery setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public DataQuery setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public DataQuery setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }
}
