
package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class AccountBaseInterval {

    private final String startTimestamp, endTimestamp, intervalAlgorithmName;
    private final List<AccountBase> accounts;

    public AccountBaseInterval(
            String startTimestamp,
            String endTimestamp,
            String intervalAlgorithmName,
            List<AccountBase> accounts
    ) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.intervalAlgorithmName = intervalAlgorithmName;
        this.accounts = accounts;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public String getIntervalAlgorithmName() {
        return intervalAlgorithmName;
    }

    public List<AccountBase> getAccounts() {
        return accounts;
    }

    @Override
    public String toString() {
        return "AccountBaseInterval{" + 
                "startTimestamp=" + startTimestamp + 
                ", endTimestamp=" + endTimestamp + 
                ", intervalAlgorithmName=" + intervalAlgorithmName + 
                ", accounts=" + accounts + 
                '}';
    }
           
}
