package org.sainnr.wgc;

import org.sainnr.wgc.crawler.Crawler;
import org.sainnr.wgc.crawler.HyperPage;
import org.sainnr.wgc.crawler.HypertextStructure;
import org.sainnr.wgc.crawler.io.HypertextWriter;
import org.apache.log4j.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class Console {

    private static final String DEFAULT_PATTERN_LAYOUT = "%d{dd.MM.yyyy HH:mm:ss.SSS} [%-5p] <%c{1}> - %m%n";

    private static void setLogger() {
        PatternLayout pattern = new PatternLayout(DEFAULT_PATTERN_LAYOUT);
        ConsoleAppender appender = new ConsoleAppender(pattern);
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(appender);
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        setLogger();
        testCrawlFullToXML();
    }

    private static void testCrawlSingleToMap() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "aksw.org";
        String url = "http://" + domain + "/";
        Crawler crawler = new Crawler(domain);
        Map<String, Set<String>> results = crawler.singleParseToMap(url);
        HypertextWriter writer = new HypertextWriter();
        writer.writeMap(results);
        System.out.println("Results: " + results.keySet().size());
        for (String key : results.keySet()){
            System.out.println(key);
        }
    }

    private static void testCrawlFullToMap() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "aksw.org";
        String url = "http://" + domain + "/";
        Crawler crawler = new Crawler(domain);
        Map<String, Set<String>> results = crawler.fullParseToMap(url);
        HypertextWriter writer = new HypertextWriter();
        writer.writeMap(results);
        System.out.println("Results: " + results.keySet().size());
        for (String key : results.keySet()){
            System.out.println(key);
        }
    }

    private static void testCrawlSingleToXML() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "aksw.org";
        String url = "http://" + domain + "/";
        Crawler crawler = new Crawler(domain);
        HypertextStructure structure = crawler.singleParseToHyperStructure(url);
        HypertextWriter writer = new HypertextWriter();
        writer.writeCarrot2XML(structure);
        System.out.println("Results: " + structure.getPages().size());
        for (HyperPage page : structure.getPages()){
            System.out.println(page.getId() + ": " + page.getUrl());
        }
    }

    private static void testCrawlFullToXML() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "aksw.org";
        String url = "http://" + domain + "/";
        Crawler crawler = new Crawler(domain);
        HypertextStructure structure = crawler.fullParseToHyperStructure(url);
        HypertextWriter writer = new HypertextWriter();
        writer.writeCarrot2XML(structure);
        writer.writeMapUrl(structure);
        System.out.println("Results: " + structure.getPages().size());
        for (HyperPage page : structure.getPages()){
            System.out.println(page.getId() + ": " + page.getUrl());
        }
    }

    private static Map<String, Set<String>> testMap(){
        Map<String, Set<String>> testMap = new HashMap<String, Set<String>>();
        Set<String> testSet = new HashSet<String>();
        testSet.add("strA");
        testSet.add("strB");
        testMap.put("str1", testSet);
        testMap.put("str2", testSet);
        return testMap;
    }
}
