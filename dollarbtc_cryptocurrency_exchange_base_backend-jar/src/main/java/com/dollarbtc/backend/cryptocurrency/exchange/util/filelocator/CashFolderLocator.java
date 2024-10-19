/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author carlos molina
 */
public class CashFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Cash"));
    }

    public static File getConfigFile() {
        return new File(getFolder(), "config.json");
    }

    public static File getPlacesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Places"));
    }

    public static File getDevicesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Devices"));
    }

    public static File getPlaceFolder(String placeId) {
        return new File(getPlacesFolder(), placeId);
    }

    public static File getPlacesEscrowLimitsFile() {
        return new File(getPlacesFolder(), "escrowLimits.json");
    }

    public static File getPlaceChargesFolder(String placeId) {
        return new File(getPlaceFolder(placeId), "Charges");
    }

    public static File getPlaceAttachmentsFolder(String placeId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceFolder(placeId), "Attachments"));
    }

    public static File getPlaceQRCodesFolder(String placeId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceFolder(placeId), "QRCodes"));
    }

    public static File getPlaceQRCodeFile(String placeId) {
        return new File(getPlaceFolder(placeId), "qrCode.png");
    }

    public static File getPlaceAttachmentFile(String placeId, String fileName) {
        return new File(getPlaceAttachmentsFolder(placeId), fileName);
    }

    public static File getPlaceConfigFile(String placeId) {
        return new File(getPlaceFolder(placeId), "config.json");
    }

    public static File getPlaceBalanceFolder(String placeId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceFolder(placeId), "Balance"));
    }

    public static File getPlaceBalanceCashFolder(String placeId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceBalanceFolder(placeId), "Cash"));
    }

    public static File getPlaceBalanceNoCashFolder(String placeId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceBalanceFolder(placeId), "NoCash"));
    }

    public static File getPlaceEscrowBalanceFolder(String placeId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceFolder(placeId), "EscrowBalance"));
    }

    public static File getPlaceEscrowBalanceFromToUserFolder(String retailId) {
        return FileUtil.createFolderIfNoExist(new File(getPlaceEscrowBalanceFolder(retailId), "FromToUser"));
    }

    public static File getOperationsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Operations"));
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

}
