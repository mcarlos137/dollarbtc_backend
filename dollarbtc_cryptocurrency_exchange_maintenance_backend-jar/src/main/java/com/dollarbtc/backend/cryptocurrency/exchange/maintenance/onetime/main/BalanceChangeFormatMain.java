/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

/**
 *
 * @author CarlosDaniel
 */
public class BalanceChangeFormatMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        ObjectMapper mapper = new ObjectMapper();
//        File modelBalanceFolder = new File(new File(ROOT_PATH, "Models"), "Balance");
//        for (File modelBalanceFile : modelBalanceFolder.listFiles()) {
//            if (!modelBalanceFile.isFile()) {
//                continue;
//            }
//            JsonNode modelBalance = null;
//            try {
//                modelBalance = mapper.readTree(modelBalanceFile);
//            } catch (IOException ex) {
//                Logger.getLogger(BalanceChangeFormatMain.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            JsonNode newModelBalance = mapper.createObjectNode();
//            ArrayNode availableAmountsArrayNode = mapper.createArrayNode();
//            ObjectNode availableUsdAmountNode = mapper.createObjectNode();
//            availableUsdAmountNode.put("amount", modelBalance.get("availableUsdAmount").doubleValue());
//            availableUsdAmountNode.put("currency", "USD");
//            availableAmountsArrayNode.add(availableUsdAmountNode);
//            ObjectNode availableBtcAmountNode = mapper.createObjectNode();
//            availableBtcAmountNode.put("amount", modelBalance.get("availableBtcAmount").doubleValue());
//            availableBtcAmountNode.put("currency", "BTC");
//            availableAmountsArrayNode.add(availableBtcAmountNode);
//            ObjectNode availableEthAmountNode = mapper.createObjectNode();
//            availableEthAmountNode.put("amount", modelBalance.get("availableEthAmount").doubleValue());
//            availableEthAmountNode.put("currency", "ETH");
//            availableAmountsArrayNode.add(availableEthAmountNode);
//            ((ObjectNode) newModelBalance).putArray("availableAmounts").addAll(availableAmountsArrayNode);
//            ArrayNode reservedAmountsArrayNode = mapper.createArrayNode();
//            ObjectNode reservedUsdAmountNode = mapper.createObjectNode();
//            reservedUsdAmountNode.put("amount", modelBalance.get("reservedUsdAmount").doubleValue());
//            reservedUsdAmountNode.put("currency", "USD");
//            reservedAmountsArrayNode.add(reservedUsdAmountNode);
//            ObjectNode reservedBtcAmountNode = mapper.createObjectNode();
//            reservedBtcAmountNode.put("amount", modelBalance.get("reservedBtcAmount").doubleValue());
//            reservedBtcAmountNode.put("currency", "BTC");
//            reservedAmountsArrayNode.add(reservedBtcAmountNode);
//            ObjectNode reservedEthAmountNode = mapper.createObjectNode();
//            reservedEthAmountNode.put("amount", modelBalance.get("reservedEthAmount").doubleValue());
//            reservedEthAmountNode.put("currency", "ETH");
//            reservedAmountsArrayNode.add(reservedEthAmountNode);
//            ((ObjectNode) newModelBalance).putArray("reservedAmounts").addAll(reservedAmountsArrayNode);
//            FileUtil.editFile(newModelBalance, modelBalanceFile);
//        }
    }

}
