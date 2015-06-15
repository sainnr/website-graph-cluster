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

    String domain;
    String templatePath;
    String[] reserveTemplatePaths;
    String encoding;

    private static final Log log = LogFactory.getLog(Crawler.class);

    public Crawler(String domain) {
        this.domain = domain;
    }

    @Deprecated
    public Map<String, Set<String>> fullParseToMap(String startPage){
        long startTime = System.currentTimeMillis();

        Map<String, Set<String>> pagesParsed = new HashMap<String, Set<String>>();
        Stack<String> urlStack = new Stack<String>();
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
        Stack<String> urlStack = new Stack<String>();
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
        List<String> urlIndex = new ArrayList<String>();
        List<String> filesIndex = new ArrayList<String>();
        Set<HyperPage> pages = new HashSet<HyperPage>();

        MultiParser parser = null;
        try {
            parser = new MultiParser(domain, startPage, encoding);
        } catch (IOException e) {
            log.error("Cannot extract page " + startPage);
            return null;
        }
        parser.setTemplatePath(templatePath);
        parser.setReserveTemplatePaths(reserveTemplatePaths);
        HyperPage page = parser.parsePage();
        log.trace("Page " + page.getUrl() + " parsed");

        urlIndex.add(page.getUrl());
        page.setId(urlIndex.indexOf(page.getUrl()));
        pages.add(page);
        log.trace("Page ID " + page.getId());

        // assume all non-parsed as "files"
        for (String link : page.getOutcomingUrl()) {
            filesIndex.add(link);
        }
        structure.setPages(pages);
        structure.setUrlIndex(urlIndex);
        structure.setFilesIndex(filesIndex);

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return structure;
    }

    public HypertextStructure fullParseToHyperStructure(String startPage){
        long startTime = System.currentTimeMillis();

        HypertextStructure structure = new HypertextStructure();
        List<String> urlIndex = new ArrayList<String>();
        List<String> filesIndex = new ArrayList<String>();
        List<String> brokenLinks = new ArrayList<String>();
        Set<HyperPage> pages = new HashSet<HyperPage>();

        Stack<String> urlStack = new Stack<String>();
        urlStack.push(startPage);
        String curUrl;

        while (!urlStack.isEmpty()){
            curUrl = urlStack.pop();
            HyperPage curPage = null;
            try {
                curPage = parseSinglePage(curUrl);
            } catch (IOException e) {
                if (!brokenLinks.contains(curUrl)) {
                    brokenLinks.add(curUrl);
                }
                log.error("Cannot extract page " + curUrl);
                continue;
            }

            urlIndex.add(curUrl);
            curPage.setId(urlIndex.indexOf(curUrl));
            pages.add(curPage);
            log.trace("Page ID " + curPage.getId());

            for (String link : curPage.getOutcomingUrl()) {
                if (!LinkParser.isWebpage(link)){
                    filesIndex.add(link);
                    continue;
                }
                if (!urlStack.contains(link) && !urlIndex.contains(link)
                &&  !urlStack.contains(link + ".html") && !urlIndex.contains(link + ".html")
                &&  !urlStack.contains(link.replace(".html", "")) && !urlIndex.contains(link.replace(".html", ""))
                        ){
                    urlStack.push(link);
                } else {
                    log.trace("Skipping " + link);
                }
            }
        }

        structure.setPages(pages);
        structure.setUrlIndex(urlIndex);
        structure.setFilesIndex(filesIndex);
        structure.setBrokenUrls(brokenLinks);

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return structure;
    }

    private HyperPage parseSinglePage(String url) throws IOException {
        MultiParser parser = new MultiParser(domain, url, encoding);
        parser.setTemplatePath(templatePath);
        parser.setReserveTemplatePaths(reserveTemplatePaths);
        HyperPage page = parser.parsePage();
        log.trace("Page " + page.getUrl() + " parsed");
        return page;
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
}
