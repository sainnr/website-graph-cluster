package org.sainnr.wgc.crawler;

import java.util.Set;

/**
 * Created by Vladimir on 17.05.2015.
 */
public class HyperPage {

    int id;
    String url;
    String title;
    String content;
    Set<String> outcomingUrl;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getOutcomingUrl() {
        return outcomingUrl;
    }

    public void setOutcomingUrl(Set<String> outcomingUrl) {
        this.outcomingUrl = outcomingUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HyperPage hyperPage = (HyperPage) o;

        return url.equals(hyperPage.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
