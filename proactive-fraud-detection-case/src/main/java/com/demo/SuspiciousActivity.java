package com.demo;

import java.io.Serializable;
import java.util.List;

/**
 * SuspiciousActivity
 */
public class SuspiciousActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    private int riskRanking;
    private List<Transaction> transactions;

    @Override
    public String toString() {
        return String.format("SuspiciousActivity {riskRanking: %s, transactions: %s }", riskRanking, transactions);
    }

    /**
     * @return the riskRanking
     */
    public int getRiskRanking() {
        return riskRanking;
    }

    /**
     * @param riskRanking the riskRanking to set
     */
    public void setRiskRanking(int riskRanking) {
        this.riskRanking = riskRanking;
    }

    /**
     * @return the transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @param transactions the transactions to set
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}