package org.zeroref.borg.timeouts.domain;

public class OrderRefundAllowance {
    private int qualifiedRefund;

    public OrderRefundAllowance(int qualifiedRefund) {

        this.qualifiedRefund = qualifiedRefund;
    }

    public int getQualifiedRefund() {
        return qualifiedRefund;
    }

    public void setQualifiedRefund(int qualifiedRefund) {
        this.qualifiedRefund = qualifiedRefund;
    }
}
