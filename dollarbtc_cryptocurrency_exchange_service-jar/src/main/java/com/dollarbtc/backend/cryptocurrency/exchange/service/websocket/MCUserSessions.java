/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author carlosmolina
 */
public class MCUserSessions {

    // static variable single_instance of type Singleton
    private static MCUserSessions single_instance = null;

    // variable of type String
    public Map<String, Session> data;

    // private constructor restricted to this class itself
    private MCUserSessions() {
        data = new HashMap<>();
    }

    // static method to create instance of Singleton class
    public static MCUserSessions getInstance() {
        if (single_instance == null) {
            single_instance = new MCUserSessions();
        }

        return single_instance;
    }

}
