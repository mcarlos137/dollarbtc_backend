/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountNewGetOTCMasterAccountNames extends AbstractOperation<ArrayNode> {

    private final String userName;

    public MasterAccountNewGetOTCMasterAccountNames(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        ArrayNode otcMasterAccountNames = mapper.createArrayNode();
        Set<String> otcMsterAccountNamesAlreadyAdded = new HashSet<>();
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            if (!OPERATOR_NAME.equals("MAIN") && !OPERATOR_NAME.equals(operator)) {
                continue;
            }
            userCurrencies.stream().map((userCurrency) -> new MasterAccountNewGetOTCMasterAccountName(operator, userCurrency).getResponse()).filter((otcMasterAccountName) -> !(otcMsterAccountNamesAlreadyAdded.contains(otcMasterAccountName.get("name").textValue())) && !otcMasterAccountName.get("name").textValue().equals("")).map((otcMasterAccountName) -> {
                if (otcMasterAccountName.get("name").textValue().equals("")) {

                }
                otcMsterAccountNamesAlreadyAdded.add(otcMasterAccountName.get("name").textValue());
                return otcMasterAccountName;
            }).forEach((otcMasterAccountName) -> {
                otcMasterAccountNames.add(otcMasterAccountName);
            });
        }
        super.response = otcMasterAccountNames;
    }

}
