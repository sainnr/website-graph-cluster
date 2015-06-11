package org.sainnr.wgc.crawler.parsers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Vladimir on 17.05.2015.
 */
public class LinkParser {

    private static String[] blackList = new String[]{
            "aksw.org/model",
            "aksw.org/history",
            "aksw.org/source",
            "aksw.org/resource",
            "aksw.org/list",
            "aksw.org/view",
    };
    private static Log log = LogFactory.getLog(LinkParser.class);

    public static Set<String> getLinksFromPage(String siteUrl, String pageUrl){
        HashSet<String> linksSet = new HashSet<String>();
        Elements links = null;
        try {
            log.info("Collecting links from " + pageUrl);
            Document doc = Jsoup.connect(pageUrl).get();
            links = doc.select("a[href]");
        } catch (IOException ex){
            log.error("Error while extracting links on page: " + pageUrl, ex);
        }
        if (links != null) {
            log.info("Links found: " + links.size());
            for (Element link : links) {
                String url = link.attr("abs:href");
                log.trace("Checking " + url);
                if (LinkParser.isNative(url, siteUrl)
//                        && LinkParser.isWebpage(url)
                        && !LinkParser.inBlackList(url)){
                    linksSet.add(url);
                } else{
                    log.trace("Skip it");
                }
            }
        } else {
            log.warn("No links found");
        }
        return linksSet;
    }

    public static boolean inBlackList(String url){
        for (String buzz : blackList){
            if (url.contains(buzz)){
                log.trace(" - blacklisted");
                return true;
            }
        }
        return false;
    }

    public static boolean isNative(String url, String siteUrl){
        return url.startsWith("http://" + siteUrl)
                || url.startsWith("http://www." + siteUrl)
                || url.startsWith("https://" + siteUrl)
                || url.startsWith("https://www." + siteUrl)
                ;
    }


    public static boolean isWebpage(String url) {
        boolean isWebPage =
                !(url.toLowerCase(Locale.ENGLISH).endsWith(".jpg")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".png")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".gif")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".swf")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".fla")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".doc")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".xls")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".pdf")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".docx")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".xlsx")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".ppt")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".pptx")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".tif")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".rar")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".zip")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".tar")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".7z")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".odt")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".mpg")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".avi")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".mp3")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".mp4")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".mov")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".mpeg")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".f4v")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".wmv")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".flv")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".rdf")
                        || url.toLowerCase(Locale.ENGLISH).endsWith(".ttl")
                        || url.toLowerCase(Locale.ENGLISH).endsWith("#"));
        log.trace(" - is a webpage: " + isWebPage);
        return isWebPage;
    }

}
