/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 * @param <T>
 */
public abstract class AbstractFirebaseOperation<T> extends AbstractOperation<T> {

    private final boolean kaikai;
    
    public AbstractFirebaseOperation(Class<T> responseClass, boolean kaikai) {
        super(responseClass);
        this.kaikai = kaikai;
        init();
    }

    private void init() {
        try {
            File firebaseFile = NotificationsFolderLocator.getFirebaseFile();
            if(kaikai){
                firebaseFile = NotificationsFolderLocator.getKaikaiFirebaseFile();
            }
            Logger.getLogger(AbstractFirebaseOperation.class.getName()).log(Level.INFO, firebaseFile.getAbsolutePath());
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(new FileInputStream(firebaseFile)))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException ex) {
            Logger.getLogger(AbstractFirebaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
