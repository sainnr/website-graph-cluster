package org.sainnr.wgc.statistics.io;

import com.google.api.services.analytics.model.GaData;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir on 12.06.2015.
 */
public class GaDataWriter {

    public void writeCsv(GaData dataToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("gadata.csv", "UTF-8");
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
                for (String column : row) {
                    writer.print("\"" + column + "\";");
                }
                writer.println();
            }
        }
        writer.close();
    }

}
