package org.sainnr.wgc.clustering.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by Vladimir on 05.07.2015.
 */
public class ClustersIntersection {

    private static final Log log = LogFactory.getLog(ClustersIntersection.class);
    List<SingleCluster> relevantSet; // relevant clusters
    int[][] evalCount;
    double[][] evalPrecision;
    double[][] evalRecall;
    double[][] evalFMeasure;

    public ClustersIntersection(List<SingleCluster> relevantSet) {
        this.relevantSet = relevantSet;
    }

    public int[][] evaluateIntersection(List<SingleCluster> targetSet){
        if (targetSet == null || targetSet.size() == 0){
            return null;
        }
        evalCount = new int[targetSet.size()][relevantSet.size()];
        evalPrecision = new double[targetSet.size()][relevantSet.size()];
        evalRecall = new double[targetSet.size()][relevantSet.size()];
        evalFMeasure = new double[targetSet.size()][relevantSet.size()];
        int i = 0;
        for (SingleCluster targetEntry : targetSet){
            List<Integer> targetDocIds = targetEntry.getDocIds();
            int j = 0;
            for (SingleCluster relevantEntry : relevantSet){
                List<Integer> relevantDocs = relevantEntry.getDocIds();
                int intersection = getIntersection(targetDocIds, relevantDocs);
                double precision = (1.0 * intersection) / targetDocIds.size();
                double recall = (1.0 * intersection) / relevantDocs.size();
                double fMeasure = 2 * precision * recall / (precision + recall);
                evalCount[i][j] = intersection;
                evalPrecision[i][j] = precision;
                evalRecall[i][j] = recall;
                evalFMeasure[i][j] = fMeasure;
                j++;
            }
            i++;
        }
        log();
        return evalCount;
    }

    public double getAvgPrecision(){
        double sum = 0.0;
        for (double val : getMaxPrecisionValues()){
            sum += val;
        }
        return sum / evalPrecision.length;
    }

    public double getAvgRecall(){
        double sum = 0.0;
        for (double val : getMaxRecallValues()){
            sum += val;
        }
        return sum / evalRecall.length;
    }

    public double getAvgFMeasure(){
        double sum = 0.0;
        for (double val : getFMeasureValues()){
            sum += val;
        }
        return sum / evalFMeasure.length;
    }

    public double[] getMaxPrecisionValues(){
        double[] maxPrecisions = new double[evalPrecision.length];
        int i = 0;
        for (double[] row : evalPrecision){
            maxPrecisions[i] = maxDouble(row);
            i++;
        }
        return maxPrecisions;
    }

    public double[] getMaxRecallValues(){
        double[] maxRecalls = new double[evalRecall.length];
        int i = 0;
        for (double[] row : evalRecall){
            maxRecalls[i] = maxDouble(row);
            i++;
        }
        return maxRecalls;
    }

    public double[] getMaxFMeasureValuesClean(){
        double[] maxFMeasures = new double[evalFMeasure.length];
        int i = 0;
        for (double[] row : evalFMeasure){
            maxFMeasures[i] = maxDouble(row);
            i++;
        }
        return maxFMeasures;
    }
    public double[] getFMeasureValues(){
        double[] maxPrecisions = getMaxPrecisionValues();
        double[] maxRecalls = getMaxRecallValues();
        double[] maxFMeasures = new double[maxPrecisions.length];
        for (int i = 0; i< maxFMeasures.length; i++){
            maxFMeasures[i] = 2 * maxPrecisions[i] * maxRecalls[i] / (maxPrecisions[i] + maxRecalls[i]);
        }
        return maxFMeasures;
    }

    double maxDouble(double[] input){
        double max = 0.0;
        for (double val : input){
            if (val > max){
                max = val;
            }
        }
        return max;
    }

    void log(){
        StringBuilder sbC = new StringBuilder("Intersections count:\n");
        StringBuilder sbR = new StringBuilder("Recall:\n");
        StringBuilder sbP = new StringBuilder("Precision:\n");
        StringBuilder sbF = new StringBuilder("F-Measure:\n");
        for (int i = 0; i < evalCount.length; i++) {
            int[] rowC = evalCount[i];
            double[] rowP = evalPrecision[i];
            double[] rowR = evalRecall[i];
            double[] rowF = evalFMeasure[i];
            for (int j = 0; j < rowC.length; j++) {
                sbC.append(rowC[j]).append("\t");
                sbR.append(rowR[j]).append("\t");
                sbP.append(rowP[j]).append("\t");
                sbF.append(rowF[j]).append("\t");
            }
            sbC.append("\n");
            sbR.append("\n");
            sbP.append("\n");
            sbF.append("\n");
        }
        log.info(sbC);
        log.info(sbR);
        log.info(sbP);
        log.info(sbF);
    }

    public int getIntersection(List<Integer> clust1, List<Integer> clust2) {
        log.trace("Sizes: 1: " + clust1.size() + "; 2: " + clust2.size());
        boolean set1IsLarger = clust1.size() > clust2.size();
        List<Integer> cloneSet = new LinkedList<Integer>(set1IsLarger ? clust2 : clust1);
        cloneSet.retainAll(set1IsLarger ? clust1 : clust2);
        return cloneSet.size();
    }
}
