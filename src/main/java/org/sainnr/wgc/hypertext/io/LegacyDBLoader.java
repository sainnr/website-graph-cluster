package org.sainnr.wgc.hypertext.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Vladimir on 01.07.2015.
 */
public class LegacyDBLoader {

    private static final Log log = LogFactory.getLog(LegacyDBLoader.class);
    public static final String LOAD_PAGES_WITH_TEXT =
            "select \n" +
            "   v.verticle_id,\n" +
            "   v.url,\n" +
            "   t.text\n" +
            "from verticles v left join texts t \n" +
            "on v.verticle_id = t.verticle_id";
    public static final String LOAD_OUTCOMING_URLS =
            "select \n" +
            "\tv1.url as \"from\",\n" +
            "\tv2.url as \"to\"\n" +
            "from edges e, verticles v1, verticles v2\n" +
            "where e.verticle_start_id = v1.verticle_id \n" +
            "\tand e.verticle_end_id = v2.verticle_id";

    public HypertextStructure loadHypertext(){
        HypertextStructure structure = new HypertextStructure();
        preloadPages(structure);
        return structure;
    }

    void preloadPages(HypertextStructure structure){
        Set<HyperPage> pages = new HashSet<HyperPage>();
        List<String> urlIndex = new ArrayList<String>();
        Map<String, Set<String>> urls = preloadOutUrls();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(LOAD_PAGES_WITH_TEXT);
            while(rs.next()){
                String url = rs.getString(2);
                if (!urlIndex.contains(url)) {
                    urlIndex.add(url);
                }
                HyperPage page = new HyperPage();
//                page.setId(rs.getInt(1));
                page.setId(urlIndex.indexOf(url));
                page.setUrl(url);
                page.setContent(rs.getString(3));
                page.setOutcomingUrl(urls.get(url));
                pages.add(page);
                log.trace("Page loaded: " + url);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_OUTCOMING_URLS, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        structure.setUrlIndex(urlIndex);
        structure.setPages(pages);
    }

    Map<String, Set<String>> preloadOutUrls(){
        Map<String, Set<String>> urls = new HashMap<String, Set<String>>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(LOAD_OUTCOMING_URLS);
            while(rs.next()){
                String urlFrom = rs.getString(1);
                String urlTo = rs.getString(2);
                Set<String> outUrls;
                if (urls.containsKey(urlFrom)){
                    outUrls = urls.get(urlFrom);
                } else {
                    outUrls = new HashSet<String>();
                }
                outUrls.add(urlTo);
                urls.put(urlFrom, outUrls);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_OUTCOMING_URLS, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        return urls;
    }

    Connection getConnection(){
        try {
            return new DBConnector().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to DS", e);
        }
    }
}
