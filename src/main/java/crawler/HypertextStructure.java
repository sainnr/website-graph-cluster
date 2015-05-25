package crawler;

import java.util.List;
import java.util.Set;

/**
 * Created by Vladimir on 17.05.2015.
 */
public class HypertextStructure {

    List<String> urlIndex;
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
}
