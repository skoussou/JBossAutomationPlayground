package com.demo;

import java.io.Serializable;

/**
 * Fraud
 */
public class Fraud implements Serializable{

    
    private static final long serialVersionUID = 1L;

    // suspect, confirmed, false
    private String status;
    private boolean reissue;
    private boolean reimbursement;

    @Override
    public String toString() {
        return String.format("Fraud { status: %s, reimbursement: %s, reissue: %s }", status, reimbursement, reissue);
    }
    
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the reissue
     */
    public boolean isReissue() {
        return reissue;
    }

    /**
     * @param reissue the reissue to set
     */
    public void setReissue(boolean reissue) {
        this.reissue = reissue;
    }

    /**
     * @return the reimbursement
     */
    public boolean isReimbursement() {
        return reimbursement;
    }

    /**
     * @param reimbursement the reimbursement to set
     */
    public void setReimbursement(boolean reimbursement) {
        this.reimbursement = reimbursement;
    }
    
}