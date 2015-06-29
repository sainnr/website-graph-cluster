package org.sainnr.wgc.clustering;

import de.uni_leipzig.cugar.cluster.ClusterAlgorithm;
import de.uni_leipzig.cugar.cluster.ClusterAlgorithmBF;
import de.uni_leipzig.cugar.cluster.ClusterAlgorithmMCL;
import de.uni_leipzig.cugar.cluster.ClusterAlgorithmMCL2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import prefuse.data.Table;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class ClusteringAlgorithm {

    public enum Algorithm {BF, MCL, MCL2}
    private String filename;
    private ClusterAlgorithm algorithm;

    private static Log log = LogFactory.getLog(ClusteringAlgorithm.class);

    public ClusteringAlgorithm(Algorithm type, String filename) {
        this.filename = filename;
        this.algorithm = getAlgorithmInstance(type);
    }

    public ClusterAlgorithm getAlgorithmInstance(Algorithm type){
        switch (type){
            case BF: {
                return createBF();
            }
            case MCL: {
                return createMCL();
            }
            case MCL2: {
                return createMCL2();
            }
            default: {
                return createBF();
            }
        }
    }

    private ClusterAlgorithmBF createBF(){
        ClusterAlgorithmBF instance = new ClusterAlgorithmBF();
        instance.setFilename(filename);
        return instance;
    }

    private ClusterAlgorithmMCL createMCL(){
        ClusterAlgorithmMCL instance = new ClusterAlgorithmMCL();
        instance.setFilename(filename);
        return instance;
    }

    private ClusterAlgorithmMCL2 createMCL2(){
        ClusterAlgorithmMCL2 instance = new ClusterAlgorithmMCL2();
        instance.setFilename(filename);
        return instance;
    }

    public Table cluster(double threshold, String[] params){
        return cluster(null, threshold, params);
    }

    public Table cluster(String[] seeds, double threshold, String[] params){
        log.info("Clustering with " + algorithm);
        long timeStart = System.currentTimeMillis();
        Table table = algorithm.cluster(seeds, threshold, params[0], params[1], params[2], params[3]);
        long timeEnd = System.currentTimeMillis();
        log.info("Time spent: " + (timeEnd - timeStart) / 1000 + " sec.");

        if (log.isTraceEnabled()) {
            int rows = table.getRowCount();
            int cols = table.getColumnCount();
            log.trace("Results: cols " + cols + "; rows " + rows);
        }
        return table;
    }

}
