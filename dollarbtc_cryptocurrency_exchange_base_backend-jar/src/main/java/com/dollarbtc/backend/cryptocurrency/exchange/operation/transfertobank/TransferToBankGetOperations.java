/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminGetOperationsNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOperationsNew;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class TransferToBankGetOperations extends AbstractOperation<ArrayNode> {

    private final TransferToBankGetOperationsRequest transferToBankGetOperationsRequest;

    public TransferToBankGetOperations(TransferToBankGetOperationsRequest transferToBankGetOperationsRequest) {
        super(ArrayNode.class);
        this.transferToBankGetOperationsRequest = transferToBankGetOperationsRequest;
    }

    @Override
    protected void execute() {
        ArrayNode operations = mapper.createArrayNode();
        String[] currencies = new String[]{transferToBankGetOperationsRequest.getCurrency()};
        OTCOperationStatus[] otcOperationStatuses = new OTCOperationStatus[]{OTCOperationStatus.WAITING_FOR_PAYMENT};
        OTCOperationType[] otcOperationTypes = new OTCOperationType[]{
            OTCOperationType.SEND_TO_PAYMENT,
            OTCOperationType.SELL};
        operations.addAll(new OTCAdminGetOperationsNew(new OTCAdminGetOperationsNewRequest(
                transferToBankGetOperationsRequest.getUserName(),
                transferToBankGetOperationsRequest.getInitTimestamp(),
                transferToBankGetOperationsRequest.getFinalTimestamp(),
                null,
                currencies,
                otcOperationTypes,
                otcOperationStatuses,
                null)
        ).getResponse());
        Double totalAmount = 0.0;
        Iterator<JsonNode> operationsIterator = operations.iterator();
        while (operationsIterator.hasNext()) {
            JsonNode operationsIt = operationsIterator.next();
            Double operationsItAmount = operationsIt.get("amount").doubleValue();
            if (operationsItAmount > transferToBankGetOperationsRequest.getMaxPerOperationAmount()) {
                operationsIterator.remove();
                continue;
            }
            if (operationsItAmount < transferToBankGetOperationsRequest.getMinPerOperationAmount()) {
                operationsIterator.remove();
                continue;
            }
            if (!transferToBankGetOperationsRequest.getUserPaymentType().equals("BOTH")) {
                JsonNode operationsItClientPayment = operationsIt.get("clientPayment");
                String userPaymentType = "OWN";
                if (operationsItClientPayment.has("emailReceiver")) {
                    userPaymentType = "THIRD";
                }
                if (!userPaymentType.equals(transferToBankGetOperationsRequest.getUserPaymentType())) {
                    operationsIterator.remove();
                    continue;
                }
            }
            totalAmount = totalAmount + operationsItAmount;
            if (totalAmount > transferToBankGetOperationsRequest.getTotalAmount()) {
                operationsIterator.remove();
                break;
            }
        }
        super.response = operations;
    }

}
