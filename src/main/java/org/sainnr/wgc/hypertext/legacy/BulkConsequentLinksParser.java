package org.sainnr.wgc.hypertext.legacy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.sainnr.groups.core.org.sainnr.wgc.graph.Node;
//import org.sainnr.groups.core.org.sainnr.wgc.graph.dao.EdgeDAO;
//import org.sainnr.groups.core.org.sainnr.wgc.graph.dao.NodeDAO;

import java.util.*;

/**
 * @author sainnr
 * @since 30.05.14
 */
@Deprecated
public class BulkConsequentLinksParser {

    private static final Log log = LogFactory.getLog(BulkConsequentLinksParser.class);
    private String baseDomain;
//    Map<String, Set<String>> pagesParsed;

    public BulkConsequentLinksParser(String baseDomain) {
        this.baseDomain = baseDomain;
    }

    public Map<String, Set<String>> parse(String startUrl) {
        long startTime = System.currentTimeMillis();

        Map<String, Set<String>> pagesParsed = new HashMap<String, Set<String>>();
        Stack<String> urlStack = new Stack<String>();
        Set<String> linksOnPage;
        String curUrl;
        urlStack.push(startUrl);

        while (!urlStack.empty()){
            curUrl = urlStack.pop();
            linksOnPage = ParseHelper.getLinksFromPage(baseDomain, curUrl);
            for (String link : linksOnPage){
                if(!urlStack.contains(link) && !pagesParsed.containsKey(link)){
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

    private void store(){
        long startTime = System.currentTimeMillis();

//        NodeDAO nDao = new NodeDAO();
//        Set<String> foundUrls = pagesParsed.keySet();
//        Set<String> existingUrls = nDao.findAllUrls();
//        Set<String> urlsToStore = new HashSet<String>();
//
//        for (String url : foundUrls){
//            if(!existingUrls.contains(Node.prepareUrl(url))){
//                urlsToStore.add(url);
//            }
//        }
//        nDao.storeUrls(urlsToStore);
//        for (String url : foundUrls){
//            storeEdges(url);
//        }
        long endTime = System.currentTimeMillis();
        log.info("Storing took " + (endTime - startTime) / 1000 + " s");
    }

    private void storeEdges(String url){
//        NodeDAO nDao = new NodeDAO();
//        EdgeDAO eDao = new EdgeDAO();
//        Set<Integer> idsToStore = new HashSet<Integer>();
//        Map<String, Node> nodes = nDao.findAllAccessByUrl();
//        Set<String> links = pagesParsed.get(url);
//
//        int startId = nodes.get(url).getId();
//        Set<Integer> existingTargets = eDao.findTargetIdsBySource(startId);
//
//        for(String link : links){
//            int endId = nodes.get(link).getId();
//            if (!existingTargets.contains(endId)){
//                idsToStore.add(endId);
//            }
//        }
//        eDao.storeTargetIdsWithSource(startId, idsToStore);
    }
}
