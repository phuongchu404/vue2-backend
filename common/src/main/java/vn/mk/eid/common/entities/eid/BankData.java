package vn.mk.eid.common.entities.eid;

import lombok.Data;

@Data
public class BankData {
    String bankTransactionId = "";
    Integer bankAppId = 1; // 1- ATM, 2- counter, 3 - eZone, ...
    String transactionInfo = ""; // enxtended info
    Integer transactionType = 0; // extended info format
}
