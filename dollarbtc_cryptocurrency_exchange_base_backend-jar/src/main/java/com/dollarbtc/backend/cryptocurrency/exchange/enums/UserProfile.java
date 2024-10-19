/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.enums;

/**
 
 * @author CarlosDaniel
 */
public enum UserProfile {

    ADMIN("general administrator", UserType.ADMIN, UserEnvironment.NONE, UserOperationAccount.MULTIPLE),
    NORMAL("new portal base user", UserType.NORMAL, UserEnvironment.NONE, UserOperationAccount.SELF),
    BROKER("broker user", UserType.BROKER, UserEnvironment.NONE, UserOperationAccount.SELF),
    BANKER("moneyclick banker", UserType.BANKER, UserEnvironment.NONE, UserOperationAccount.SELF),
    PRO_TRADER_TESTER("user to test old portal functionalities", UserType.PRO_TRADER, UserEnvironment.SAMPLER, UserOperationAccount.SELF),
    PRO_TRADER_EMULATED("old portal base user without production mode", UserType.PRO_TRADER, UserEnvironment.SAMPLER_LIMITED, UserOperationAccount.SELF),
    PRO_TRADER("old portal user with production mode", UserType.PRO_TRADER, UserEnvironment.PRODUCTION, UserOperationAccount.SELF),
    PRO_TRADER_MASTER("old portal user with production mode andsend/receive money to master", UserType.PRO_TRADER, UserEnvironment.PRODUCTION, UserOperationAccount.MASTER);

    private final String description;
    private final UserType userType;
    private final UserEnvironment userEnvironment;
    private final UserOperationAccount userOperationAccount;

    private UserProfile(String description, UserType userType, UserEnvironment userEnvironment, UserOperationAccount userOperationAccount) {
        this.description = description;
        this.userType = userType;
        this.userEnvironment = userEnvironment;
        this.userOperationAccount = userOperationAccount;
    }

    public String getDescription() {
        return description;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserEnvironment getUserEnvironment() {
        return userEnvironment;
    }

    public UserOperationAccount getUserOperationAccount() {
        return userOperationAccount;
    }
    
    public boolean equals(UserType userType, UserEnvironment userEnvironment, UserOperationAccount userOperationAccount){
        if(!this.userType.equals(userType)){
            return false;
        }
        if(!this.userEnvironment.equals(userEnvironment)){
            return false;
        }
        return this.userOperationAccount.equals(userOperationAccount);
    }

}
