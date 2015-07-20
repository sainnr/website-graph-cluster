package org.sainnr.wgc.statistics.io;

import com.google.api.services.analytics.model.GaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sainnr.wgc.hypertext.io.DBConnector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Vladimir on 12.06.2015.
 */
public class DBGaDataWriter {

    public static final int URL_MAX_SIZE = 255;
    public static final String INSERT_WEIGHTS_BATCH =
            "insert into weights(url_from, url_to, date_from, date_to, value) values (?, ?, ?, ?, ?)";
    private static final Log log = LogFactory.getLog(DBGaDataWriter.class);
    String domainSuffix;
    String startDate;
    String endDate;

    public DBGaDataWriter(String domainSuffix, String startDate, String endDate) {
        this.domainSuffix = domainSuffix;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int writeToDB(GaData dataToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        if (dataToWrite.getRows() == null || dataToWrite.getRows().isEmpty()) {
            return 0;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_WEIGHTS_BATCH);
            for (List<String> row : dataToWrite.getRows()) {
                String urlFrom = format(row.get(1));
                String urlTo = format(row.get(0));
                String value = row.get(2);
                if (urlFrom.length() > URL_MAX_SIZE){
                    urlFrom = urlFrom.substring(0, URL_MAX_SIZE);
                }
                if (urlTo.length() > URL_MAX_SIZE){
                    urlTo = urlTo.substring(0, URL_MAX_SIZE);
                }
                statement.setString(1, urlFrom);
                statement.setString(2, urlTo);
                statement.setDate(3, Date.valueOf(startDate));
                statement.setDate(4, Date.valueOf(endDate));
                statement.setInt(5, Integer.parseInt(value));
                statement.addBatch();
            }
            // Execute the batch
            int [] updateCounts = statement.executeBatch();
            log.info("Inserted pages: " + updateCounts.length);
            return updateCounts.length;
        } catch (SQLException e) {
            log.error("Cannot handle SQL query: " + INSERT_WEIGHTS_BATCH, e);
//            throw new RuntimeException("Cannot handle SQL query: " + INSERT_PAGES_BATCH, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        return 0;
    }

    private String format(String url){
        url = "http://" + domainSuffix + url;
        if (url.endsWith("/") || url.endsWith("#")){
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    Connection getConnection(){
        try {
            return new DBConnector().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to DS", e);
        }
    }


}
