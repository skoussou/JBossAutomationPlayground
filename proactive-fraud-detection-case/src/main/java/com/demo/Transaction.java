package com.demo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

import org.kie.api.definition.type.Label;

/**
 * Transaction
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    // Amount
    private Double amount;
    // Date
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSZ")
    @JsonSerialize(using=DateSerializer.class)
    private Date dateTimeExec;
    // Auth Code
    @Label("Auth Code")
    private String authCode;
    // Merchant Name
    private String merchantName;
    // Merchant Code
    @Label("Merchant Code")
    private String merchantCode;
    // Location
    private String location;
    // CardType (debit, secured credit, unsecured credit, prepaid, reward)
    @Label("Card Type")
    private String cardType;

    @Override
    public String toString() {
        return String.format(
                "Transaction {amount: %s, authCode: %s, cardType: %s, dateTimeExec: %s, location: %s, merchantCode: %s, merchantName: %s }",
                amount, authCode, cardType, dateTimeExec, location, merchantCode, merchantName);
    }

    /**
     * @return the amount
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * @return the dateTimeExec
     */
    public Date getDateTimeExec() {
        return dateTimeExec;
    }

    /**
     * @param dateTimeExec the date to set
     */
    public void setDateTimeExec(Date dateTimeExec) {
        this.dateTimeExec = dateTimeExec;
    }

    /**
     * @return the authCode
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * @param authCode the authCode to set
     */
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    /**
     * @return the merchantName
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * @param merchantName the merchantName to set
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    /**
     * @return the merchantCode
     */
    public String getMerchantCode() {
        return merchantCode;
    }

    /**
     * @param merchantCode the merchantCode to set
     */
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the cardType
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * @param cardType the cardType to set
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

}