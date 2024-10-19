/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class MoneyclickFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Moneyclick"));
    }

    public static File getAlertsFile() {
        return new File(getFolder(), "alerts.json");
    }

    public static File getAlertsWithLanguageFile() {
        return new File(getFolder(), "alertsWithLanguage.json");
    }
    
    public static File getAlertsWithLanguageAndOperationFile() {
        return new File(getFolder(), "alertsWithLanguageAndOperation.json");
    }

    public static File getConfigFile() {
        return new File(getFolder(), "config.json");
    }

    public static File getBalanceFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Balance"));
    }

    public static File getBalanceFolder(String operatorName) {
        return FileUtil.createFolderIfNoExist(new File(getBalanceFolder(), operatorName));
    }

    public static File getDevicesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Devices"));
    }

    public static File getRetailsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Retails"));
    }

    public static File getOperationsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Operations"));
    }
    
    public static File getCryptoBuysFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "CryptoBuys"));
    }
    
    public static File getCryptoBuysOldFolder() {
        return FileUtil.createFolderIfNoExist(new File(getCryptoBuysFolder(), "Old"));
    }

    public static File getClientsBalanceFile() {
        return new File(getFolder(), "clientsBalance.json");
    }

    public static File getOperationBalanceFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "OperationBalance"));
    }

    public static File getOperationBalanceFolder(String currency) {
        return FileUtil.createFolderIfNoExist(new File(getOperationBalanceFolder(), currency));
    }

    public static File getOperationsIndexesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getOperationsFolder(), "Indexes"));
    }

    public static File getOperationsIndexFolder(String index) {
        return FileUtil.createFolderIfNoExist(new File(getOperationsIndexesFolder(), index));
    }

    public static File getRetailFolder(String retailId) {
        return new File(getRetailsFolder(), retailId);
    }

    public static File getRetailEscrowLimitsFile() {
        return new File(getRetailsFolder(), "escrowLimits.json");
    }

    public static File getRetailChargesFolder(String retailId) {
        return new File(getRetailFolder(retailId), "Charges");
    }

    public static File getRetailAttachmentsFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailFolder(retailId), "Attachments"));
    }

    public static File getRetailQRCodesFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailFolder(retailId), "QRCodes"));
    }

    public static File getRetailQRCodeFile(String retailId) {
        return new File(getRetailFolder(retailId), "qrCode.png");
    }

    public static File getRetailAttachmentFile(String retailId, String fileName) {
        return new File(getRetailAttachmentsFolder(retailId), fileName);
    }

    public static File getRetailConfigFile(String retailId) {
        return new File(getRetailFolder(retailId), "config.json");
    }

    public static File getRetailBalanceFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailFolder(retailId), "Balance"));
    }

    public static File getRetailBalanceCashFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailBalanceFolder(retailId), "Cash"));
    }

    public static File getRetailBalanceNoCashFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailBalanceFolder(retailId), "NoCash"));
    }

    public static File getRetailEscrowBalanceFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailFolder(retailId), "EscrowBalance"));
    }

    public static File getRetailEscrowBalanceFromToUserFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getRetailEscrowBalanceFolder(retailId), "FromToUser"));
    }

    public static File getFastChangesCountFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "FastChangesCount"));
    }

    public static File getFastChangesCountFile(String userName) {
        return new File(getFastChangesCountFolder(), userName + ".file");
    }
    
    public static File getCryptoBuysSellsCountFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "CryptoBuysSellsCount"));
    }

    public static File getCryptoBuysSellsCountFile(String userName) {
        return new File(getCryptoBuysSellsCountFolder(), userName + ".file");
    }

    public static File getMessageOffersFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "MessageOffers"));
    }
    
    public static File getMessageOffersUserNameFile(String userName) {
        return new File(getMessageOffersFolder(), userName + ".json");
    }
    
    public static File getPairsFile() {
        return new File(getFolder(), "pairs.json");
    }

    public static File getMessageOfferPairFolder(String pair) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            boolean pairExist = false;
            Iterator<String> pairsIterator = mapper.readTree(getPairsFile()).fieldNames();
            while (pairsIterator.hasNext()) {
                String pairsIt = pairsIterator.next();
                if (pairsIt.equals(pair)) {
                    pairExist = true;
                    break;
                }
            }
            if (pairExist) {
                return FileUtil.createFolderIfNoExist(new File(getMessageOffersFolder(), pair));
            }
        } catch (IOException ex) {
            Logger.getLogger(MoneyclickFolderLocator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static File getMessageOfferPairTypeFolder(String pair, String type) {
        return FileUtil.createFolderIfNoExist(new File(getMessageOfferPairFolder(pair), type));
    }
    
    public static File getMessageOfferPairTypeOldFolder(String pair, String type) {
        return FileUtil.createFolderIfNoExist(new File(getMessageOfferPairTypeFolder(pair, type), "Old"));
    }

    public static File getMessageOfferPairTypeIdFile(String pair, String type, String id) {
        return new File(getMessageOfferPairTypeFolder(pair, type), id + ".json");
    }
    
    public static File getMessageOfferPairTypeIdOldFile(String pair, String type, String id) {
        return new File(getMessageOfferPairTypeOldFolder(pair, type), id + ".json");
    }
    
    public static File getBotsFile() {
        return new File(getFolder(), "bots.json");
    }
    
    public static File getBotsActivityFile() {
        return new File(getFolder(), "botsActivity.json");
    }
    
    public static File getSpecialBalanceMovementsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "SpecialBalanceMovements"));
    }
    
    public static File getSpecialBalanceMovementsFile(String id) {
        return new File(getSpecialBalanceMovementsFolder(), id + ".json");
    }
    
    public static File getEmptyUserFile() {
        return new File(getFolder(), "emptyUser.png");
    }
        
}
