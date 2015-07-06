package org.sainnr.wgc.hypertext.io;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class HypertextWriter {

    private static final Log log = LogFactory.getLog(HypertextWriter.class);
    public static final String FOLDER = "hypertext";
    String domainSuffix;

    public HypertextWriter(String domainSuffix) {
        this.domainSuffix = domainSuffix;
    }

    @Deprecated
    public void writeMap(Map<String, Set<String>> mapToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(getFileName(2), "UTF-8");
        for (String url : mapToWrite.keySet()){
            for (String urlTo : mapToWrite.get(url)){
                writer.println(url + "," + urlTo + ",1.0");
            }
        }
        writer.close();
    }

    public String writeMapIds(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFileName(1);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
        List<String> urlIndex = structureToWrite.getUrlIndex();
        List<String> filesIndex = structureToWrite.getFilesIndex();
        for (HyperPage page : pagesToWrite){
            log.trace("Writing page: " + page.getUrl());
            if (page.getOutcomingUrl() == null){
                continue;
            }
            for (String urlTo : page.getOutcomingUrl()){
                int indexTo = ((filesIndex!=null && filesIndex.contains(urlTo))
                        ? filesIndex.indexOf(urlTo) : urlIndex.indexOf(urlTo) );
                if (indexTo == -1){
                    log.warn("Cannot find index for url " + urlTo);
                }
                writer.println(page.getId() + "," + indexTo + "," + page.getWeight(urlTo));
            }
        }
        writer.close();
        return filename;
    }

    public String writeIndex(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFileName(4);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        for (String url : structureToWrite.getUrlIndex()){
            writer.println("\"" + url + "\"," + structureToWrite.getUrlIndex().indexOf(url));
        }
        writer.close();
        return filename;
    }

    public String writeBrokenLinks(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFileName(5);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        for (String url : structureToWrite.getBrokenUrls()){
            writer.println("\"" + url + "\"," + structureToWrite.getBrokenUrls().indexOf(url));
        }
        writer.close();
        return filename;
    }

    public String writeMapUrl(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFileName(1);
        log.trace("Writing to: " + filename);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
//        List<String> urlIndex = structureToWrite.getUrlIndex();
        for (HyperPage page : pagesToWrite){
            log.trace("Writing page: " + page.getUrl());
            if (page.getOutcomingUrl() == null){
                continue;
            }
            for (String urlTo : page.getOutcomingUrl()){
//                if (urlIndex.contains(urlTo)) {
                    writer.println("\"" + page.getUrl() + "\",\"" + urlTo + "\"," + page.getWeight(urlTo));
//                }
            }
        }
        writer.close();
        return filename;
    }

    public String writeCarrot2XML(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFileName(2);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<searchresult>");
        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
        for (HyperPage page : pagesToWrite){
            writer.println("  <document id=\"" + page.getId() + "\">");
            writer.println("     <title><![CDATA[" + page.getTitle() + "]]></title>");
            writer.println("     <url>" + StringEscapeUtils.escapeXml(page.getUrl()) + "</url>");
            writer.println("     <snippet><![CDATA[" + (
                    page.getContent() != null ? page.getContent() : "") + "]]></snippet>");
            writer.println("  </document>");
        }
        writer.println("</searchresult>");
        writer.close();
        return filename;
    }

    public String writeGEXF(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filename = getFileName(3);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\">");
        writer.println("<graph defaultedgetype=\"directed\">");

        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
        List<String> urlIndex = structureToWrite.getUrlIndex();
        List<String> filesIndex = structureToWrite.getFilesIndex();
        writer.println("\t<nodes>");
        for (HyperPage page : pagesToWrite){
            writer.println("\t\t<node id=\"" + page.getId()
                    + "\" label=\"" + URLEncoder.encode(page.getUrl(), "UTF-8") + "\"/>");
        }
        int maxUrlsIndex = urlIndex.size();
        if (filesIndex != null) {
            for (String file : filesIndex) {
                writer.println("\t\t<node id=\"" + (filesIndex.indexOf(file) + maxUrlsIndex)
                        + "\" label=\"" + URLEncoder.encode(file, "UTF-8") + "\"/>");
            }
        }
        writer.println("\t</nodes>");
        writer.println("\t<edges>");
        int i = 0;
        for (HyperPage page : pagesToWrite){
            if (page.getOutcomingUrl() == null){
                continue;
            }
            for (String urlTo : page.getOutcomingUrl()) {
                int targetId;
                if (urlIndex.contains(urlTo)) {
                    targetId = urlIndex.indexOf(urlTo);
                } else if (filesIndex != null && filesIndex.contains(urlTo)){
                    targetId = filesIndex.indexOf(urlTo) + maxUrlsIndex;
                } else {
                    targetId = -1;
                }
                writer.println("\t\t<edge id=\"" + i +
                        "\" source=\"" + page.getId() +
                        "\" target=\"" + targetId +
                        "\" weight=\"" + page.getWeight(urlTo) + "\"/>");
                i++;
            }
        }
        writer.println("\t</edges>");
        writer.println("</graph>");
        writer.println("</gexf>");
        writer.close();
        return filename;
    }

    private String getFileName(int type){
        long time = Calendar.getInstance().getTime().getTime() / 1000;
        String suffix = "";
        String ext = "txt";
        switch(type){
            case 1: {
                suffix = "cm";
                ext = "csv";
                break;
            }
            case 2: {
                suffix = "cc2";
                ext = "xml";
                break;
            }
            case 3: {
                suffix = "cgf";
                ext = "gexf";
                break;
            }
            case 4: {
                suffix = "index";
                ext = "csv";
                break;
            }
            case 5: {
                suffix = "broken";
                ext = "csv";
                break;
            }
            default:
        }
        return FOLDER + "/" +
                "ht_" + suffix + "_" + domainSuffix.replace(".","") + "_" + time + "." + ext;
    }
}
