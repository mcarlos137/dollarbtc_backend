/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCheckSecurityQuestionsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class UserCheckSecurityQuestions extends AbstractOperation<String> {

    private final UserCheckSecurityQuestionsRequest userCheckSecurityQuestionsRequest;

    public UserCheckSecurityQuestions(UserCheckSecurityQuestionsRequest userCheckSecurityQuestionsRequest) {
        super(String.class);
        this.userCheckSecurityQuestionsRequest = userCheckSecurityQuestionsRequest;
    }

    @Override
    protected void execute() {
        JsonNode config = new UserGetConfig(userCheckSecurityQuestionsRequest.getUserName(), "").getResponse();
        Iterator<String> configFieldNamesIterator = config.fieldNames();
        int correctAnswers = 0;
        while (configFieldNamesIterator.hasNext()) {
            String configFieldNamesIt = configFieldNamesIterator.next();
            if (!configFieldNamesIt.contains("questionSecurity")) {
                continue;
            }
            String position = configFieldNamesIt.replace("questionSecurity", "");
            String question = config.get("questionSecurity" + position).textValue();
            String answer = config.get("answerSecurity" + position).textValue().toLowerCase();
            if (userCheckSecurityQuestionsRequest.getSecurityQuestionsAndAnswers().has(question)) {
                if (userCheckSecurityQuestionsRequest.getSecurityQuestionsAndAnswers().get(question).textValue().toLowerCase().equals(answer)) {
                    correctAnswers++;
                }
            }
        }
        if (correctAnswers == userCheckSecurityQuestionsRequest.getSecurityQuestionsAndAnswers().size()) {
            super.response = "OK";
            return;
        }
        super.response = "FAIL";
    }

}
