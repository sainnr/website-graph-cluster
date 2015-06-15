package org.sainnr.wgc.statistics.io;

import com.google.api.services.analytics.model.GaData;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir on 12.06.2015.
 */
public class GaDataWriter {

    public static final String FOLDER = "gadata";
    String domainSuffix;
    String startDate;
    String endDate;
    String method;

    public GaDataWriter(String method, String domainSuffix, String startDate, String endDate) {
        this.domainSuffix = domainSuffix;
        this.startDate = startDate;
        this.endDate = endDate;
        this.method = method;
    }

    public String writeCsv(GaData dataToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFilename();
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        if (dataToWrite.getRows() == null || dataToWrite.getRows().isEmpty()) {
            writer.println("No results Found.");
        } else {
            // Print column headers.
            for (GaData.ColumnHeaders header : dataToWrite.getColumnHeaders()) {
                writer.print("\"" + header.getName() + "\";");
            }
            writer.println();

            // Print actual data.
            for (List<String> row : dataToWrite.getRows()) {
                int i = 0;
                for (String column : row) {
                    writer.print("\"" + filter(column) + "\";");
                    i++;
                }
                writer.println();
            }
        }
        writer.close();
        return filename;
    }

    private String filter(String url){
        if (url.endsWith("'") || url.endsWith("\\")){
            url = url.substring(0, url.length() - 2);
        }
        return url;
    }

    private String getFilename(){
        return FOLDER + "/" +
                "ga_" + method + "_" + domainSuffix.replace(".","") + "_" + startDate + "_" + endDate + ".csv";
    }

}
