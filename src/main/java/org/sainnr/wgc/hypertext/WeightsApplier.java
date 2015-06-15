package org.sainnr.wgc.hypertext;

import org.sainnr.wgc.hypertext.data.HyperPage;
import org.sainnr.wgc.hypertext.data.HypertextStructure;
import org.sainnr.wgc.statistics.data.GaVisitedPageEntry;
import org.sainnr.wgc.statistics.data.GaVisitedPagesStructure;

import java.util.Set;

/**
 * Created by Vladimir on 13.06.2015.
 */
public class WeightsApplier {

    HypertextStructure htStructure;
    String domain;

    public WeightsApplier(HypertextStructure htStructure, String domain) {
        this.htStructure = htStructure;
        this.domain = domain;
    }

    public HypertextStructure applyWeights(GaVisitedPagesStructure gaStructure){

        for (HyperPage page : htStructure.getPages()){
            String url = page.getUrl().toLowerCase();
//            System.out.println(url);
            Set<GaVisitedPageEntry> pageEntrySet1;
            Set<GaVisitedPageEntry> pageEntrySet2;
            if (url.endsWith(".html")) {
                pageEntrySet1 = gaStructure.getPages().get(url);
                pageEntrySet2 = gaStructure.getPages().get(url.substring(0, url.length() - 6));
            } else {
                pageEntrySet1 = gaStructure.getPages().get(url);
                pageEntrySet2 = gaStructure.getPages().get(url + ".html");
            }
//            System.out.println(pageEntrySet1);
//            System.out.println(pageEntrySet2);
//            System.out.println("____________________________________________________");
            for (String urlTo : page.getOutcomingUrl()){
                double weight = 0;
//                System.out.println(urlTo);
                if (pageEntrySet1 != null) {
                    for (GaVisitedPageEntry entry : pageEntrySet1) {
                        if (compare(entry.getNextUrl(), urlTo.toLowerCase())) {
                            weight = entry.getNumOfVisits();
                            break;
                        }
                    }
                }
                if (pageEntrySet2 != null) {
                    for (GaVisitedPageEntry entry : pageEntrySet2) {
                        if (compare(entry.getNextUrl(), urlTo.toLowerCase())) {
                            weight += entry.getNumOfVisits();
                            break;
                        }
                    }
                }
                if (weight == 0){
                    weight = 1.0;
                }
                page.setWeight(urlTo, weight);
            }
        }

        return htStructure;
    }

    private boolean compare(String url1, String url2){
        return (url1 != null && url2 != null &&
                (url1.equals(url2) || (url1 + ".html").equals(url2) || (url2 + ".html").equals(url1))
        );
    }
}
