package org.sainnr.wgc.hypertext.data;

import java.util.List;
import java.util.Set;

/**
 * Created by Vladimir on 17.05.2015.
 */
public class HypertextStructure {

    List<String> urlIndex;
    List<String> filesIndex;
    List<String> brokenUrls;
    Set<HyperPage> pages;

    public List<String> getUrlIndex() {
        return urlIndex;
    }

    public void setUrlIndex(List<String> urlIndex) {
        this.urlIndex = urlIndex;
    }

    public Set<HyperPage> getPages() {
        return pages;
    }

    public void setPages(Set<HyperPage> pages) {
        this.pages = pages;
    }

    public List<String> getFilesIndex() {
        return filesIndex;
    }

    public void setFilesIndex(List<String> filesIndex) {
        this.filesIndex = filesIndex;
    }

    public List<String> getBrokenUrls() {
        return brokenUrls;
    }

    public void setBrokenUrls(List<String> brokenUrls) {
        this.brokenUrls = brokenUrls;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HypertextStructure{\n\t");
        for (HyperPage page : pages){
            sb.append(page).append("\n\t");
        }
        sb.append("}");
        return sb.toString();
    }
}
