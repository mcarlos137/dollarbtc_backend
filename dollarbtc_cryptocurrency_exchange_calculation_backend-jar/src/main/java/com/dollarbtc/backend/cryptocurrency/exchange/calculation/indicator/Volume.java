package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.math.BigDecimal;
import org.apache.commons.lang3.Validate;

/**
 * Volume indicator.
 */
public class Volume implements Indicator<BigDecimal> {

    private final Period period;

    /**
     * @param period the period for which we want the trade volume
     */
    public Volume(Period period) {
        Validate.notNull(period, "Period can't be null");
        this.period = period;
    }
    
    /**
     * @return the trade volume (always positive) during the period
     */
    @Override
    public BigDecimal execute() {
        BigDecimal volume = BigDecimal.ZERO;
        for (Trade trade : period.getTrades()) {
            volume = volume.add(trade.getTradableAmount().multiply(trade.getPrice()));
        }
        return volume;
    }
    
}