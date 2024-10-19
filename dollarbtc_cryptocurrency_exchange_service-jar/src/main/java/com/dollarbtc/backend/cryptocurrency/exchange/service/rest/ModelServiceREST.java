/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.JsonNodeResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.ListStringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelActivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelBalanceOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelCopyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelTestRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.SetStringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.StringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.ModelOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRegistry;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author conamerica90
 */
@Path("/model")
@XmlRegistry
public class ModelServiceREST {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(ModelCreateRequest modelCreateRequest) throws ServiceException {
        if (modelCreateRequest == null) {
            throw new ServiceException("modelCreateRequest is null");
        }
        if (modelCreateRequest.getConfig() == null) {
            throw new ServiceException("modelCreateRequest.getConfig() is null");
        }
        if (modelCreateRequest.getAmounts() == null || modelCreateRequest.getAmounts().isEmpty()) {
            throw new ServiceException("modelCreateRequest.getAmounts() is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(ModelOperation.create(modelCreateRequest)))
                .build();
    }

    @POST
    @Path("/copy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response copy(
            ModelCopyRequest modelCopyRequest
    ) throws ServiceException {
        if (modelCopyRequest == null) {
            throw new ServiceException("modelCopyRequest is null");
        }
        if (modelCopyRequest.getUserName() == null || modelCopyRequest.getUserName().equals("")) {
            throw new ServiceException("modelCopyRequest.getUserName() is null or empty");
        }
        if (modelCopyRequest.getModelName() == null || modelCopyRequest.getModelName().equals("")) {
            throw new ServiceException("modelCopyRequest.getModelName() is null or empty");
        }
        if (modelCopyRequest.getAmounts() == null || modelCopyRequest.getAmounts().isEmpty()) {
            throw new ServiceException("modelCopyRequest.getAmounts() is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(ModelOperation.copy(modelCopyRequest)))
                .build();
    }

    @POST
    @Path("/test/{currentTimestamp}/{testPastTimeInHours}/{lastTradePriceSpread}/{scanTimeInSeconds}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(
            ModelTestRequest modelTestRequest,
            @PathParam("currentTimestamp") String currentTimestamp,
            @PathParam("testPastTimeInHours") int testPastTimeInHours,
            @PathParam("lastTradePriceSpread") double lastTradePriceSpread,
            @PathParam("scanTimeInSeconds") int scanTimeInSeconds
    ) throws ServiceException {
        if (modelTestRequest == null) {
            throw new ServiceException("modelTestRequest is null");
        }
        if (modelTestRequest.getConfig() == null) {
            throw new ServiceException("modelTestRequest.getConfig() is null");
        }
        if (currentTimestamp == null || currentTimestamp.equals("")) {
            throw new ServiceException("currentTimestamp is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(ModelOperation.test(modelTestRequest, currentTimestamp, testPastTimeInHours, lastTradePriceSpread, scanTimeInSeconds)))
                .build();
    }

    @GET
    @Path("/getConfig/{modelName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(
            @PathParam("modelName") String modelName
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(ModelOperation.getConfig(modelName)))
                .build();
    }

    @GET
    @Path("/getBalance/{modelName}/{excludeInSymbolBaseBalances}/{excludeEstimatedValues}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("modelName") String modelName,
            @PathParam("excludeInSymbolBaseBalances") boolean excludeInSymbolBaseBalances, 
            @PathParam("excludeEstimatedValues") boolean excludeEstimatedValues
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(LocalData.getModelBalance(modelName, excludeInSymbolBaseBalances, excludeEstimatedValues, true)))
                .build();
    }

    @GET
    @Path("/listNames/{userName}/{excludeTest}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listNames(
            @PathParam("userName") String userName,
            @PathParam("excludeTest") boolean excludeTest
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        //CAMBIAR
        return Response
                .status(200)
                .entity(new SetStringResponse(ExchangeUtil.getModelNames(userName, excludeTest)))
                .build();
    }

    @GET
    @Path("/list/{userName}/{excludeTest}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(
            @PathParam("userName") String userName,
            @PathParam("excludeTest") boolean excludeTest
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(ModelOperation.list(userName)))
                .build();
    }
    
    @GET
    @Path("/getInitialAmounts/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInitialAmounts(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(ModelOperation.getInitialAmounts(userName))
                .build();
    }
    
    @GET
    @Path("/listAvailables/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAvailables(@PathParam("userName") String userName) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(ModelOperation.listAvailables(userName)))
                .build();
    }

    @GET
    @Path("/getData/{modelName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getData(@PathParam("modelName") String modelName) throws ServiceException {
        if (modelName == null || modelName.equals("") || modelName.contains("__")) {
            throw new ServiceException("modelName is null or empty or malformed");
        }
        return Response
                .status(200)
                .entity(ModelOperation.getData(modelName))
                .build();
    }
    
    @GET
    @Path("/getInvestedAmounts/{modelName}/{userProfile}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvestedAmounts(
            @PathParam("modelName") String modelName,
            @PathParam("userProfile") UserProfile userProfile
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        if (userProfile == null) {
            throw new ServiceException("userProfile is null");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(ModelOperation.getInvestedAmounts(modelName, userProfile)))
                .build();
    }
    
    @GET
    @Path("/getTestStatus/{modelName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getTestStatus(
            @PathParam("modelName") String modelName
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(ModelOperation.getTestStatus(modelName)))
                .build();
    }

    @POST
    @Path("/activate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response activate(
            ModelActivateRequest modelActivateRequest
    ) throws ServiceException {
        if (modelActivateRequest == null) {
            throw new ServiceException("modelActivateRequest is null");
        }
        if (modelActivateRequest.getModelName() == null || modelActivateRequest.getModelName().equals("")) {
            throw new ServiceException("modelActivateRequest.getModelName() is null or empty");
        }
        if (modelActivateRequest.getInitialAmounts() == null || modelActivateRequest.getInitialAmounts().isEmpty()) {
            throw new ServiceException("modelActivateRequest.getInitialAmounts() is null or empty");
        }
        ModelOperation.changeAmounts(modelActivateRequest.getModelName(), modelActivateRequest.getInitialAmounts());
        String modelActivateResult = ModelOperation.process(modelActivateRequest.getModelName(), ModelOperation.ProcessType.ACTIVATE);
        if (!modelActivateResult.equals("OK")) {
            return Response
                    .status(200)
                    .entity(new StringResponse(modelActivateResult))
                    .build();
        }
        return Response
                .status(200)
                .entity(new StringResponse("OK"))
                .build();
    }

    @GET
    @Path("/inactivate/{modelName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response inactivate(
            @PathParam("modelName") String modelName
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(ModelOperation.process(modelName, ModelOperation.ProcessType.INACTIVATE)))
                .build();
    }

    @GET
    @Path("/getComments/{modelName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(
            @PathParam("modelName") String modelName
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new ListStringResponse(ModelOperation.getComments(modelName)))
                .build();
    }

    @GET
    @Path("/addToBalance/{modelName}/{amount}/{currency}/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StringResponse addToBalance(
            @PathParam("modelName") String modelName,
            @PathParam("amount") double amount,
            @PathParam("currency") String currency,
            @PathParam("type") String type
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        throw new ServiceException("NOT IMPLEMENTED");
    }

    @GET
    @Path("/substractToBalance/{modelName}/{amount}/{currency}/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StringResponse substractToBalance(
            @PathParam("modelName") String modelName,
            @PathParam("amount") double amount,
            @PathParam("currency") String currency,
            @PathParam("type") String type
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        throw new ServiceException("NOT IMPLEMENTED");
    }

    @POST
    @Path("/sendToFreeNotifications")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendToFreeNotifications(@Context HttpServletRequest request) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        try {
            String body = IOUtils.toString(request.getInputStream());
            for (String param : body.split("&")) {
                String[] ps = param.split("=");
                if (ps.length != 2) {
                    continue;
                }
                String key = ps[0];
                String value = ps[1].replace("%40", "@").replace("+", " ").replace("%2B", "+");
                params.put(key, value);
            }
        } catch (IOException ex) {
            Logger.getLogger(MailServiceREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!params.containsKey("anti-honeypot") || !params.get("anti-honeypot").equals("RealMadrid180380...")) {
            throw new ServiceException("spam");
        }
        if (!params.containsKey("userModelName")) {
            throw new ServiceException("userModelName is not present");
        }
        if (!params.containsKey("exchangeId")) {
            throw new ServiceException("exchangeId is not present");
        }
        if (!params.containsKey("symbol")) {
            throw new ServiceException("symbol is not present");
        }
        if (!params.containsKey("name")) {
            throw new ServiceException("name is not present");
        }
        if (!params.containsKey("email")) {
            throw new ServiceException("email is not present");
        }
        String result = ModelOperation.sendToFreeNotifications(params.get("userModelName"), params.get("exchangeId"), params.get("symbol"), params.get("name"), params.get("email"), params.get("phone"));
        return Response
                .status(200)
                .entity(result)
                .build();
    }
    
    @POST
    @Path("/balanceOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response balanceOperation(
            ModelBalanceOperationRequest modelBalanceOperationRequest
    ) throws ServiceException {
        if (modelBalanceOperationRequest.getUserModelName() == null || modelBalanceOperationRequest.getUserModelName().equals("")) {
            throw new ServiceException("modelBalanceOperationRequest.getUserModelName() is null or empty");
        }
        if (modelBalanceOperationRequest.getBalanceOperationType() == null) {
            throw new ServiceException("modelBalanceOperationRequest.getBalanceOperationType() is null");
        }
        switch (modelBalanceOperationRequest.getBalanceOperationType()) {
            case MOVE_RESERVE:
                return Response
                        .status(200)
                        .entity(new StringResponse(ModelOperation.moveReserveToUser(modelBalanceOperationRequest.getUserModelName())))
                        .build();
            default:
                return Response
                        .status(200)
                        .entity(new StringResponse("BALANCE OPERATION TYPE NOT SUPPORTED"))
                        .build();
        }

    }
    
    @GET
    @Path("/getInAlgorithmsInfo/{modelName}/{exchangeId}/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInAlgorithmsInfo(
            @PathParam("modelName") String modelName,
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(ModelOperation.getInAlgorithmsInfo(modelName, exchangeId, symbol)))
                .build();
    }
    
    @GET
    @Path("/modifyDescription/{modelName}/{description}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyDescription(
            @PathParam("modelName") String modelName,
            @PathParam("description") String description
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        if (description == null || description.equals("")) {
            throw new ServiceException("description is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(ModelOperation.modifyDescription(modelName, description)))
                .build();
    }

}
