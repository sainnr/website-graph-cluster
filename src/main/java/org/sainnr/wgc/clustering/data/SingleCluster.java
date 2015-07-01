package org.sainnr.wgc.clustering.data;

import java.util.Set;

/**
 * Created by Vladimir on 02.07.2015.
 */
public class SingleCluster {

    int id;
    Set<Integer> docIds;
    Set<String> keys;
    double score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Integer> getDocIds() {
        return docIds;
    }

    public void setDocIds(Set<Integer> docIds) {
        this.docIds = docIds;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public void setKeys(Set<String> keys) {
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
