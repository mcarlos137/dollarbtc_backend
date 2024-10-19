/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.digitalbusiness;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.DecimalFormat;
import java.util.Random;

/**
 *
 * @author carlosmolina
 */
public class DigitalBusinessGetMoneyCallsDetails extends AbstractOperation<ArrayNode> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private final String userName;

    public DigitalBusinessGetMoneyCallsDetails(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        ArrayNode overview = mapper.createArrayNode();
        /*//MONEY CALL
        ObjectNode moneyCallFinancial = mapper.createObjectNode();
        moneyCallFinancial.put("type", "MONEY_CALL");
        moneyCallFinancial.putArray("values");
        int i = 6;
        while (i > 0) {
            ObjectNode value = mapper.createObjectNode();
            value.put("timestamp", DateUtil.getDateDaysAfter(DateUtil.getDateMonthsBefore(DateUtil.getMonthStartDate(null), i), 1));
            value.put("earnings", Double.parseDouble(DECIMAL_FORMAT.format(new Random().nextDouble() * 100)));
            value.put("spends", Double.parseDouble(DECIMAL_FORMAT.format(new Random().nextDouble() * 100)));
            ((ArrayNode) moneyCallFinancial.get("values")).add(value);
            i--;
        }
        overview.add(moneyCallFinancial);
        //SUBSCRIPTION
        ObjectNode subscriptionFinancial = mapper.createObjectNode();
        subscriptionFinancial.put("type", "SUBSCRIPTION");
        subscriptionFinancial.putArray("values");
        i = 6;
        while (i > 0) {
            ObjectNode value = mapper.createObjectNode();
            value.put("timestamp", DateUtil.getDateDaysAfter(DateUtil.getDateMonthsBefore(DateUtil.getMonthStartDate(null), i), 1));
            value.put("earnings", Double.parseDouble(DECIMAL_FORMAT.format(new Random().nextDouble() * 100)));
            value.put("spends", Double.parseDouble(DECIMAL_FORMAT.format(new Random().nextDouble() * 100)));
            ((ArrayNode) subscriptionFinancial.get("values")).add(value);
            i--;
        }
        overview.add(subscriptionFinancial);
        //SUBSCRIPTION
        ObjectNode donationFinancial = mapper.createObjectNode();
        donationFinancial.put("type", "DONATION");
        donationFinancial.putArray("values");
        i = 6;
        while (i > 0) {
            ObjectNode value = mapper.createObjectNode();
            value.put("timestamp", DateUtil.getDateDaysAfter(DateUtil.getDateMonthsBefore(DateUtil.getMonthStartDate(null), i), 1));
            value.put("earnings", Double.parseDouble(DECIMAL_FORMAT.format(new Random().nextDouble() * 100)));
            value.put("spends", Double.parseDouble(DECIMAL_FORMAT.format(new Random().nextDouble() * 100)));
            ((ArrayNode) donationFinancial.get("values")).add(value);
            i--;
        }
        overview.add(donationFinancial);*/
        super.response = overview;
    }

}
