/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserPostMessageNew;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class SendTestMCMessagesMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UsersFolderLocator.getMCMessagesFolder("584245522788", "584245105809");
        int i = 0;
        while (true) {
            i++;
            if(i >= 100){
                break;
            }
            String response1 = new MCUserPostMessageNew("584245522788", "584245105809", "REQUEST frekhuhevvhibribrbvvhbdfvdhfvblsjdfbvjhbdfvbsfvsdbfvjlsdbfvjsbfhvsbvjhbvjhebjrvbjhfbejlhbvelrhvbevjhblrblebvljdfhbvdv " + i, null, null, null, new Date().getTime(), null).getResponse();
            System.out.println("message1 " + i + " responde " + response1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendTestMCMessagesMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            String response2 = new MCUserPostMessageNew("584245105809", "584245522788", "RESPONSE dnfkalnsdfknas'dklfn'asldkfn'asdf'laksdf'alksdfjlasdfkadfjlasjdfasdkfhaksdjfhkajhdf " + i, null, null, null, new Date().getTime(), null).getResponse();
            System.out.println("message2 " + i + " responde " + response2);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendTestMCMessagesMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
