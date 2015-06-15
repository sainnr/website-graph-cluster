package org.sainnr.wgc.statistics;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import org.sainnr.wgc.statistics.data.GaVisitedPagesStructure;
import org.sainnr.wgc.statistics.gaapi.AnalyticsBuilder;
import org.sainnr.wgc.statistics.gaapi.AnalyticsService;
import org.sainnr.wgc.statistics.gaapi.DataQuery;
import org.sainnr.wgc.statistics.io.GaDataReader;
import org.sainnr.wgc.statistics.io.GaDataWriter;
import org.sainnr.wgc.statistics.oauth.Authorization;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Vladimir on 09.06.2015.
 */
public class StatisticsConsole {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        testGa();
    }

    public static void testCreds() throws IOException, GeneralSecurityException {
        Authorization loader = new Authorization();
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential cred = loader.authorize(transport);
    }

    public static void testGa() throws GeneralSecurityException, IOException {
        String domain = "aksw.org";
        String startDate = "2010-01-01";
        String endDate = "2015-06-12";
        Statistics statistics = new Statistics(domain);
        String filename = statistics.getVisitedPages(startDate, endDate);
        System.out.println(filename);
    }

    public static void testGaReader() throws IOException {
        String domain = "aksw.org";
        String file = "gadata/ga_visited_aksworg_2015-01-01_2015-05-31.csv";
        GaVisitedPagesStructure structure = (new GaDataReader(domain)).readCSVVisitedPages(file);
        System.out.println(structure);
    }
}
