package org.sainnr.wgc;

import org.sainnr.wgc.clustering.ClusteringAlgorithm;
import org.sainnr.wgc.clustering.io.ClusterWriter;
import org.sainnr.wgc.hypertext.Crawler;
import org.sainnr.wgc.hypertext.DBCrawler;
import org.sainnr.wgc.hypertext.WeightsApplier;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;
import org.sainnr.wgc.hypertext.io.*;
import org.apache.log4j.*;
import org.sainnr.wgc.statistics.data.GaVisitedPagesStructure;
import org.sainnr.wgc.statistics.io.GaDataReader;
import prefuse.data.Table;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class Console {

    private static final String DEFAULT_PATTERN_LAYOUT = "%d{dd.MM.yyyy HH:mm:ss.SSS} [%-5p] <%c{1}> - %m%n";

    private static void setConsoleLogger() {
        PatternLayout pattern = new PatternLayout(DEFAULT_PATTERN_LAYOUT);
        ConsoleAppender appender = new ConsoleAppender(pattern);
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(appender);
    }

    private static void setFileLogger() throws IOException {
        PatternLayout pattern = new PatternLayout(DEFAULT_PATTERN_LAYOUT);
        RollingFileAppender appender = new RollingFileAppender(pattern, "logs/console.log");
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(appender);
    }

    public static void main(String[] args) throws IOException {
        setFileLogger();
        testCrawlFull();
        testApplyWeights();
        testDBLoad();
    }

    private static void testWriter() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "example.org";
        HypertextWriter writer = new HypertextWriter(domain);
        writer.writeMap(testMap());
    }

    private static void testCrawlFull() throws FileNotFoundException, UnsupportedEncodingException {
//        String domain = "aksw.org";
        String domain = "sstu.ru";
        String url = "http://" + domain + "/";
        Crawler crawler = new Crawler(domain);
//        crawler.setTemplatePath("article#content");
        crawler.setTemplatePath("div.page-container");
        crawler.setReserveTemplatePaths(new String[]{"div#content"});
        crawler.setSkipText(true);
        HypertextStructure structure = crawler.fullParseToHyperStructure(url);
        HypertextWriter writer = new HypertextWriter(domain);
        writer.writeMapUrl(structure);
        writer.writeGEXF(structure);
        writer.writeBrokenLinks(structure);
        writer.writeCarrot2XML(structure);
        System.out.println("Results: " + structure.getPages().size());
//        System.out.println(structure);
    }

    public static void testCrawlSingle() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "sstu.ru";
        String url = "http://" + domain + "/";
        Crawler crawler = new Crawler(domain);
        crawler.setEncoding("UTF-8");
//        crawler.setTemplatePath("article#content");
        crawler.setTemplatePath("div.page-container");
        HypertextStructure structure = crawler.singleParseToHyperStructure(url);
        HypertextWriter writer = new HypertextWriter(domain);
        writer.writeCarrot2XML(structure);
        writer.writeGEXF(structure);
    }

    public static void testHypertextReader() throws IOException {
        String filename = "hypertext/ht_cm_aksworg_1434483791.csv";
//        String filename = "hypertext/pagegroups.csv";
        HypertextStructure structure = (new HypertextReader()).readCSVUrlMapToHypertext(filename);
        HypertextWriter writer = new HypertextWriter("aksw.org");
//        writer.writeIndex(structure);
        writer.writeMapIds(structure);
//        writer.writeGEXF(structure);
        writer.writeCarrot2XML(structure);
//        System.out.println(structure);
    }

    public static void testApplyWeights() throws IOException {
        String domain = "aksw.org";
//        String htFile = "hypertext/ht_cm_aksworg_1434152972.csv";
        String htFile = "hypertext/ht_cm_aksworg_1434327226.csv"; // full
        String gaFile = "gadata/ga_visited_aksworg_2010-01-01_2015-06-12.csv";
        HypertextStructure ht = (new HypertextReader()).readCSVUrlMapToHypertext(htFile);
//        System.out.println(ht);
        GaVisitedPagesStructure ga = (new GaDataReader(domain)).readCSVVisitedPages(gaFile);
//        System.out.println(ga);
        ht = (new WeightsApplier(ht, domain)).applyWeights(ga);
        (new HypertextWriter(domain)).writeGEXF(ht);
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

    static void testDBLoad() throws FileNotFoundException, UnsupportedEncodingException {
        String domain = "sstu.ru";
        DBConnector.setDefaultDBName(domain);
        DBLoader loader = new DBLoader();
//        loader.setNoHrefs(true);
//        loader.setTextLimit(2000);
        loader.setTextLimit(1);
        loader.setTempHrefs(true);
        HypertextStructure structure = loader.loadHypertextForCarrot2();
        HypertextWriter writer = new HypertextWriter(domain);
//        writer.writeCarrot2XML(structure);
        writer.writeMapIds(structure);
    }

    static void testCrawlBatch() throws FileNotFoundException, UnsupportedEncodingException {
//        String domain = "aksw.org";
        String domain = "sstu.ru";
        String url = "http://" + domain + "/";
        DBConnector.setDefaultDBName(domain);
        DBCrawler crawler = new DBCrawler(domain);
//        crawler.setTemplatePath("article#content");
        crawler.setTemplatePath("div.page-container");
        crawler.setReserveTemplatePaths(new String[]{"div#content"});
//        crawler.setSkipText(true);
        crawler.batchDBParse(url);
    }

//    static void parseMissedPages() throws IOException {
//        List<String> missed = new LinkedList<String>();
//        missed.add("http://sstu.ru/obshchestvennaya-zhizn/afisha/museum");
//        missed.add("http://sstu.ru/universitet/struktura.php");
//        missed.add("http://sstu.ru/news/predstaviteli-firmy-lg-zainteresovalis-proektom-uchenykh-inetm.html");
//        missed.add("http://sstu.ru/aspirantu/dissertation");
//        missed.add("http://sstu.ru/obrazovanie/fakultety-i-instituty/mfpit/struktura/dksh.php");
//        missed.add("http://sstu.ru/upravlenie/upravleniya-i-otdely/uit/okpm.php");
//        missed.add("http://sstu.ru/news/na-kafedre-fmtm-proshla-zashchita-magisterskikh-dissertatsiy.html");
//        missed.add("http://sstu.ru/nauka/nauchnye-izdaniya/innovatsionnaya-deyatelnost");
//        missed.add("http://sstu.ru/nauka/nauchnye-izdaniya/innovatsionnaya-deyatelnost/pravila-publikatsii.php");
//        missed.add("http://sstu.ru/nauka/nauchnye-izdaniya/innovatsionnaya-deyatelnost/redaktsionnaya-kollegiya.php");
//        missed.add("http://sstu.ru/nauka/nauchnye-izdaniya/innovatsionnaya-deyatelnost/arkhiv.php");
//        missed.add("http://sstu.ru/sotrudnichestvo/proekt-nanobridge.php");
//        missed.add("http://sstu.ru/upravlenie/uchenyy-sovet");
//        missed.add("http://sstu.ru/upravlenie/upravleniya-i-otdely/uiso/gazety-i-zhurnaly/zhurnal-novus-trend");
//        missed.add("http://sstu.ru/upravlenie/ofitsialnaya-informatsiya");
//        missed.add("http://sstu.ru/obrazovanie/obrazovatelnye-programmy/uchebnye-plany-ipu");
//        missed.add("http://sstu.ru/upravlenie/ofitsialnaya-informatsiya/uchebnye-plany-fgos-vo");
//        missed.add("http://sstu.ru/obrazovanie/obrazovatelnye-programmy/uchebnye-plany-fgos-vpo-zo");
//        missed.add("http://sstu.ru/obrazovanie/obrazovatelnye-programmy/uchebnye-plany-fgos-vpo-zs");
//
//        int lastIndex = 25857;
//        String domain = "sstu.ru";
//        DBConnector.setDefaultDBName(domain);
//        DBCrawler crawler = new DBCrawler(domain);
//        crawler.setTemplatePath("div.page-container");
//        crawler.setReserveTemplatePaths(new String[]{"div#content"});
//        crawler.parseCollectionToDB(missed, lastIndex);
//    }
}
