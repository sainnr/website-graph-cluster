package org.sainnr.wgc.clustering;

import org.apache.log4j.*;
import org.sainnr.wgc.clustering.data.ClustersIntersection;
import org.sainnr.wgc.clustering.data.SingleCluster;
import org.sainnr.wgc.clustering.io.Carrot2XMLParser;
import org.sainnr.wgc.clustering.io.ClusterWriter;
import org.sainnr.wgc.clustering.io.CugarXMLParser;
import prefuse.data.Table;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vladimir on 02.07.2015.
 */
public class ClusteringConsole {

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
        RollingFileAppender appender = new RollingFileAppender(pattern, "logs/clustering.log");
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.TRACE);
        rootLogger.addAppender(appender);
    }

    public static void main(String[] args) throws IOException, XMLStreamException {
        setFileLogger();
        testClustering();
    }

    static void testClustering() throws FileNotFoundException, UnsupportedEncodingException {
//        String htFile = "hypertext/ht_cm_aksworg_1434527868.csv";
        String htFile = "hypertext/ht_cm_aksworg_1435962555.csv";
//        String htFile = "hypertext/ht_cm_ssturu_1435871879.csv";
        String domain = "aksw.org";
//        String domain = "sstu.ru";
        double threshold = 1.0;
        String[] params = new String[4];
        params[0] = "Superset";
//        params[1] = "off";
//        params[2] = "0";
//        params[3] = "off";
//        params[1] = "SGD* + RP";
//        params[2] = "8000";
//        params[3] = "off";
        params[1] = "on";
        params[2] = "0";
        params[3] = "on";
        ClusteringAlgorithm algo = new ClusteringAlgorithm(ClusteringAlgorithm.Algorithm.BF, htFile);
        Table results = algo.cluster(threshold, params);
//        (new ClusterWriter(domain)).writeCugarClustersPlain(results);
        (new ClusterWriter(domain)).writeCugarClustersXML(results);
    }

    static List<SingleCluster> testReadCarrot2Clusters() throws FileNotFoundException, XMLStreamException {
        String file = "clusters/carrot_ht_cc2_aksworg_1435962555.xml";
        List<SingleCluster> clusters = (new Carrot2XMLParser()).readFile(new File(file));
        System.out.println("Carrot clusters found: " + clusters.size());
        return clusters;
    }

    static List<SingleCluster> testReadCugarClusters() throws FileNotFoundException, XMLStreamException {
        String file = "clusters/clust_aksworg_1435962668.xml";
        List<SingleCluster> clusters = (new CugarXMLParser()).readFile(new File(file));
        System.out.println("Cugar clusters found: " + clusters.size());
        return clusters;
    }

    static void tesIntersectClusters() throws FileNotFoundException, XMLStreamException {
        ClustersIntersection intersection = new ClustersIntersection(testReadCarrot2Clusters());
        intersection.evaluateIntersection(testReadCugarClusters());
        double[] precision = intersection.getMaxPrecisionValues();
        double[] recall = intersection.getMaxRecallValues();
        double[] fMeasure = intersection.getFMeasureValues();
        System.out.println("Precisions: " + Arrays.toString(precision) + "; Avg: " + intersection.getAvgPrecision());
        System.out.println("Recalls: " + Arrays.toString(recall) + "; Avg: " + intersection.getAvgRecall());
        System.out.println("F-Measures: " + Arrays.toString(fMeasure) + "; Avg: " + intersection.getAvgFMeasure());
    }

}
