/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

/**
 *
 * @author CarlosDaniel
 */
public class HMACAlgorithmFactory {

    /**
     * Creates an HMACAlgorithm that uses the given algorithm.
     * 
     * @param algorithmName The name of the algorithm (only SHA1, SHA256, SHA384 and SHA512 supported currently
     * @return The HMACAlgorithm
     */
    public HMACAlgorithm createAlgorithm(String algorithmName) {
        if ( algorithmName.startsWith("SHA") ) {
            return new SHAHMACAlgorithm(Integer.parseInt(algorithmName.substring("SHA".length())));
        } 
        throw new IllegalArgumentException("Algorithm "+algorithmName+" not supported");
    }
    
}