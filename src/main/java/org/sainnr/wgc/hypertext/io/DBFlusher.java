package org.sainnr.wgc.hypertext.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sainnr.wgc.hypertext.data.HyperPage;

import java.sql.*;
import java.util.*;

/**
 * Created by Vladimir on 06.07.2015.
 */
public class DBFlusher {

    private static final Log log = LogFactory.getLog(DBFlusher.class);
    public static final int URL_MAX_SIZE = 255;
    public static final String INSERT_PAGES_BATCH =
            "INSERT INTO pages (id, url, title, text, is_media) VALUES(?, ?, ?, ?, ?)";
    public static final String INSERT_TEMP_HREFS_BATCH =
            "INSERT INTO temp_hrefs (url_from, url_to) VALUES(?, ?)";
    Map<String, Set<String>> hrefs;

    public void flushPages(Set<HyperPage> pageSet, List<String> mediaIndex){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_PAGES_BATCH);
            hrefs = new HashMap<String, Set<String>>();
            for (HyperPage page : pageSet) {
                String url = page.getUrl();
                if (url.length() > URL_MAX_SIZE){
                    url = url.substring(0, URL_MAX_SIZE);
                }
                statement.setInt(1, page.getId()+1);
                statement.setString(2, url);
                statement.setString(3, page.getTitle());
                statement.setString(4, page.getContent());
                statement.setInt(5, (mediaIndex.contains(url) ? 1 : 0));
                statement.addBatch();
                hrefs.put(url, page.getOutcomingUrl());
            }
            // Execute the batch
            int [] updateCounts = statement.executeBatch();
            log.info("Inserted pages: " + updateCounts.length);
            insertTempHrefs(connection, statement);
        } catch (SQLException e) {
            log.error("Cannot handle SQL query: " + INSERT_PAGES_BATCH, e);
//            throw new RuntimeException("Cannot handle SQL query: " + INSERT_PAGES_BATCH, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
    }

    void insertTempHrefs(Connection connection, PreparedStatement statement) throws SQLException {
        statement = connection.prepareStatement(INSERT_TEMP_HREFS_BATCH);
        for (String urlFrom : hrefs.keySet()) {
            for (String urlTo : hrefs.get(urlFrom)) {
                if (urlTo.length() > URL_MAX_SIZE){
                    urlTo = urlTo.substring(0, URL_MAX_SIZE);
                }
                if (urlFrom.length() > URL_MAX_SIZE){
                    urlFrom = urlFrom.substring(0, URL_MAX_SIZE);
                }
                statement.setString(1, urlFrom);
                statement.setString(2, urlTo);
                statement.addBatch();
            }
        }
        // Execute the batch
        int [] updateCounts = statement.executeBatch();
        log.info("Inserted hrefs: " + updateCounts.length);
    }

    Connection getConnection(){
        try {
            return new DBConnector().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to DS", e);
        }
    }
}
