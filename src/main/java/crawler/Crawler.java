package crawler;

import crawler.parsers.LinkParser;
import crawler.parsers.MultiParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by Vladimir on 15.05.2015.
 */
public class Crawler {

    String domain;

    private static final Log log = LogFactory.getLog(Crawler.class);

    public Crawler(String domain) {
        this.domain = domain;
    }

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
        Set<HyperPage> pages = new HashSet<HyperPage>();

        MultiParser parser = new MultiParser(domain, startPage);
        parser.setTemplatePath("article#content");
        HyperPage page = parser.parsePage();
        log.trace("Page " + page.getUrl() + " parsed");

        urlIndex.add(page.getUrl());
        page.setId(urlIndex.indexOf(page.getUrl()));
        pages.add(page);
        log.trace("Page ID " + page.getId());

        structure.setPages(pages);
        structure.setUrlIndex(urlIndex);

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return structure;
    }

    public HypertextStructure fullParseToHyperStructure(String startPage){
        long startTime = System.currentTimeMillis();

        HypertextStructure structure = new HypertextStructure();
        List<String> urlIndex = new ArrayList<String>();
        Set<HyperPage> pages = new HashSet<HyperPage>();

        Stack<String> urlStack = new Stack<String>();
        urlStack.push(startPage);
        String curUrl;

        while (!urlStack.isEmpty()){
            curUrl = urlStack.pop();
            HyperPage curPage = parseSinglePage(curUrl);

            urlIndex.add(curUrl);
            curPage.setId(urlIndex.indexOf(curUrl));
            pages.add(curPage);
            log.trace("Page ID " + curPage.getId());

            for (String link : curPage.getOutcomingUrl()) {
                if (!LinkParser.isWebpage(link)){
                    urlIndex.add(link);
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

        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
        return structure;
    }

    private HyperPage parseSinglePage(String url){
        MultiParser parser = new MultiParser(domain, url);
        parser.setTemplatePath("article#content");
        HyperPage page = parser.parsePage();
        log.trace("Page " + page.getUrl() + " parsed");
        return page;
    }

}
