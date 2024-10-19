/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author carlosmolina
 */
public class UserGetSegurityQuestions extends AbstractOperation<JsonNode> {

    private final String userName;
    private int quantity;

    public UserGetSegurityQuestions(String userName, int quantity) {
        super(JsonNode.class);
        this.userName = userName;
        this.quantity = quantity;
    }

    @Override
    protected void execute() {
        ArrayNode securityQuestions = mapper.createArrayNode();
        List<String> securityQuestiosList = new ArrayList<>();
        JsonNode config = new UserGetConfig(userName, "").getResponse();
        Iterator<String> configFieldNamesIterator = config.fieldNames();
        while (configFieldNamesIterator.hasNext()) {
            String configFieldNamesIt = configFieldNamesIterator.next();
            if (configFieldNamesIt.contains("questionSecurity")) {
                securityQuestiosList.add(config.get(configFieldNamesIt).textValue());
            }
        }
        if (securityQuestiosList.size() < quantity) {
            quantity = securityQuestiosList.size();
        }
        int i = 0;
        Random random = new Random();
        while (i < quantity) {
            String randomElement = securityQuestiosList.get(random.nextInt(securityQuestiosList.size()));
            securityQuestions.add(randomElement);
            securityQuestiosList.remove(randomElement);
            i++;
        }
        super.response = securityQuestions;
    }

}
