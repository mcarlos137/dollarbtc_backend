/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mail;

/**
 *
 * @author CarlosDaniel
 */
public class MailException extends Exception {
        
    public MailException(String message){
        super(message);
    }
    
    public MailException(String message, Throwable throwable){
        super(message,throwable);
    }
        
}
