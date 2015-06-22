package org.sainnr.wgc.hypertext.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;

import java.io.*;
import java.util.*;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class HypertextReader {

    public HypertextStructure readCSVUrlMapToHypertext(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<String, Set<String>> pages = new HashMap<String, Set<String>>();
        List<String> urlIndex = new ArrayList<String>();
        List<String> filesIndex = new ArrayList<String>();
        Set<HyperPage> pageSet = new HashSet<HyperPage>();
        String line;
        while ((line = reader.readLine()) != null){
            String[] lineChunks = (new CSVParser()).parseLine(line);
            String fromUrl = lineChunks[0];
            String toUrl = lineChunks[1];
            double weight = Double.parseDouble(lineChunks[2]);
            Set<String> toSet;
            if (pages.get(fromUrl) == null){
                toSet = new HashSet<String>();
            } else {
                toSet = pages.get(fromUrl);
            }
            toSet.add(toUrl);
            pages.put(fromUrl, toSet);
            if (!urlIndex.contains(fromUrl)) {
                urlIndex.add(fromUrl);
            }
            if (!urlIndex.contains(toUrl)) {
                filesIndex.add(toUrl);
            }

            HyperPage page = new HyperPage();
            page.setId(urlIndex.indexOf(fromUrl));
            page.setUrl(fromUrl);
            page.setOutcomingUrl(toSet);
            page.setWeight(toUrl, weight);
            pageSet.add(page);
        }
        for (String url: urlIndex){
            if (filesIndex.contains(url)){
                filesIndex.remove(url);
            }
        }
        HypertextStructure structure = new HypertextStructure();
        structure.setUrlIndex(urlIndex);
        structure.setFilesIndex(filesIndex);
        structure.setPages(pageSet);
        return structure;
    }
}
