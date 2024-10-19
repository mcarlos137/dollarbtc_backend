/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.reports.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mcarlos
 */
public class ReportsMain extends ReportsListResult {

    private static final ObjectNode CONDITIONS = new ObjectMapper().valueToTree(
            new HashMap<String, Object>() {
        {
            put("ACTIVE", true);
            put("VERIFICATED", true);
            //put("EMAIL", "ANY_VALUE");
            //put("USD_ESTIMATED_BALANCE", ">=200");
            //put("FLAG", "BLUE");
            //put("BALANCE_MOVEMENTS_CRYPTO_DEPOSITS_QUANTITY", ">=1"); //CRYPTO_DEPOSITS_QUANTITY FIAT_DEPOSITS_QUANTITY
            put("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY", ">=1");
            //put("BALANCE_MOVEMENTS_CRYPTO_DEPOSITS_AMOUNT", ">=300_USD"); //CRYPTO_DEPOSITS_AMOUNT FIAT_DEPOSITS_AMOUNT
            put("BALANCE_MOVEMENTS_STATUS", "OK");
            //put("key2", "value2");
        }
    });

    private static final Set<String> COLLECT_INFO = new HashSet<>(
            Arrays.asList(
                    "email",
                    "firstName",
                    "lastName",
                    "balanceMovementsCryptoDepositsQuantity",
                    "balanceMovementsFiatDepositsQuantity"
                    //"usdEstimatedBalance"
                    //btcEstimatedBalance
            //"balanceMovementsFiatDepositsQuantity"
            //"12019896074", 
            //"sinep77@gmail.com",
            //"15512214091"
            )
    );

    //UsersWithNoVerificationsButWithBalanceMain() {
        //super(CONDITIONS, COLLECT_INFO, 100000);
    //}
    
    ReportsMain(JsonNode conditions, HashSet<String> collectInfo, Double btcusdPrice) {
        super(conditions, collectInfo, 16900, 100000);
    }

    /**
     * Find users with no verifications but with balance
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        args = new String[]{"2"};
        ObjectMapper mapper = new ObjectMapper();
        JsonNode report = mapper.readTree(new File(new File(ExchangeUtil.OPERATOR_PATH, "reports"), args[0] + ".json"));
        JsonNode conditions = report.get("conditions");
        HashSet<String> collectInfo = new ObjectMapper().convertValue(report.get("collectInfo"), HashSet.class);
        Double btcusdPrice = report.get("btcusdPrice").doubleValue();
        List<List<Object>> result = new ReportsMain(conditions, collectInfo, btcusdPrice).getResult();
        System.out.println("result " + result.size() + " " + result);
    }

}
