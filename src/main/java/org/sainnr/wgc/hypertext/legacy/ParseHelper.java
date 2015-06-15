/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sainnr.wgc.hypertext.legacy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author sainnr
 */
public class ParseHelper {

    private static String[] blackList = new String[]{
            "aksw.org/model",
            "aksw.org/history",
            "aksw.org/source",
            "aksw.org/resource",
            "aksw.org/list",
            "aksw.org/view",
    };
    private static Log log = LogFactory.getLog(ParseHelper.class);
    
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
                if (ParseHelper.isNative(url, siteUrl)
                        && ParseHelper.isWebpage(url)
                        && !ParseHelper.inBlackList(url)){
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

    public static Set<String> getTextBlockFromPage(String pageUrl, String templatePath/*, boolean unwrap*/){
        return getTextBlockFromPage(pageUrl, templatePath, "UTF-8");
    }

    public static Set<String> getTextBlockFromPage(String pageUrl, String templatePath, String encoding){
        HashSet<String> textsSet = new HashSet<String>();
        Elements texts;
        try {
            Document doc = Jsoup.parse(new URL(pageUrl).openStream(), encoding, pageUrl);
            texts = doc.select(templatePath);
        } catch (IOException ex){
            log.error("Error while extracting texts on page: " + pageUrl, ex);
            throw new RuntimeException("Connection problems? ", ex);
        }
        if (texts != null) {
            log.info("Els found: " + texts.size());
            for (Element text : texts) {
//                if (unwrap){
//                    Logger.getLogger(ParseHelper.class.getName()).log(Level.INFO, "Do unwrap");
//                    unwrapElements(text, textsSet);
//                }
                textsSet.add(text.text());
            }
        } else {
            log.warn("No texts found");
        }
        return textsSet;
    }

//    private static void unwrapElements(Element startElement, Set<String> texts){
//        Stack<Elements> toUnwrap = new Stack<Elements>();
//        toUnwrap.add(startElement.select("*"));
//        while (!toUnwrap.isEmpty()){
//            Elements temp = toUnwrap.pop();
//            for (Element el : temp){
//                toUnwrap.add(el.select("*"));
//                texts.add(el.text());
//            }
//        }
//    }

    private static boolean isWebpage(String url) {
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
