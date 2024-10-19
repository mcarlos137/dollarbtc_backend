package com.dollarbtc.backend.cryptocurrency.exchange.service.main;

import com.dollarbtc.backend.cryptocurrency.exchange.service.filter.CORSFilter;
import com.dollarbtc.backend.cryptocurrency.exchange.service.filter.SecurityFilter;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.AccountServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.AddressServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.AdminServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.LiveStreamingServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.AnalysisServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.AttachmentServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.BankerServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.BrokerServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.BuyBalanceServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.CashServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.ChargeServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.ChatServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.CryptoAPIsServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.DebitCardServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.DigitalBusinessServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.DonationServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.ForexServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.GiftCardServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.HmacServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.LocalBitcoinsServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MCRetailNewServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MCRetailServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MCUserNewServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MCUserServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.OTCServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MailServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MarketModulatorServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MasterAccountNewServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MasterAccountServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.WebsiteServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.ModelServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.OTCAdminServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.OrderServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.PaymentServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.ReviewServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.TestServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.TradeServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.UserServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MFAServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MoneyCallServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MoneyMarketServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.MoneyOrderServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.NotificationServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.OTCNewServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.BroadcastingServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.ShortsServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.SmsServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.SubscriptionServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.SubscriptionEventServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.TagServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.TransferToBankServiceREST;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.LiveStreamingChangeImageServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.LiveStreamingCreateServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.DebitCardRequestServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.MCRetailAddAttachmentServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.MCUserPostMessageNewServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.MCUserPostMessageServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.MoneyOrderCreateServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.OTCPostOperationMessageServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.BroadcastingAddEpisodeTrailerServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.BroadcastingChangeImageServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.BroadcastingCreateServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.ShortsCreateServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.UploadFileServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet.UserAddAttachmentServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.ChatServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.ExchangeServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.MCUserServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.MarketOperationServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.MoneyMarketServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.OTCAdminServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.OTCServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.OrderServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.PriceServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.servlet.UserServlet;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.ServerConnector;
import java.util.EnumSet;
import javax.servlet.MultipartConfigElement;
import org.eclipse.jetty.servlet.FilterHolder;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JettyServerMain {

    public static final String JERSEY_CLASSNAMES
            = AccountServiceREST.class.getCanonicalName()
            + "," + AttachmentServiceREST.class.getCanonicalName()
            + "," + OrderServiceREST.class.getCanonicalName()
            + "," + AdminServiceREST.class.getCanonicalName()
            + "," + BuyBalanceServiceREST.class.getCanonicalName()
            + "," + BrokerServiceREST.class.getCanonicalName()
            + "," + OTCServiceREST.class.getCanonicalName()
            + "," + OTCNewServiceREST.class.getCanonicalName()
            + "," + HmacServiceREST.class.getCanonicalName()
            + "," + OTCAdminServiceREST.class.getCanonicalName()
            + "," + UserServiceREST.class.getCanonicalName()
            + "," + MCRetailServiceREST.class.getCanonicalName()
            + "," + MCRetailNewServiceREST.class.getCanonicalName()
            + "," + MCUserServiceREST.class.getCanonicalName()
            + "," + MCUserNewServiceREST.class.getCanonicalName()
            + "," + ModelServiceREST.class.getCanonicalName()
            + "," + MasterAccountServiceREST.class.getCanonicalName()
            + "," + MasterAccountNewServiceREST.class.getCanonicalName()
            + "," + WebsiteServiceREST.class.getCanonicalName()
            + "," + MailServiceREST.class.getCanonicalName()
            + "," + MarketModulatorServiceREST.class.getCanonicalName()
            + "," + TestServiceREST.class.getCanonicalName()
            + "," + LocalBitcoinsServiceREST.class.getCanonicalName()
            + "," + ChargeServiceREST.class.getCanonicalName()
            + "," + ForexServiceREST.class.getCanonicalName()
            + "," + AnalysisServiceREST.class.getCanonicalName()
            + "," + ChatServiceREST.class.getCanonicalName()
            + "," + PaymentServiceREST.class.getCanonicalName()
            + "," + AddressServiceREST.class.getCanonicalName()
            + "," + ReviewServiceREST.class.getCanonicalName()
            + "," + MFAServiceREST.class.getCanonicalName()
            + "," + TransferToBankServiceREST.class.getCanonicalName()
            + "," + GiftCardServiceREST.class.getCanonicalName()
            + "," + BankerServiceREST.class.getCanonicalName()
            + "," + SmsServiceREST.class.getCanonicalName()
            + "," + DebitCardServiceREST.class.getCanonicalName()
            + "," + MoneyOrderServiceREST.class.getCanonicalName()
            + "," + CryptoAPIsServiceREST.class.getCanonicalName()
            + "," + NotificationServiceREST.class.getCanonicalName()
            + "," + MoneyCallServiceREST.class.getCanonicalName()
            + "," + MoneyMarketServiceREST.class.getCanonicalName()
            + "," + SubscriptionServiceREST.class.getCanonicalName()
            + "," + SubscriptionEventServiceREST.class.getCanonicalName()
            + "," + BroadcastingServiceREST.class.getCanonicalName()
            + "," + CashServiceREST.class.getCanonicalName()
            + "," + DigitalBusinessServiceREST.class.getCanonicalName()
            + "," + ShortsServiceREST.class.getCanonicalName()
            + "," + DonationServiceREST.class.getCanonicalName()
            + "," + LiveStreamingServiceREST.class.getCanonicalName()
            + "," + TagServiceREST.class.getCanonicalName()
            + "," + TradeServiceREST.class.getCanonicalName();

    public static final String ATTACHMENT_BASE_PATH = "/attachment";
    public static final String USER_BASE_PATH = "/user";
    public static final String MODEL_BASE_PATH = "/model";
    public static final String OTC_BASE_PATH = "/otc";
    public static final String OTC_ADMIN_BASE_PATH = "/otcAdmin";
    public static final String OTC_NEW_BASE_PATH = "/otcNew";
    public static final String MASTER_ACCOUNT_BASE_PATH = "/masterAccount";
    public static final String MASTER_ACCOUNT_NEW_BASE_PATH = "/masterAccountNew";
    public static final String MARKET_MODULATOR_BASE_PATH = "/marketModulator";
    public static final String BROKER_BASE_PATH = "/broker";
    public static final String MC_USER_BASE_PATH = "/mcUser";
    public static final String MC_USER_NEW_BASE_PATH = "/mcUserNew";
    public static final String MC_RETAIL_BASE_PATH = "/mcRetail";
    public static final String MC_RETAIL_NEW_BASE_PATH = "/mcRetailNew";
    public static final String ADDRESS_BASE_PATH = "/address";
    public static final String CHAT_BASE_PATH = "/chat";
    public static final String BUY_BALANCE_BASE_PATH = "/buyBalance";
    public static final String TRANSFER_TO_BANK_BASE_PATH = "/transferToBank";
    public static final String GIFT_CARD_BASE_PATH = "/giftCard";
    public static final String BANKER_BASE_PATH = "/banker";
    public static final String SMS_BASE_PATH = "/sms";
    public static final String MONEY_ORDER_BASE_PATH = "/moneyOrder";
    public static final String DEBIT_CARD_BASE_PATH = "/debitCard";
    public static final String NOTIFICATION_BASE_PATH = "/notification";
    public static final String MONEY_CALL_BASE_PATH = "/moneyCall";
    public static final String MONEY_MARKET_BASE_PATH = "/moneyMarket";
    public static final String SUBSCRIPTION_BASE_PATH = "/subscription";
    public static final String SUBSCRIPTION_EVENT_BASE_PATH = "/subscriptionEvent";
    public static final String MFA_BASE_PATH = "/mfa";
    public static final String BROADCASTING_BASE_PATH = "/broadcasting";
    public static final String CASH_BASE_PATH = "/cash";
    public static final String DIGITAL_BUSINESS_BASE_PATH = "/digitalBusiness";
    public static final String SHORTS_BASE_PATH = "/shorts";
    public static final String DONATION_BASE_PATH = "/donation";
    public static final String LIVE_STREAMING_BASE_PATH = "/liveStreaming";
    public static final String TAG_BASE_PATH = "/tag";

    public static void main(String[] args) throws Exception {
        File jettyServerFile = BaseFilesLocator.getJettyServerFile();
        JsonNode jettyServer = new ObjectMapper().readTree(jettyServerFile);
        if (!jettyServer.has("BASE_PORT") || !jettyServer.has("PORTS_QUANTITY")) {
            return;
        }
        Integer BASE_PORT = jettyServer.get("BASE_PORT").intValue();
        Integer PORTS_QUANTITY = jettyServer.get("PORTS_QUANTITY").intValue();
        List<Integer> ports = new ArrayList<>();
        if (PORTS_QUANTITY < 1) {
            PORTS_QUANTITY = 1;
        }
        if (PORTS_QUANTITY > 5) {
            PORTS_QUANTITY = 5;
        }
        for (int i = 0; i < PORTS_QUANTITY; i++) {
            ports.add(BASE_PORT + i);
        }
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server server = new Server(new QueuedThreadPool(512, 64));
        if (!ports.isEmpty()) {
            for (int port : ports) {
                ServerConnector http = new ServerConnector(server);
                http.setIdleTimeout(30000);
                http.setHost("0.0.0.0");
                http.setPort(port);
                server.addConnector(http);
            }
        } else {
            throw new Exception("There is no ports to run server");
        }
        server.setHandler(context);
        addCORSFilters(context);
        List<String> paths = new ArrayList<>();
        addAttachmentPaths(paths);
        addUserPaths(paths);
        addModelPaths(paths);
        addOTCPaths(paths);
        addOTCAdminPaths(paths);
        addMasterAccountPaths(paths);
        addMasterAccountNewPaths(paths);
        addMarketModulatorPaths(paths);
        addBrokerPaths(paths);
        addMCUserPaths(paths);
        addMCUserNewPaths(paths);
        addMCRetailPaths(paths);
        addMCRetailNewPaths(paths);
        addAddressPaths(paths);
        addChatPaths(paths);
        addBuyBalancePaths(paths);
        addTransferToBankPaths(paths);
        addGiftCardPaths(paths);
        addBankerPaths(paths);
        addSmsPaths(paths);
        addMoneyOrderPaths(paths);
        addDebitCardPaths(paths);
        addNotificationPaths(paths);
        addMoneyCallPaths(paths);
        addLiveStreamingPaths(paths);
        addBroadcastingPaths(paths);
        addCashPaths(paths);
        addMoneyMarketPaths(paths);
        addSubscriptionPaths(paths);
        addSubscriptionEventPaths(paths);
        addDigitalBusinessPaths(paths);
        addShortsPaths(paths);
        addDonationPaths(paths);
        addMFAPaths(paths);
        addTagPaths(paths);
        addTestPaths(paths);
        //addSecurityFilters(context, paths.toArray(new String[paths.size()]));
        addWebsocketServlets(context);
        addMultipartServlets(context);
        addMainServlets(context);
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    private static void addCORSFilters(ServletContextHandler context) {
        context.addFilter(CORSFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
    }

    private static void addSecurityFilters(ServletContextHandler context, String[] paths) {
        for (String path : paths) {
            FilterHolder securityFilterHolder = context.addFilter(SecurityFilter.class, path, EnumSet.allOf(DispatcherType.class));
            securityFilterHolder.setInitParameter("algorithm", "SHA256");
        }
    }

    private static void addWebsocketServlets(ServletContextHandler context) {
        context.addServlet(OrderServlet.class, "/order");
        context.addServlet(ExchangeServlet.class, "/exchange");
        context.addServlet(OTCServlet.class, "/otc");
        context.addServlet(OTCAdminServlet.class, "/otcAdmin");
        context.addServlet(UserServlet.class, "/user");
        context.addServlet(MarketOperationServlet.class, "/marketOperation");
        context.addServlet(ChatServlet.class, "/chat");
        context.addServlet(MCUserServlet.class, "/mcUser");
        context.addServlet(PriceServlet.class, "/price");
        context.addServlet(MoneyMarketServlet.class, "/moneyMarket");
    }

    private static void addMultipartServlets(ServletContextHandler context) {
        ServletHolder otcPostOperationMessageServletHolder = new ServletHolder(new OTCPostOperationMessageServlet());
        otcPostOperationMessageServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(otcPostOperationMessageServletHolder, "/otcPostOperationMessage");
        ServletHolder userAddAttachmentServletHolder = new ServletHolder(new UserAddAttachmentServlet());
        userAddAttachmentServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(userAddAttachmentServletHolder, "/userAddAttachment");
        ServletHolder uploadFileServletHolder = new ServletHolder(new UploadFileServlet());
        uploadFileServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(uploadFileServletHolder, "/uploadFile");
        ServletHolder mcRetailAddAttachmentServletHolder = new ServletHolder(new MCRetailAddAttachmentServlet());
        mcRetailAddAttachmentServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(mcRetailAddAttachmentServletHolder, "/mcRetailAddAttachment");
        ServletHolder mcUserPostMessageServletHolder = new ServletHolder(new MCUserPostMessageServlet());
        mcUserPostMessageServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(mcUserPostMessageServletHolder, "/mcUserPostMessage");
        ServletHolder mcUserPostMessageNewServletHolder = new ServletHolder(new MCUserPostMessageNewServlet());
        mcUserPostMessageNewServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(mcUserPostMessageNewServletHolder, "/mcUserPostMessageNew");
        ServletHolder moneyOrderCreateServletHolder = new ServletHolder(new MoneyOrderCreateServlet());
        moneyOrderCreateServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(moneyOrderCreateServletHolder, "/moneyOrderCreate");
        ServletHolder debitCardRequestServletHolder = new ServletHolder(new DebitCardRequestServlet());
        debitCardRequestServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(debitCardRequestServletHolder, "/debitCardRequest");
        ServletHolder broadcastingCreateServletHolder = new ServletHolder(new BroadcastingCreateServlet());
        broadcastingCreateServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(broadcastingCreateServletHolder, "/broadcastingCreate");
        ServletHolder broadcastingAddEpisodeTrailerServletHolder = new ServletHolder(new BroadcastingAddEpisodeTrailerServlet());
        broadcastingAddEpisodeTrailerServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(broadcastingAddEpisodeTrailerServletHolder, "/broadcastingAddEpisodeTrailer");
        ServletHolder broadcastingChangeImageServletHolder = new ServletHolder(new BroadcastingChangeImageServlet());
        broadcastingChangeImageServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(broadcastingChangeImageServletHolder, "/broadcastingChangeImage");
        ServletHolder shortsCreateServletHolder = new ServletHolder(new ShortsCreateServlet());
        shortsCreateServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(shortsCreateServletHolder, "/shortsCreate");
        ServletHolder liveStreamingCreateServletHolder = new ServletHolder(new LiveStreamingCreateServlet());
        liveStreamingCreateServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(liveStreamingCreateServletHolder, "/liveStreamingCreate");
        ServletHolder liveStreamingChangeImageServletHolder = new ServletHolder(new LiveStreamingChangeImageServlet());
        liveStreamingChangeImageServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(liveStreamingChangeImageServletHolder, "/liveStreamingChangeImage");

    }

    private static void addMainServlets(ServletContextHandler context) {
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", JERSEY_CLASSNAMES);
    }

    private static void addAttachmentPaths(List<String> paths) {
        paths.add(ATTACHMENT_BASE_PATH + "/getUserGAQRCode/*");
//        paths.add(ATTACHMENT_BASE_PATH + "/getRetailQRCode/*");
        paths.add(ATTACHMENT_BASE_PATH + "/getRetailFile/*");
        paths.add(ATTACHMENT_BASE_PATH + "/getUserFile/*");
        paths.add(ATTACHMENT_BASE_PATH + "/getOTCOperationFile/*");
    }

    private static void addUserPaths(List<String> paths) {
        paths.add(USER_BASE_PATH + "/create");
        paths.add(USER_BASE_PATH + "/addMasterWalletIds");
        paths.add(USER_BASE_PATH + "/list"); // PROBLEM
        paths.add(USER_BASE_PATH + "/getConfig/*");
        paths.add(USER_BASE_PATH + "/getConfigs");
        paths.add(USER_BASE_PATH + "/getBalance/*");
        paths.add(USER_BASE_PATH + "/activate");
        paths.add(USER_BASE_PATH + "/inactivate");
        paths.add(USER_BASE_PATH + "/balanceOperation");
//        paths.add(USER_BASE_PATH + "/getBalanceMovements/*");
//        paths.add(USER_BASE_PATH + "/getProcessingBalanceMovements");
//        paths.add(USER_BASE_PATH + "/processBalanceMovement/*");
//        paths.add(USER_BASE_PATH + "/processBalanceMovement");
//        paths.add(USER_BASE_PATH + "/currencyChange");
//        paths.add(USER_BASE_PATH + "/getMarketPrice/*");
        paths.add(USER_BASE_PATH + "/changeProfile");
        paths.add(USER_BASE_PATH + "/startVerification");
        paths.add(USER_BASE_PATH + "/startVerificationEmail");
//        paths.add(USER_BASE_PATH + "/getVerifications");
        paths.add(USER_BASE_PATH + "/processVerification");
//        paths.add(USER_BASE_PATH + "/addFieldToVerification");
//        paths.add(USER_BASE_PATH + "/cancelVerification");
//        paths.add(USER_BASE_PATH + "/postMessage/*");
//        paths.add(USER_BASE_PATH + "/markMessageAsReaded/*");
        paths.add(USER_BASE_PATH + "/addInfo");
        paths.add(USER_BASE_PATH + "/modifyInfo");
        paths.add(USER_BASE_PATH + "/changeToAdmin");
        paths.add(USER_BASE_PATH + "/addWallet");
        paths.add(USER_BASE_PATH + "/automaticChange");
//        paths.add(USER_BASE_PATH + "/getSegurityQuestions/*");
        paths.add(USER_BASE_PATH + "/checkSecurityQuestions");
//        paths.add(USER_BASE_PATH + "/getSendOpetarionType/*");
        paths.add(USER_BASE_PATH + "/delete");
        paths.add(USER_BASE_PATH + "/recoverDeleted");
        paths.add(USER_BASE_PATH + "/removeUserVerification");
        paths.add(USER_BASE_PATH + "/transferBTC");
        paths.add(USER_BASE_PATH + "/getReceiveAuthorizations/*");
        paths.add(USER_BASE_PATH + "/changeReceiveAuthorizationStatus");
        paths.add(USER_BASE_PATH + "/getReceiveAuthorizationMessage/*");
        paths.add(USER_BASE_PATH + "/listNames");
        paths.add(USER_BASE_PATH + "/addFlag");
        paths.add(USER_BASE_PATH + "/listByFlagColor/*");
        paths.add(USER_BASE_PATH + "/specialOption");
    }

    private static void addModelPaths(List<String> paths) {
//        paths.add(MODEL_BASE_PATH + "/create");
//        paths.add(MODEL_BASE_PATH + "/copy");
//        paths.add(MODEL_BASE_PATH + "/getConfig/*");
//        paths.add(MODEL_BASE_PATH + "/getBalance/*");
//        paths.add(MODEL_BASE_PATH + "/listNames/*");
//        paths.add(MODEL_BASE_PATH + "/list/*");
//        paths.add(MODEL_BASE_PATH + "/listAvailables/*");
//        paths.add(MODEL_BASE_PATH + "/getData/*");
//        paths.add(MODEL_BASE_PATH + "/getInvestedAmounts/*");
//        paths.add(MODEL_BASE_PATH + "/activate");
//        paths.add(MODEL_BASE_PATH + "/inactivate/*");
//        paths.add(MODEL_BASE_PATH + "/getComments/*");
//        paths.add(MODEL_BASE_PATH + "/addToBalance/*");
//        paths.add(MODEL_BASE_PATH + "/substractToBalance/*");
//        paths.add(MODEL_BASE_PATH + "/sendToFreeNotifications");
//        paths.add(MODEL_BASE_PATH + "/balanceOperation");
//        paths.add(MODEL_BASE_PATH + "/getInAlgorithmsInfo/*");
//        paths.add(MODEL_BASE_PATH + "/modifyDescription/*");
    }

    private static void addOTCPaths(List<String> paths) {
//        paths.add(OTC_BASE_PATH + "/getCurrencies");
//        paths.add(OTC_BASE_PATH + "/getClientPaymentTypes");
//        paths.add(OTC_BASE_PATH + "/getClientPaymentTypes/*");
//        paths.add(OTC_BASE_PATH + "/getClientPayment/*");
        paths.add(OTC_BASE_PATH + "/getPayments/*");
        paths.add(OTC_BASE_PATH + "/addPayment");
//        paths.add(OTC_BASE_PATH + "/removePayment/*");
        paths.add(OTC_BASE_PATH + "/addDollarBTCPayment");
//        paths.add(OTC_BASE_PATH + "/getDollarBTCPayment/*"); // PROBLEM
//        paths.add(OTC_BASE_PATH + "/getDollarBTCPaymentBalance");
//        paths.add(OTC_BASE_PATH + "/getDollarBTCPaymentBalances/*");
//        paths.add(OTC_BASE_PATH + "/getDollarBTCPaymentBalanceMovements"); // PROBLEM
        paths.add(OTC_BASE_PATH + "/editDollarBTCPayment");
        paths.add(OTC_BASE_PATH + "/addBalanceToDollarBTCPayment");
        paths.add(OTC_BASE_PATH + "/substractBalanceToDollarBTCPayment");
        paths.add(OTC_BASE_PATH + "/addOffer");
//        paths.add(OTC_BASE_PATH + "/addDynamicOffer");        
//        paths.add(OTC_BASE_PATH + "/editOffer");        
//        paths.add(OTC_BASE_PATH + "/editDynamicOffer");
        paths.add(OTC_BASE_PATH + "/removeOffer");
//        paths.add(OTC_BASE_PATH + "/getOffer/*");
//        paths.add(OTC_BASE_PATH + "/getOffers");
//        paths.add(OTC_BASE_PATH + "/getOffers/*");
//        paths.add(OTC_BASE_PATH + "/getOldOffers");
        paths.add(OTC_BASE_PATH + "/createOperation");
        paths.add(OTC_BASE_PATH + "/changeOperationStatus");
//        paths.add(OTC_BASE_PATH + "/getOperation/*");        
//        paths.add(OTC_BASE_PATH + "/getOperationIndexesAndValues");
//        paths.add(OTC_BASE_PATH + "/getOperations");
//        paths.add(OTC_BASE_PATH + "/postOperationMessage/*");
//        paths.add(OTC_BASE_PATH + "/getAutomaticChatMessages/*");
//        paths.add(OTC_BASE_PATH + "/getOperationCheckList/*");        
//        paths.add(OTC_BASE_PATH + "/modifyOperationCheckList");        
//        paths.add(OTC_BASE_PATH + "/getUSDPrice/*");
        paths.add(OTC_BASE_PATH + "/getLimits");
//        paths.add(OTC_BASE_PATH + "/getCharges"); // POST Y GET
//        paths.add(OTC_BASE_PATH + "/getOperationsOverall");
//        paths.add(OTC_BASE_PATH + "/getOfficesInfo/*");
        paths.add(OTC_BASE_PATH + "/fastChangeFromBTC");
        paths.add(OTC_BASE_PATH + "/fastChangeToBTC");
    }

    private static void addOTCAdminPaths(List<String> paths) {
//        paths.add(OTC_ADMIN_BASE_PATH + "/getCurrencies/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/editCurrencies");
//        paths.add(OTC_ADMIN_BASE_PATH + "/verification");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getDollarBTCPayments/*"); // PROBLEM
//        paths.add(OTC_ADMIN_BASE_PATH + "/getOffers/*"); // PROBLEM
//        paths.add(OTC_ADMIN_BASE_PATH + "/getOldOffers/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getOperations");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getOperationIndexesAndValues/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/updateFieldValues/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/resetOperationBalance/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getOperationBalanceParams/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/editOperationBalanceParams");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getClientsBalance");
//        paths.add(OTC_ADMIN_BASE_PATH + "/buyFromDollarBTCPayment");
//        paths.add(OTC_ADMIN_BASE_PATH + "/sellFromDollarBTCPayment");
//        paths.add(OTC_ADMIN_BASE_PATH + "/transferBetweenDollarBTCPayments");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getSpecialPayments/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/resetMoneyclickOperationBalance/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getMoneyclickOperationBalanceParams/*");
//        paths.add(OTC_ADMIN_BASE_PATH + "/editMoneyclickOperationBalanceParams")
//        paths.add(OTC_ADMIN_BASE_PATH + "/getMoneyclickClientsBalance");
//        paths.add(OTC_ADMIN_BASE_PATH + "/getChangeFactors");
        paths.add(OTC_ADMIN_BASE_PATH + "/editChangeFactors");
    }

    private static void addMasterAccountPaths(List<String> paths) {
//        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getNames");
//        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getDetails");
//        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getBalances");
//        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getBalance/*");
        paths.add(MASTER_ACCOUNT_BASE_PATH + "/transferBetweenMasters");
//        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getAutomaticRules");
        paths.add(MASTER_ACCOUNT_BASE_PATH + "/editAutomaticRules");
//        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getBalanceMovements"); // PROBLEM
        paths.add(MASTER_ACCOUNT_BASE_PATH + "/getConfig/*");
        paths.add(MASTER_ACCOUNT_BASE_PATH + "/addWallet");
        paths.add(MASTER_ACCOUNT_BASE_PATH + "/balanceOperationSend");
    }

    private static void addMasterAccountNewPaths(List<String> paths) {
//        paths.add(MASTER_ACCOUNT_NEW_BASE_PATH + "/getOTCMasterAccountNames/*");
//        paths.add(MASTER_ACCOUNT_NEW_BASE_PATH + "/getOTCMasterAccountBalances/*");
    }

    private static void addMarketModulatorPaths(List<String> paths) {
        paths.add(MARKET_MODULATOR_BASE_PATH + "/modifyAutomaticRules");
        paths.add(MARKET_MODULATOR_BASE_PATH + "/modifyManualRules");
//        paths.add(MARKET_MODULATOR_BASE_PATH + "/getAutomaticRules");
        paths.add(MARKET_MODULATOR_BASE_PATH + "/getManualRules");
//        paths.add(MARKET_MODULATOR_BASE_PATH + "/getActiveSymbols");
    }

    private static void addBrokerPaths(List<String> paths) {
//        paths.add(BROKER_BASE_PATH + "/getBalance/*");
//        paths.add(BROKER_BASE_PATH + "/getOfferParams/*");
//        paths.add(BROKER_BASE_PATH + "/addOffer");
//        paths.add(BROKER_BASE_PATH + "/addDynamicOffer");
//        paths.add(BROKER_BASE_PATH + "/editOffer");
//        paths.add(BROKER_BASE_PATH + "/editDynamicOffer");
//        paths.add(BROKER_BASE_PATH + "/removeOffer");
//        paths.add(BROKER_BASE_PATH + "/getOffer/*");
//        paths.add(BROKER_BASE_PATH + "/getOffers/*");
//        paths.add(BROKER_BASE_PATH + "/getOldOffers/*");
//        paths.add(BROKER_BASE_PATH + "/sendToPayment");
    }

    private static void addMCUserPaths(List<String> paths) {
        paths.add(MC_USER_BASE_PATH + "/send");
        paths.add(MC_USER_BASE_PATH + "/sendToPayment");
//        paths.add(MC_USER_BASE_PATH + "/buyBTC");
//        paths.add(MC_USER_BASE_PATH + "/sellBTC");
//        paths.add(MC_USER_BASE_PATH + "/getBalance/*");
        paths.add(MC_USER_BASE_PATH + "/getNewBalance/*");
        paths.add(MC_USER_BASE_PATH + "/getBalanceMovements/*");
//        paths.add(MC_USER_BASE_PATH + "/getAlerts/*");
//        paths.add(MC_USER_BASE_PATH + "/buyBTCRetail");
//        paths.add(MC_USER_BASE_PATH + "/cashbackRetail");
//        paths.add(MC_USER_BASE_PATH + "/getFastChangeFactor/*");
        paths.add(MC_USER_BASE_PATH + "/fastChange");
        paths.add(MC_USER_BASE_PATH + "/getReferralCodes");
        paths.add(MC_USER_BASE_PATH + "/deleteMessage");
        paths.add(MC_USER_BASE_PATH + "/deleteMessages");
        paths.add(MC_USER_BASE_PATH + "/getMessageAttachment/*");
        paths.add(MC_USER_BASE_PATH + "/sendNew");
        paths.add(MC_USER_BASE_PATH + "/postMessageOffer");
        paths.add(MC_USER_BASE_PATH + "/closeMessageOffer");
        paths.add(MC_USER_BASE_PATH + "/takeMessageOffer");
        paths.add(MC_USER_BASE_PATH + "/getMessageOffers");
        paths.add(MC_USER_BASE_PATH + "/getPairs");
        paths.add(MC_USER_BASE_PATH + "/buyBitcoins");
        paths.add(MC_USER_BASE_PATH + "/sellBitcoins");
        paths.add(MC_USER_BASE_PATH + "/buyCrypto");
        paths.add(MC_USER_BASE_PATH + "/sellCrypto");
        paths.add(MC_USER_BASE_PATH + "/cashback");
        paths.add(MC_USER_BASE_PATH + "/sendToPaymentNew");
    }

    private static void addMCUserNewPaths(List<String> paths) {
//        paths.add(MC_USER_NEW_BASE_PATH + "/buyBalanceRetail");
//        paths.add(MC_USER_NEW_BASE_PATH + "/sellBalanceRetail");
    }

    private static void addMCRetailPaths(List<String> paths) {
//        paths.add(MC_RETAIL_BASE_PATH + "/getOperations");
//        paths.add(MC_RETAIL_BASE_PATH + "/processOperation");
//        paths.add(MC_RETAIL_BASE_PATH + "/getRetail/*");
//        paths.add(MC_RETAIL_BASE_PATH + "/getRetails/*");
//        paths.add(MC_RETAIL_BASE_PATH + "/checkDevice");    
//        paths.add(MC_RETAIL_BASE_PATH + "/linkDevice");     
//        paths.add(MC_RETAIL_BASE_PATH + "/unlinkDevice");
//        paths.add(MC_RETAIL_BASE_PATH + "/getBalance/*");
//        paths.add(MC_RETAIL_BASE_PATH + "/getBalanceMovements/*");
//        paths.add(MC_RETAIL_BASE_PATH + "/activate/*");
//        paths.add(MC_RETAIL_BASE_PATH + "/inactivate/*");   
//        paths.add(MC_RETAIL_BASE_PATH + "/addToBTCEscrowFromBTC");     
//        paths.add(MC_RETAIL_BASE_PATH + "/create");
//        paths.add(MC_RETAIL_BASE_PATH + "/list");
//        paths.add(MC_RETAIL_BASE_PATH + "/list/*");
    }

    private static void addMCRetailNewPaths(List<String> paths) {
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/processOperation");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/getRetail/*");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/getRetails");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/getRetails/*");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/getBalance/*");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/getBalanceMovements/*");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/addEscrowFromMCUserBalance");  
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/substractEscrowToMCUserBalance");   
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/create");
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/linkDevice"); 
//        paths.add(MC_RETAIL_NEW_BASE_PATH + "/getChargesBalance");

    }

    private static void addAddressPaths(List<String> paths) {
//        paths.add(ADDRESS_BASE_PATH + "/create");
//        paths.add(ADDRESS_BASE_PATH + "/list/*");
    }

    private static void addChatPaths(List<String> paths) {
//        paths.add(CHAT_BASE_PATH + "/postMessage");
//        paths.add(CHAT_BASE_PATH + "/list");
//        paths.add(CHAT_BASE_PATH + "/markAdminMessagesAsReaded/*");
    }

    private static void addBuyBalancePaths(List<String> paths) {
        paths.add(BUY_BALANCE_BASE_PATH + "/getDollarBTCPayments/*");
        paths.add(BUY_BALANCE_BASE_PATH + "/createOperation");
        paths.add(BUY_BALANCE_BASE_PATH + "/changeOperationStatus");
    }

    private static void addTransferToBankPaths(List<String> paths) {
        paths.add(TRANSFER_TO_BANK_BASE_PATH + "/getOperations");
        paths.add(TRANSFER_TO_BANK_BASE_PATH + "/createProcess");
//        paths.add(TRANSFER_TO_BANK_BASE_PATH + "/getLastProcesses/*");
        paths.add(TRANSFER_TO_BANK_BASE_PATH + "/applyProcess");
    }

    private static void addGiftCardPaths(List<String> paths) {
//        paths.add(GIFT_CARD_BASE_PATH + "/activate");
        paths.add(GIFT_CARD_BASE_PATH + "/redeem");
        //paths.add(GIFT_CARD_BASE_PATH + "/send");
        paths.add(GIFT_CARD_BASE_PATH + "/resend");
        paths.add(GIFT_CARD_BASE_PATH + "/list/*");
    }

    private static void addBankerPaths(List<String> paths) {
        paths.add(BANKER_BASE_PATH + "/getCurrencies/*");
        paths.add(BANKER_BASE_PATH + "/addDollarBTCPayment");
        paths.add(BANKER_BASE_PATH + "/getDollarBTCPayments/*");
        paths.add(BANKER_BASE_PATH + "/getDollarBTCPaymentBalance");
        paths.add(BANKER_BASE_PATH + "/getDollarBTCPaymentBalances/*");
        paths.add(BANKER_BASE_PATH + "/getDollarBTCPaymentBalanceMovements");
        paths.add(BANKER_BASE_PATH + "/editDollarBTCPayment");
        paths.add(BANKER_BASE_PATH + "/getConfig/*");
        paths.add(BANKER_BASE_PATH + "/list/*");
        paths.add(BANKER_BASE_PATH + "/addCurrency/*");
        paths.add(BANKER_BASE_PATH + "/removeCurrency/*");
        paths.add(BANKER_BASE_PATH + "/getReferredUsers/*");
        paths.add(BANKER_BASE_PATH + "/changeOperationStatus");
    }

    private static void addSmsPaths(List<String> paths) {
        paths.add(SMS_BASE_PATH + "/send");
    }

    private static void addMoneyOrderPaths(List<String> paths) {
//        paths.add(MONEY_ORDER_BASE_PATH + "/getInfo/*");
        paths.add(MONEY_ORDER_BASE_PATH + "/create");
        paths.add(MONEY_ORDER_BASE_PATH + "/list/*");
        paths.add(MONEY_ORDER_BASE_PATH + "/process");
    }

    private static void addDebitCardPaths(List<String> paths) {
        //paths.add(DEBIT_CARD_BASE_PATH + "/changeStatus");
        //paths.add(DEBIT_CARD_BASE_PATH + "/addSubstractBalance");
        //paths.add(DEBIT_CARD_BASE_PATH + "/makePayment");
        //paths.add(DEBIT_CARD_BASE_PATH + "/getConfig/*");
    }

    private static void addMoneyCallPaths(List<String> paths) {
        //paths.add(MONEY_CALL_BASE_PATH + "/pay");
    }

    private static void addLiveStreamingPaths(List<String> paths) {
        //paths.add(LIVE_STREAMING_BASE_PATH + "/pay");
    }

    private static void addBroadcastingPaths(List<String> paths) {
        //paths.add(BROADCASTING_BASE_PATH + "/create");
    }

    private static void addCashPaths(List<String> paths) {
        //paths.add(CASH_BASE_PATH + "/buy");
        //paths.add(CASH_BASE_PATH + "/sell");
    }

    private static void addMoneyMarketPaths(List<String> paths) {
        paths.add(MONEY_MARKET_BASE_PATH + "/postOffer");
        paths.add(MONEY_MARKET_BASE_PATH + "/closeOffer");
        paths.add(MONEY_MARKET_BASE_PATH + "/takeOffer");
        paths.add(MONEY_MARKET_BASE_PATH + "/getOffers");
        paths.add(MONEY_MARKET_BASE_PATH + "/getPairs");
    }

    private static void addSubscriptionPaths(List<String> paths) {
        //paths.add(SUBSCRIPTION_BASE_PATH + "/create");
        //paths.add(SUBSCRIPTION_BASE_PATH + "/join");
    }

    private static void addSubscriptionEventPaths(List<String> paths) {
        //paths.add(SUBSCRIPTION_EVENT_BASE_PATH + "/create");
        //paths.add(SUBSCRIPTION_EVENT_BASE_PATH + "/join");
    }

    private static void addDigitalBusinessPaths(List<String> paths) {
        //paths.add(DIGITAL_BUSINESS_BASE_PATH + "/overview");
        //paths.add(DIGITAL_BUSINESS_BASE_PATH + "/getDetails");
    }

    private static void addShortsPaths(List<String> paths) {
        //paths.add(SHORTS_BASE_PATH + "/overview");
        //paths.add(SHORTS_BASE_PATH + "/getDetails");
    }

    private static void addDonationPaths(List<String> paths) {
        //paths.add(DONATION_BASE_PATH + "/overview");
        //paths.add(DONATION_BASE_PATH + "/getDetails");
    }

    private static void addMFAPaths(List<String> paths) {
        paths.add(MFA_BASE_PATH + "/sendCode");
    }

    private static void addTagPaths(List<String> paths) {
        //paths.add(TAG_BASE_PATH + "/create");
    }

    private static void addNotificationPaths(List<String> paths) {
//        paths.add(NOTIFICATION_BASE_PATH + "/createTopic");
//        paths.add(NOTIFICATION_BASE_PATH + "/getTopics");
//        paths.add(NOTIFICATION_BASE_PATH + "/getGroups");
    }

    private static void addTestPaths(List<String> paths) {
        paths.add("/test/get");
        paths.add("/test/getWithParams");
        paths.add("/test/post");
    }

}
