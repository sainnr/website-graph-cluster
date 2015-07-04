package org.sainnr.wgc.clustering.data;

import java.util.List;
import java.util.Set;

/**
 * Created by Vladimir on 02.07.2015.
 */
public class SingleCluster {

    int id;
    List<Integer> docIds;
    List<String> keys;
    double score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getDocIds() {
        return docIds;
    }

    public void setDocIds(List<Integer> docIds) {
        this.docIds = docIds;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SingleCluster{" +
                "\n\tid=" + id +
                "\n\tdocIds=" + docIds +
                "\n\tkeys=" + keys +
                "\n\tscore=" + score +
                "}\n";
    }
}
