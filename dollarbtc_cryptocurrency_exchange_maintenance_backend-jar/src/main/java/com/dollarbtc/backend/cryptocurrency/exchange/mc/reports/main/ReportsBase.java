/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.reports.main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mcarlos
 */
public abstract class ReportsBase {
    
    protected static final Set<String> BLACK_LISTED_USERS = new HashSet<>(
            Arrays.asList(
                    "584245522788", 
                    "12019896074", 
                    "sinep77@gmail.com",
                    "15512214091"
            )
    );
    
}
