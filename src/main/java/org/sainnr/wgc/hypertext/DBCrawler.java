package org.sainnr.wgc.hypertext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.io.DBFlusher;
import org.sainnr.wgc.hypertext.parsers.LinkParser;

import java.io.IOException;
import java.util.*;

/**
 * Created by Vladimir on 06.07.2015.
 */
public class DBCrawler extends Crawler {

    public static final int BATCH_SIZE = 100;
    private static final Log log = LogFactory.getLog(DBCrawler.class);

    public DBCrawler(String domain) {
        super(domain);
    }

    public void batchDBParse(String startPage){
        long startTime = System.currentTimeMillis();
        urlStack = new Stack<String>();
        urlIndex = new ArrayList<String>();
        mediaIndex = new ArrayList<String>();
        brokenLinks = new ArrayList<String>();
        pages = new HashSet<HyperPage>();
//
//        if (PURIFY) {
//            startPage = purify(startPage);
//        }
        urlStack.push(startPage);
        String curUrl;

        while (!urlStack.isEmpty()){
            log.info("Remaining stack size: " + urlStack.size());
            curUrl = urlStack.pop();
            HyperPage curPage = null;
            if (!brokenLinks.contains(curUrl)) {
                try {
                    curPage = parseSinglePage(curUrl);
                } catch (IOException e) {
                    brokenLinks.add(curUrl);
                    log.error("Cannot extract page " + curUrl);
                    continue;
                }
            } else {
                log.info("Skipping failed url: " + curUrl);
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
//                if (PURIFY) {
//                    link = purify(link);
//                }
                if (!isVisited(link)){
                    urlStack.push(link);
                } else {
                    log.trace("Skipping " + link);
                }
            }
            if (pages.size() > BATCH_SIZE){
                flush();
            }
        }
        if (pages.size() > 0){
            flush();
        }
        long endTime = System.currentTimeMillis();
        log.info("Parsing took " + (endTime - startTime) / 1000 + " s");
    }

    public void parseCollectionToDB(Collection<String> missed, int lastIndex) throws IOException {
        mediaIndex = new ArrayList<String>();
        pages = new HashSet<HyperPage>();
        int i = 0;
        for (String url : missed){
            if (!LinkParser.isWebpage(url)){
                if (!mediaIndex.contains(url)) {
                    mediaIndex.add(url);
                }
                continue;
            }
            if (PURIFY){
                url = purify(url);
            }
            HyperPage page = parseSinglePage(url);
            page.setId(lastIndex + i);
            pages.add(page);
            i++;
        }
        log.info("Missed pages were added: " + pages.size());
        flush();
    }

    synchronized void flush(){
        log.info("Flushing pages: " + pages.size());
        log.trace(pages);
        (new DBFlusher()).flushPages(pages, mediaIndex);
        pages.removeAll(pages);
        log.trace("Remaining size: " + pages.size());
    }

}
