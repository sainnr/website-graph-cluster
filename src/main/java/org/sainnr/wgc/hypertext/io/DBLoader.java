package org.sainnr.wgc.hypertext.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by Vladimir on 01.07.2015.
 */
public class DBLoader {

    private static final Log log = LogFactory.getLog(DBLoader.class);
    public static final String LOAD_PAGES_WITH_TEXT =
            "select id, url, title, text from pages";
    public static final String LOAD_PAGES_NO_TEXT =
            "select id, url, is_media from pages";
    public static final String LOAD_PAGES_NO_TEXT_NO_URL =
            "select id, is_media from pages";
    public static final String LOAD_TEMP_HREFS =
            "select url_from, url_to from temp_hrefs";
    public static final String LOAD_HREFS_PER_PAGE =
            "select pid_to from hrefs where pid_from = ?";
    public static final String LOAD_WEIGHTS_PER_PAGE =
            "select id_to, value from weights_ids where id_from = ? and date_from >= ? and date_to <= ?";
    public static final String LOAD_HREFS =
            "select pid_from, pid_to from hrefs";
    public static final String LOAD_WEIGHTS =
            "select id_from, id_to, value from weights_ids where date_from >= ? and date_to <= ?";

    boolean tempHrefs = false;
    boolean noHrefs = false;
    boolean noText = false;
    int textLimit = -1;

    public HypertextStructure loadHypertextForCarrot2(){
        HypertextStructure structure = new HypertextStructure();
        noText = false;
        noHrefs = true;
        preloadPagesTempHrefs(structure);
        return structure;
    }

    void preloadPagesTempHrefs(HypertextStructure structure){
        Set<HyperPage> pages = new HashSet<HyperPage>();
        Map<String, Set<String>> urls = new HashMap<String, Set<String>>();
        if (!noHrefs) {
            urls = preloadTempHrefsBulk();
        }
        Connection connection = null;
        Statement statement = null;
        int maxId = 0;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(!noText ? LOAD_PAGES_WITH_TEXT : LOAD_PAGES_NO_TEXT);
            while(rs.next()){
                HyperPage page = new HyperPage();
                int id = rs.getInt(1);
                if (id > maxId){
                    maxId = id;
                }
                String url = rs.getString(2);
                if (!noText) {
                    String title = rs.getString(3);
                    String content = rs.getString(4);
                    if (textLimit != -1 && textLimit < content.length()){
                        content = content.substring(0, textLimit);
                    }
                    page.setTitle(title);
                    page.setContent(content);
                }
                page.setId(id);
                page.setUrl(url);
                page.setOutcomingUrl(urls.get(url));
                pages.add(page);
                log.trace("Page loaded: " + url);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_PAGES_WITH_TEXT, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        String[] urlArray = new String[maxId+1];
        for (HyperPage page : pages){
            urlArray[page.getId()] = page.getUrl();
        }
        structure.setUrlIndex(Arrays.asList(urlArray));
        structure.setPages(pages);
    }

    Map<String, Set<String>> preloadTempHrefsBulk(){
        Map<String, Set<String>> urls = new HashMap<String, Set<String>>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(LOAD_TEMP_HREFS);
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
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_TEMP_HREFS, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        return urls;
    }

    public HypertextStructure loadWeightedPageLinkStructure(String startDate, String endDate){
        HypertextStructure structure = new HypertextStructure();
        preloadPageLinkStructure(structure, startDate, endDate);
        return structure;
    }

    Map<Integer,Map<Integer,Double>> preloadWeights(String startDate, String endDate){
        Map<Integer,Map<Integer,Double>> weights = new HashMap<Integer, Map<Integer, Double>>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(LOAD_WEIGHTS);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                int urlFrom = rs.getInt(1);
                int urlTo = rs.getInt(2);
                int value = rs.getInt(3);
                Map<Integer,Double> weightsInt;
                if (weights.get(urlFrom) == null){
                    weightsInt = new HashMap<Integer, Double>();
                } else {
                    weightsInt = weights.get(urlFrom);
                }
                weightsInt.put(urlTo,value*1.0);
                weights.put(urlFrom,weightsInt);
            }
            log.info("Weights loaded: " + weights.size());
        } catch (SQLException e) {
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_WEIGHTS, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        return weights;
    }

    Map<Integer,Set<String>> preloadLinks(){
        Map<Integer,Set<String>> links = new HashMap<Integer, Set<String>>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(LOAD_HREFS);
            while(rs.next()){
                int urlFrom = rs.getInt(1);
                Set<String> linksInt;
                if (links.get(urlFrom) == null){
                    linksInt = new HashSet<String>();
                } else {
                    linksInt = links.get(urlFrom);
                }
                int urlTo = rs.getInt(2);
                linksInt.add(urlTo+"");
                links.put(urlFrom,linksInt);
            }
            log.info("Links loaded: " + links.size());
        } catch (SQLException e) {
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_HREFS, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        return links;
    }

    void preloadPageLinkStructure(HypertextStructure structure, String startDate, String endDate){
        Set<HyperPage> pages = new HashSet<HyperPage>();
        Map<Integer, Set<String>> links = preloadLinks();
        Map<Integer, Map<Integer, Double>> weights = preloadWeights(startDate, endDate);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(LOAD_PAGES_NO_TEXT_NO_URL);
            ResultSet rs = statement.executeQuery(LOAD_PAGES_NO_TEXT);
            while(rs.next()){
                HyperPage page = new HyperPage();
                int id = rs.getInt(1);
                String url = rs.getString(2);
                boolean isMedia = (rs.getInt(3) == 1);
                page.setId(id);
                page.setUrl(url);
                if (!isMedia) {
                    page.setOutcomingUrl(links.get(id));
                    Map<Integer,Double> weightsInt = weights.get(id);
                    if (weightsInt != null) {
                        for (Map.Entry<Integer, Double> entry : weightsInt.entrySet()) {
                            page.setWeight(entry.getKey() + "", entry.getValue());
                        }
                    }
                    pages.add(page);
                }
                log.trace("Page loaded: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot handle SQL query: " + LOAD_PAGES_NO_TEXT_NO_URL, e);
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        structure.setPages(pages);
    }

    Connection getConnection(){
        try {
            return new DBConnector().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to DS", e);
        }
    }

    public void setTempHrefs(boolean tempHrefs) {
        this.tempHrefs = tempHrefs;
    }

    public void setNoHrefs(boolean noHrefs) {
        this.noHrefs = noHrefs;
    }

    public void setTextLimit(int textLimit) {
        this.textLimit = textLimit;
    }
}
