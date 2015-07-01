package org.sainnr.wgc.clustering.io;

import prefuse.data.Table;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class ClusterWriter {

    public static final String FOLDER = "clusters";
    String domainString;

    public ClusterWriter(String domainString) {
        this.domainString = domainString;
    }

    public String writeCugarClustersPlain(Table table) throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFilename(0);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        for (int i = 0; i < cols; i++) {
            writer.print("\"" + table.getColumnName(i) + "\"");
            if (i != (cols - 1)) {
                writer.print(",");
            }
        }
        writer.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                writer.print("\"" + table.get(i, j) + "\"");
                if (j != (cols - 1)) {
                    writer.print(",");
                }
            }
            writer.println();
        }
        writer.close();
        return filename;
    }

    public String writeCugarClustersXML(Table table) throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFilename(1);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        //0-clusters


//        for (int i = 0; i < cols; i++) {
//            writer.print("\"" + table.getColumnName(i) + "\"");
//            if (i != (cols - 1)) {
//                writer.print(",");
//            }
//        }
//        writer.println();
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                writer.print("\"" + table.get(i, j) + "\"");
//                if (j != (cols - 1)) {
//                    writer.print(",");
//                }
//            }
//            writer.println();
//        }
        writer.close();
        return filename;
    }

    private String getFilename(int option){
        long time = Calendar.getInstance().getTime().getTime() / 1000;
        String ext = "";
        switch (option){
            case 0:
                ext = ".csv";
                break;
            case 1:
                ext = ".xml";
                break;
        }
        return FOLDER + "/" +
                "clust_" + domainString.replace(".","") + "_" + time + ext;
    }
}
