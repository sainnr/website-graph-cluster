package org.sainnr.wgc.statistics.gaapi;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Created by Vladimir on 12.06.2015.
 */
public class AnalyticsService {

    private static final Log log = LogFactory.getLog(AnalyticsService.class);
    Analytics analytics;

    public AnalyticsService(Analytics analytics) {
        this.analytics = analytics;
    }

    /**
     * Obtains first profileId by provided Analytics instance
     * @return first profile ID
     * @throws java.io.IOException
     */
    public String getProfileId(String accountName) throws IOException {
        String profileId = null;

        // Query accounts collection.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            log.error("No accounts found");
        } else {
            String accountId;
            if (accountName == null || accountName.equals("")){
                accountId = accounts.getItems().get(0).getId();
            } else {
                accountId = null;
                for(Account acc : accounts.getItems()){
                    if (acc.getName().equals(accountName)){
                        accountId = acc.getId();
                        break;
                    }
                }
            }

            // Query webproperties collection.
            Webproperties webproperties =
                    analytics.management().webproperties().list(accountId).execute();

            if (webproperties.getItems().isEmpty()) {
                log.error("No Webproperties found");
            } else {
                String firstWebpropertyId = webproperties.getItems().get(0).getId();

                // Query profiles collection.
                Profiles profiles =
                        analytics.management().profiles().list(accountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    log.error("No profiles found");
                } else {
                    profileId = profiles.getItems().get(0).getId();
                }
            }
        }
        return profileId;
    }

}
