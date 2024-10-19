/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MasterAccountEditAutomaticRulesRequest implements Serializable, Cloneable {

    private AutomaticRule[] automaticRules;

    public MasterAccountEditAutomaticRulesRequest() {
    }

    public AutomaticRule[] getAutomaticRules() {
        return automaticRules;
    }

    public void setAutomaticRules(AutomaticRule[] automaticRules) {
        this.automaticRules = automaticRules;
    }

    public static class AutomaticRule {

        private String baseAccount;
        private boolean active;
        private int executionPeriodInHours;
        private BalanceOperationType balanceOperationType;
        private TargetAccountOrClientModelNameAndPercent[] TargetAccountsOrClientModelNameAndPercents;

        public AutomaticRule() {
        }

        public String getBaseAccount() {
            return baseAccount;
        }

        public void setBaseAccount(String baseAccount) {
            this.baseAccount = baseAccount;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public int getExecutionPeriodInHours() {
            return executionPeriodInHours;
        }

        public void setExecutionPeriodInHours(int executionPeriodInHours) {
            this.executionPeriodInHours = executionPeriodInHours;
        }

        public BalanceOperationType getBalanceOperationType() {
            return balanceOperationType;
        }

        public void setBalanceOperationType(BalanceOperationType balanceOperationType) {
            this.balanceOperationType = balanceOperationType;
        }

        public TargetAccountOrClientModelNameAndPercent[] getTargetAccountsOrClientModelNameAndPercents() {
            return TargetAccountsOrClientModelNameAndPercents;
        }

        public void setTargetAccountsOrClientModelNameAndPercents(TargetAccountOrClientModelNameAndPercent[] TargetAccountsOrClientModelNameAndPercents) {
            this.TargetAccountsOrClientModelNameAndPercents = TargetAccountsOrClientModelNameAndPercents;
        }

    }

    public static class TargetAccountOrClientModelNameAndPercent {

        private String name;
        private double percent;

        public TargetAccountOrClientModelNameAndPercent() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPercent() {
            return percent;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

    }

    public ArrayNode toArrayNode() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (AutomaticRule automaticRule : this.automaticRules) {
            ObjectNode automaticRuleJson = mapper.createObjectNode();
            automaticRuleJson.put("baseAccount", automaticRule.getBaseAccount());
            automaticRuleJson.put("active", automaticRule.isActive());
            automaticRuleJson.put("balanceOperationType", automaticRule.getBalanceOperationType().name());
            automaticRuleJson.put("executionPeriodInHours", automaticRule.getExecutionPeriodInHours());
            automaticRuleJson.putArray("targetAccountsOrClientModelNameAndPercents");
            for (TargetAccountOrClientModelNameAndPercent targetAccountOrClientModelNameAndPercent : automaticRule.getTargetAccountsOrClientModelNameAndPercents()) {
                ObjectNode targetAccountOrClientModelNameAndPercentJson = mapper.createObjectNode();
                targetAccountOrClientModelNameAndPercentJson.put("name", targetAccountOrClientModelNameAndPercent.getName());
                targetAccountOrClientModelNameAndPercentJson.put("percent", targetAccountOrClientModelNameAndPercent.getPercent());
                ((ArrayNode) automaticRuleJson.get("targetAccountsOrClientModelNameAndPercents")).add(targetAccountOrClientModelNameAndPercentJson);
            }
            arrayNode.add(automaticRuleJson);
        }
        return arrayNode;
    }

}
