package org.sainnr.wgc.hypertext.parsers;

import org.sainnr.wgc.hypertext.Crawler;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vladimir on 17.05.2015.
 */
public class MultiParser {

    private static Log log = LogFactory.getLog(MultiParser.class);
    String templatePath;
    String[] reserveTemplatePaths;
    String domain;
    String pageUrl;
    String encoding = "UTF-8";
    Document doc;
    boolean skipText;

    public MultiParser(String domain, String pageUrl) throws IOException {
        this.domain = domain;
        this.pageUrl = pageUrl;
        init();
    }

    public MultiParser(String domain, String pageUrl, String encoding) throws IOException {
        this.domain = domain;
        this.pageUrl = pageUrl;
        this.encoding = encoding;
        init();
    }

    private void init() throws IOException {
        try {
//            this.doc = Jsoup.parse(new URL(pageUrl).openStream(), encoding, pageUrl);
            this.doc = Jsoup.connect(pageUrl).timeout(3000).get();
        } catch (IOException ex) {
            log.error("Error while extracting page: " + pageUrl, ex);
            throw new IOException("Connection problems? ", ex);
        }
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public HyperPage parsePage(){
        HyperPage page = new HyperPage();
        page.setUrl(pageUrl);
        page.setTitle(getTitleFromPage());
        if (!skipText) {
            page.setContent(getUniteTextBlockFromPage());
        }
        page.setOutcomingUrl(getLinksFromPage());
        return page;
    }

    public Set<String> getLinksFromPage(){
        HashSet<String> linksSet = new HashSet<String>();
        log.info("Collecting links from " + pageUrl);
        Elements links = doc.select("a[href]");

        if (links != null) {
            log.info("Links found: " + links.size());
            for (Element link : links) {
                String url = link.attr("abs:href");
                log.trace("Checking " + url);
                if (LinkParser.isNative(url, domain)
//                        && LinkParser.isWebpage(url)
                        && !LinkParser.inBlackList(url)){
                    if (Crawler.PURIFY){
                        url = "http://" + Crawler.purify(url);
                    }
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

    private String getUniteTextBlockFromPage(){
        String uniteText = "";
        Elements texts = doc.select(templatePath);
        if (texts != null) {
            log.info("Texts found with " + templatePath + " : " + texts.size());
            if (texts.size() > 0) {
                for (Element text : texts) {
                    uniteText += text.text() + " ";
                }
            } else if (reserveTemplatePaths != null && reserveTemplatePaths.length > 0) {
                for (String path : reserveTemplatePaths){
                    texts = doc.select(path);
                    if (texts == null){
                        continue;
                    }
                    log.info("Texts found with reserve " + path + " : " + texts.size());
                    for (Element text : texts) {
                        uniteText += text.text() + " ";
                    }
                    if (texts.size() > 0){
                        break;
                    }
                }
            }
        } else {
            log.warn("No texts found");
        }
        log.trace(uniteText);
//        return StringEscapeUtils.escapeXml(uniteText);
        return uniteText;
    }

    private String getTitleFromPage(){
        String fullTitle = "";
        Elements title = doc.select("title");
        if (title != null) {
            for (Element subtitle : title) {
                fullTitle += subtitle.text() + " ";
            }
            log.info("Title found: " + title.size());
        } else {
            log.warn("No title found");
        }
//        return StringEscapeUtils.escapeXml(fullTitle);
        return fullTitle;
    }

    public void setReserveTemplatePaths(String[] reserveTemplatePaths) {
        this.reserveTemplatePaths = reserveTemplatePaths;
    }

    public void setSkipText(boolean skipText) {
        this.skipText = skipText;
    }
}
