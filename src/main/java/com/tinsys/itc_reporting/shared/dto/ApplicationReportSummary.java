package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ApplicationReportSummary implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String applicationName;
    private int salesNumber;
    private BigDecimal originalCurrencyAmount;
    private String originalCurrency;
    private BigDecimal referenceCurrencyAmount;
    private String referenceCurrency;  
    
    public String getApplicationName() {
        return applicationName;
    }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    public int getSalesNumber() {
        return salesNumber;
    }
    public void setSalesNumber(int salesNumber) {
        this.salesNumber = salesNumber;
    }
    public BigDecimal getOriginalCurrencyAmount() {
        return originalCurrencyAmount;
    }
    public void setOriginalCurrencyAmount(BigDecimal originalCurrencyAmount) {
        this.originalCurrencyAmount = originalCurrencyAmount;
    }
    public String getOriginalCurrency() {
        return originalCurrency;
    }
    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }
    public BigDecimal getReferenceCurrencyAmount() {
        return referenceCurrencyAmount;
    }
    public void setReferenceCurrencyAmount(BigDecimal referenceCurrencyAmount) {
        this.referenceCurrencyAmount = referenceCurrencyAmount;
    }
    public String getReferenceCurrency() {
        return referenceCurrency;
    }
    public void setReferenceCurrency(String referenceCurrency) {
        this.referenceCurrency = referenceCurrency;
    }  
}
