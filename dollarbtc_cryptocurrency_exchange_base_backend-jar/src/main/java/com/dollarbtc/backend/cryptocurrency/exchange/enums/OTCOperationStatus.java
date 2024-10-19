/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.enums;

/**
 *
 * @author CarlosDaniel
 */
public enum OTCOperationStatus {
    
    WAITING_TO_START_OPERATION,
    WAITING_FOR_PAYMENT, 
    PAY_VERIFICATION, 
    CANCELED, 
    SUCCESS,
    CLAIM,
    IN_BATCH_PROCESS;
    
}
