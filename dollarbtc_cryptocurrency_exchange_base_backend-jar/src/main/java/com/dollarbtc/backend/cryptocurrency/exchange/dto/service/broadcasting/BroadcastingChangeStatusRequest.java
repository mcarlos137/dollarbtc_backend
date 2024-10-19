/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting;

/**
 *
 * @author CarlosDaniel
 */
public class BroadcastingChangeStatusRequest {
        
    private String broadcastingId, episoseTrailerId, status;

    public String getBroadcastingId() {
        return broadcastingId;
    }

    public void setBroadcastingId(String broadcastingId) {
        this.broadcastingId = broadcastingId;
    }

    public String getEpisoseTrailerId() {
        return episoseTrailerId;
    }

    public void setEpisoseTrailerId(String episoseTrailerId) {
        this.episoseTrailerId = episoseTrailerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
                                   
}
