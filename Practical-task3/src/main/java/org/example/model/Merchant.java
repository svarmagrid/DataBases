package org.example.model;

public class Merchant {
    private int merchantId;
    private String merchantName;

    public Merchant(int merchantId, String merchantName) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }

    public int getMerchantId() { return merchantId; }
    public String getMerchantName() { return merchantName; }
}