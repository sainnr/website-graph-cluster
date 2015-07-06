package org.sainnr.wgc.hypertext;

import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;
import org.sainnr.wgc.hypertext.parsers.LinkParser;
import org.sainnr.wgc.hypertext.parsers.MultiParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class Crawler {

    private static final Log log = LogFactory.getLog(Crawler.class);
    public static final boolean PURIFY = true;

    String domain;
    String templatePath;
    String[] reserveTemplatePaths;
    String encoding;
    boolean skipText;

    Stack<String> urlStack;
    List<String> urlIndex;
    List<String> mediaIndex;
    List<String> brokenLinks;
    Set<HyperPage> pages;

    public Crawler(String domain) {
        this.domain = domain;
    }

    @Deprecated
    public Map<String, Set<String>> fullParseToMap(String startPage){
        long startTime = System.currentTimeMillis();

        Map<String, Set<String>> pagesParsed = new HashMap<String, Set<String>>();
        urlStack = new Stack<String>();
        Set<String> linksOnPage;
        String curUrl;
        urlStack.push(startPage);

        while (!urlStack.empty()){
            curUrl = urlStack.pop();
            linksOnPage = LinkParser.getLinksFromPage(domain, curUrl);
            for (String link : linksOnPage){
                if(!urlStack.contains(link) && !pagesParsed.containsKey(link) && LinkParser.isWebpage(link)){
                    urlStack.push(link);
                }
            }
            pagesParsed.put(curUrl, linksOnPage);
            log.trace("Stack size: " + urlStack.size() + "; parsed: " + pagesParsed.size());
            log.info("Fetching " + curUrl + " (" + linksOnPage.size() + ")");
        }
        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return pagesParsed;

    }

    @Deprecated
    public Map<String, Set<String>> singleParseToMap(String startPage){
        long startTime = System.currentTimeMillis();

        Map<String, Set<String>> pagesParsed = new HashMap<String, Set<String>>();
        urlStack = new Stack<String>();
        Set<String> linksOnPage;
        String curUrl;
        urlStack.push(startPage);

        curUrl = urlStack.pop();
        linksOnPage = LinkParser.getLinksFromPage(domain, curUrl);
        for (String link : linksOnPage){
            if(!urlStack.contains(link) && !pagesParsed.containsKey(link)){
                urlStack.push(link);
            }
        }
        pagesParsed.put(curUrl, linksOnPage);
        log.trace("Stack size: " + urlStack.size() + "; parsed: " + pagesParsed.size());
        log.info("Fetching " + curUrl + " (" + linksOnPage.size() + ")");

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return pagesParsed;
    }

    public HypertextStructure singleParseToHyperStructure(String startPage){
        long startTime = System.currentTimeMillis();

        HypertextStructure structure = new HypertextStructure();
        urlIndex = new ArrayList<String>();
        mediaIndex = new ArrayList<String>();
        pages = new HashSet<HyperPage>();

        MultiParser parser = null;
        try {
            parser = new MultiParser(domain, startPage, encoding);
        } catch (IOException e) {
            log.error("Cannot extract page " + startPage);
            return null;
        }
        parser.setTemplatePath(templatePath);
        parser.setReserveTemplatePaths(reserveTemplatePaths);
        parser.setSkipText(skipText);
        HyperPage page = parser.parsePage();
        log.trace("Page " + page.getUrl() + " parsed");

        urlIndex.add(page.getUrl());
        page.setId(urlIndex.indexOf(page.getUrl()));
        pages.add(page);
        log.trace("Page ID " + page.getId());

        // assume all non-parsed as "files"
        for (String link : page.getOutcomingUrl()) {
            mediaIndex.add(link);
        }
        structure.setPages(pages);
        structure.setUrlIndex(urlIndex);
        structure.setFilesIndex(mediaIndex);

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return structure;
    }

    public HypertextStructure fullParseToHyperStructure(String startPage){
        long startTime = System.currentTimeMillis();

        HypertextStructure structure = new HypertextStructure();
        urlIndex = new ArrayList<String>();
        mediaIndex = new ArrayList<String>();
        brokenLinks = new ArrayList<String>();
        pages = new HashSet<HyperPage>();

        if (PURIFY) {
            startPage = purify(startPage);
        }
        urlStack = new Stack<String>();
        urlStack.push(startPage);
        String curUrl;

        while (!urlStack.isEmpty()){
            log.info("Remaining stack size: " + urlStack.size());
            curUrl = urlStack.pop();
            HyperPage curPage = null;
            try {
                if (!brokenLinks.contains(curUrl)) {
                    curPage = parseSinglePage(curUrl);
                } else {
                    log.trace("Skipping failed url: " + curUrl);
                    continue;
                }
            } catch (IOException e) {
                brokenLinks.add(curUrl);
                log.error("Cannot extract page " + curUrl);
                continue;
            }

            urlIndex.add(curUrl);
            curPage.setId(urlIndex.indexOf(curUrl));
            pages.add(curPage);
            log.trace("Page ID " + curPage.getId());

            for (String link : curPage.getOutcomingUrl()) {
                if (!LinkParser.isWebpage(link)){
                    if (!mediaIndex.contains(link)) {
                        mediaIndex.add(link);
                    }
                    continue;
                }
                if (PURIFY) {
                    link = purify(link);
                }
                if (!isVisited(link)){
                    urlStack.push(link);
                } else {
                    log.trace("Skipping " + link);
                }
            }
        }

        structure.setPages(pages);
        structure.setUrlIndex(urlIndex);
        structure.setFilesIndex(mediaIndex);
        structure.setBrokenUrls(brokenLinks);

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return structure;
    }

    protected HyperPage parseSinglePage(String url) throws IOException {
        if (PURIFY){
            url = "http://" + url;
        }
        MultiParser parser = new MultiParser(domain, url, encoding);
        parser.setTemplatePath(templatePath);
        parser.setReserveTemplatePaths(reserveTemplatePaths);
        parser.setSkipText(skipText);
        HyperPage page = parser.parsePage();
        log.trace("Page " + page.getUrl() + " parsed");
        return page;
    }

    public String purify(String url){
        if (url == null || url.length() == 0){
            return url;
        }
        if (url.startsWith("http://")){
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        } else {
            log.warn("URL has no http/https prefix: " + url);
        }
        if (url.startsWith("www.")){
            url = url.substring(4);
        }
        if (url.endsWith("/")){
            url = url.substring(0, url.length()-1);
        }
        int sharp = url.indexOf('#');
        if (sharp != -1){
            url = url.substring(0, sharp);
        }
        return url;
    }

    protected boolean isVisited(String url){
        if (url == null || url.length() == 0){
            return false;
        }
        if (url.endsWith(".html")) {
            url = url.substring(0, url.length()-5);
        }
        return urlStack.contains(url) || urlIndex.contains(url)
                ||  urlStack.contains(url + ".html") || urlIndex.contains(url + ".html");
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setReserveTemplatePaths(String[] reserveTemplatePaths) {
        this.reserveTemplatePaths = reserveTemplatePaths;
    }

    public void setSkipText(boolean skipText) {
        this.skipText = skipText;
    }
}
