package org.sainnr.wgc.statistics.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir on 13.06.2015.
 */
public class GaVisitedPagesStructure {

    List<String> urlIndex;
    Map<String, Set<GaVisitedPageEntry>> pages;

    public List<String> getUrlIndex() {
        return urlIndex;
    }

    public void setUrlIndex(List<String> urlIndex) {
        this.urlIndex = urlIndex;
    }

    public Map<String, Set<GaVisitedPageEntry>> getPages() {
        return pages;
    }

    public void setPages(Map<String, Set<GaVisitedPageEntry>> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GaVisitedPagesStructure{\n");
        for (String url : urlIndex) {
            sb.append("\t").append(urlIndex.indexOf(url)).append(": '").append(url).append("'\n");
            for (GaVisitedPageEntry page : pages.get(url)) {
                sb.append("\t\t").append(page).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();

    }
}
