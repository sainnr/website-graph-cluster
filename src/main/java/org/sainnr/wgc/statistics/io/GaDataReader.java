package org.sainnr.wgc.statistics.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import org.sainnr.wgc.statistics.data.GaVisitedPageEntry;
import org.sainnr.wgc.statistics.data.GaVisitedPagesStructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Vladimir on 13.06.2015.
 */
public class GaDataReader {

    String domainSuffix;

    public GaDataReader(String domainSuffix) {
        this.domainSuffix = domainSuffix;
    }

    public GaVisitedPagesStructure readCSVVisitedPages(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<String, Set<GaVisitedPageEntry>> pages = new HashMap<String, Set<GaVisitedPageEntry>>();
        List<String> urlIndex = new ArrayList<String>();
        String line = reader.readLine(); // skip headers
        while ((line = reader.readLine()) != null){
//            System.out.println(line);
            String[] lineChunks = (new CSVParser(';')).parseLine(line);
            String nextUrl = format(lineChunks[0]);
            String url = format(lineChunks[1]);
            double visits = Double.parseDouble(lineChunks[2]);
            double timeOnPage = Double.parseDouble(lineChunks[3]);
            if (!urlIndex.contains(url)) {
                urlIndex.add(url);
            }

            GaVisitedPageEntry page = new GaVisitedPageEntry();
            page.setId(urlIndex.indexOf(url));
            page.setUrl(url);
            page.setNextUrl(nextUrl);
            page.setNumOfVisits(visits);
            page.setTimeOnPage(timeOnPage);
            Set<GaVisitedPageEntry> toSet;
            if (pages.get(url) == null){
                toSet = new HashSet<GaVisitedPageEntry>();
            } else {
                toSet = pages.get(url);
            }
            toSet.add(page);
            pages.put(url, toSet);
        }
        GaVisitedPagesStructure structure = new GaVisitedPagesStructure();
        structure.setUrlIndex(urlIndex);
        structure.setPages(pages);
        return structure;
    }

    private String format(String url){
        if (url.length() > 1 && url.endsWith("/")){
            url = url.substring(0, url.length()-2);
        }
        return "http://" + domainSuffix + url.toLowerCase();
    }
}
