/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.enums;

/**
 *
 * @author CarlosDaniel
 */
public enum PaymentType {

    TRANSFER_WITH_SPECIFIC_BANK, //ALL
    TRANSFER_NATIONAL_BANK, //ALL
    CASH_DEPOSIT,
    WIRE_TRANSFER,
    TRANSFER_INTERNATIONAL_BANK,
    PERSONAL_CHECK_DEPOSIT, 
    CASHIER_CHECK_DEPOSIT, 
    MONEY_ORDER,
    CREDIT_CARD,
    TRANSFER_TO_CRYPTO_WALLET,
    ACH, //USD
    ACH_EXPRESS, //USD
    ZELLE, //USD EUR
    PAYPAL, //USD EUR
    MAIN,
    RETAIL,
    ACH_THIRD_ACCOUNT, //USD
    ACH_THIRD_ACCOUNT_EXPRESS, //USD
    GENERIC_THIRD_ACCOUNT, //ALL
    YAPE, //PEN
    MOBILE_PAYMENT, //VES
    NEQUI, //COP
    VENMO, //USD
    WISE, //USD EUR
    MONEYBEAN, //EUR USD
    CASHAPP, //USD
    SUPERGIROS, //COP
    EFECTY, //COP
    ETHEREUM, //ETH
    TRON, //USDT
    BITCOIN //BTC
    
}
