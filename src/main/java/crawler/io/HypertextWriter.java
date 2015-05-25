package crawler.io;

import crawler.HyperPage;
import crawler.HypertextStructure;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class HypertextWriter {

    public void writeMap(Map<String, Set<String>> mapToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("clustersMap.csv", "UTF-8");
        for (String url : mapToWrite.keySet()){
            for (String urlTo : mapToWrite.get(url)){
                writer.println(url + "," + urlTo + ",1.0");
            }
        }
        writer.close();
    }

    public void writeMap(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("clustersMap.csv", "UTF-8");
        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
        List<String> urlIndex = structureToWrite.getUrlIndex();
        for (HyperPage page : pagesToWrite){
            for (String urlTo : page.getOutcomingUrl()){
                writer.println(page.getId() + "," + urlIndex.indexOf(urlTo) + ",1.0");
            }
        }
        writer.close();
    }

    public void writeMapUrl(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("clustersMap.csv", "UTF-8");
        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
        List<String> urlIndex = structureToWrite.getUrlIndex();
        for (HyperPage page : pagesToWrite){
            for (String urlTo : page.getOutcomingUrl()){
                if (urlIndex.contains(urlTo)) {
                    writer.println("\"" + page.getUrl() + "\",\"" + urlTo + "\",1.0");
                }
            }
        }
        writer.close();
    }

    public void writeCarrot2XML(HypertextStructure structureToWrite)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("clustersCarrot2.xml", "UTF-8");

        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<searchresult>");
        Set<HyperPage> pagesToWrite = structureToWrite.getPages();
        for (HyperPage page : pagesToWrite){
            writer.println("  <document id=\"" + page.getId() + "\">");
            writer.println("     <title>" + page.getTitle() + "</title>");
            writer.println("     <url>" + page.getUrl() + "</url>");
            writer.println("     <snippet>" + page.getContent() + "</snippet>");
            writer.println("  </document>");
        }
        writer.println("</searchresult>");
        writer.close();
    }

}
