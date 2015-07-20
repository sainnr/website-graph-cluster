package org.sainnr.wgc.statistics;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.sainnr.wgc.hypertext.io.DBConnector;
import org.sainnr.wgc.statistics.data.GaVisitedPagesStructure;
import org.sainnr.wgc.statistics.gaapi.AnalyticsBuilder;
import org.sainnr.wgc.statistics.gaapi.AnalyticsService;
import org.sainnr.wgc.statistics.gaapi.DataQuery;
import org.sainnr.wgc.statistics.io.DBGaDataWriter;
import org.sainnr.wgc.statistics.io.GaDataReader;
import org.sainnr.wgc.statistics.io.GaDataWriter;
import org.sainnr.wgc.statistics.oauth.Authorization;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by Vladimir on 09.06.2015.
 */
public class StatisticsConsole {

    private static final String DEFAULT_PATTERN_LAYOUT = "%d{dd.MM.yyyy HH:mm:ss.SSS} [%-5p] <%c{1}> - %m%n";

    private static void setFileLogger() throws IOException {
        PatternLayout pattern = new PatternLayout(DEFAULT_PATTERN_LAYOUT);
        RollingFileAppender appender = new RollingFileAppender(pattern, "logs/console.log");
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(appender);
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        setFileLogger();
        testGaToDBCycle();
    }

    public static void testCreds() throws IOException, GeneralSecurityException {
        Authorization loader = new Authorization();
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential cred = loader.authorize(transport);
    }

    public static void testGa() throws GeneralSecurityException, IOException {
        String domain = "aksw.org";
//        String domain = "sstu.ru";
        String startDate = "2015-07-15";
        String endDate = "2015-07-16";
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

    static void testGaToDB() throws GeneralSecurityException, IOException {
        String domain = "sstu.ru";
        String startDate = "2015-06-30";
        String endDate = "2015-07-01";
        Statistics statistics = new Statistics(domain);
        GaData data = statistics.getVisitedPagesRaw(startDate, endDate);
        DBConnector.setDefaultDBName(domain);
        int rows = (new DBGaDataWriter(domain,startDate,endDate)).writeToDB(data);

        System.out.println("Inserted rows: " + rows);
    }

    static void testGaToDBCycle() throws GeneralSecurityException, IOException {
//        String domain = "sstu.ru";
        String domain = "aksw.org";
        DBConnector.setDefaultDBName(domain);
        Statistics statistics = new Statistics(domain);
        for (int m = 1; m <= 7; m ++) {
            int dMax = 0;
            switch (m){
                case 1: {dMax = 31; break;}
                case 2: {dMax = 28; break;}
                case 3: {dMax = 31; break;}
                case 4: {dMax = 30; break;}
                case 5: {dMax = 31; break;}
                case 6: {dMax = 30; break;}
                case 7: {dMax = 15; break;}
            }
            for (int d = 1; d < dMax; d++) {
                String ds1 = d < 10 ? "0"+d : d + "";
                String ds2 = (d+1) < 10 ? "0"+(d+1) : (d+1) + "";
                String ms = m < 10 ? "0"+m : m + "";
                String startDate = "2015-" + ms + "-" + ds1;
                String endDate = "2015-" + ms + "-" + ds2;
                try {
                    GaData data = statistics.getVisitedPagesRaw(startDate, endDate);
                    int rows = (new DBGaDataWriter(domain, startDate, endDate)).writeToDB(data);

                    System.out.println(startDate + ":" + endDate + "Inserted rows: " + rows);
                }catch (Exception e){
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }
}
