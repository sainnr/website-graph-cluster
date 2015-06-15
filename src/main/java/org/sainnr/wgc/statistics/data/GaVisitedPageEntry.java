package org.sainnr.wgc.statistics.data;

import java.util.Set;

/**
 * Created by Vladimir on 13.06.2015.
 */
public class GaVisitedPageEntry {

    int id;
    String url;
    String nextUrl;
    double numOfVisits;
    double timeOnPage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public double getNumOfVisits() {
        return numOfVisits;
    }

    public void setNumOfVisits(double numOfVisits) {
        this.numOfVisits = numOfVisits;
    }

    public double getTimeOnPage() {
        return timeOnPage;
    }

    public void setTimeOnPage(double timeOnPage) {
        this.timeOnPage = timeOnPage;
    }

    @Override
    public String toString() {
        return "GaVisitedPageEntry{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", nextUrl='" + nextUrl + '\'' +
                ", numOfVisits=" + numOfVisits +
                ", timeOnPage=" + timeOnPage +
                '}';
    }
}
