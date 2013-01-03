package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class RoyaltyReportLine implements Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  private FiscalPeriodDTO period;
  private ApplicationDTO application;
  private ZoneDTO zone;
  private int salesNumber;
  private BigDecimal originalCurrencyAmount;
  private String originalCurrency;
  private BigDecimal originalCurrencyTotalAmount;
  private String referenceCurrency;
  private BigDecimal referenceCurrencyTotalAmount;
  private BigDecimal referenceCurrencyProceedsAfterTaxTotalAmount;
  private BigDecimal referenceCurrencyCompanyRoyaltiesTotalAmount;

  public FiscalPeriodDTO getPeriod() {
    return period;
  }

  public void setPeriod(FiscalPeriodDTO period) {
    this.period = period;
  }

  public ApplicationDTO getApplication() {
    return application;
  }

  public void setApplication(ApplicationDTO application) {
    this.application = application;
  }

  public ZoneDTO getZone() {
    return zone;
  }

  public void setZone(ZoneDTO zone) {
    this.zone = zone;
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

  public BigDecimal getOriginalCurrencyTotalAmount() {
    return originalCurrencyTotalAmount;
  }

  public void setOriginalCurrencyTotalAmount(BigDecimal originalCurrencyTotalAmount) {
    this.originalCurrencyTotalAmount = originalCurrencyTotalAmount;
  }

  public String getReferenceCurrency() {
    return referenceCurrency;
  }

  public void setReferenceCurrency(String referenceCurrency) {
    this.referenceCurrency = referenceCurrency;
  }

  public BigDecimal getReferenceCurrencyTotalAmount() {
    return referenceCurrencyTotalAmount;
  }

  public void setReferenceCurrencyTotalAmount(BigDecimal referenceCurrencyTotalAmount) {
    this.referenceCurrencyTotalAmount = referenceCurrencyTotalAmount;
  }

  public BigDecimal getReferenceCurrencyProceedsAfterTaxTotalAmount() {
    return referenceCurrencyProceedsAfterTaxTotalAmount;
  }

  public void setReferenceCurrencyProceedsAfterTaxTotalAmount(BigDecimal referenceCurrencyProceedsAfterTaxTotalAmount) {
    this.referenceCurrencyProceedsAfterTaxTotalAmount = referenceCurrencyProceedsAfterTaxTotalAmount;
  }

  public BigDecimal getReferenceCurrencyCompanyRoyaltiesTotalAmount() {
    return referenceCurrencyCompanyRoyaltiesTotalAmount;
  }

  public void setReferenceCurrencyCompanyRoyaltiesTotalAmount(BigDecimal referenceCurrencyCompanyRoyaltiesTotalAmount) {
    this.referenceCurrencyCompanyRoyaltiesTotalAmount = referenceCurrencyCompanyRoyaltiesTotalAmount;
  }

}
