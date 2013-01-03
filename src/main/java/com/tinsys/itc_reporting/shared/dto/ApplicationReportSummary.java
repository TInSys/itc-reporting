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
  private String originalCurrency;
  private BigDecimal originalCurrencyAmount;
  private BigDecimal referenceCurrencyAmount;
  private BigDecimal originalCurrencyProceeds;
  private BigDecimal referenceCurrencyProceeds;
  private BigDecimal originalCurrencyProceedsAfterTax;
  private BigDecimal referenceCurrencyProceedsAfterTax;

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

  public String getOriginalCurrency() {
    return originalCurrency;
  }

  public void setOriginalCurrency(String originalCurrency) {
    this.originalCurrency = originalCurrency;
  }

  public BigDecimal getOriginalCurrencyAmount() {
    return originalCurrencyAmount;
  }

  public void setOriginalCurrencyAmount(BigDecimal originalCurrencyAmount) {
    this.originalCurrencyAmount = originalCurrencyAmount;
  }

  public BigDecimal getReferenceCurrencyAmount() {
    return referenceCurrencyAmount;
  }

  public void setReferenceCurrencyAmount(BigDecimal referenceCurrencyAmount) {
    this.referenceCurrencyAmount = referenceCurrencyAmount;
  }

  public BigDecimal getOriginalCurrencyProceeds() {
    return originalCurrencyProceeds;
  }

  public void setOriginalCurrencyProceeds(BigDecimal originalCurrencyProceeds) {
    this.originalCurrencyProceeds = originalCurrencyProceeds;
  }

  public BigDecimal getReferenceCurrencyProceeds() {
    return referenceCurrencyProceeds;
  }

  public void setReferenceCurrencyProceeds(BigDecimal referenceCurrencyProceeds) {
    this.referenceCurrencyProceeds = referenceCurrencyProceeds;
  }

  public String getReferenceCurrency() {
    return referenceCurrency;
  }

  public void setReferenceCurrency(String referenceCurrency) {
    this.referenceCurrency = referenceCurrency;
  }

  public BigDecimal getOriginalCurrencyProceedsAfterTax() {
    return originalCurrencyProceedsAfterTax;
  }

  public void setOriginalCurrencyProceedsAfterTax(BigDecimal originalCurrencyProceedsAfterTax) {
    this.originalCurrencyProceedsAfterTax = originalCurrencyProceedsAfterTax;
  }

  public BigDecimal getReferenceCurrencyProceedsAfterTax() {
    return referenceCurrencyProceedsAfterTax;
  }

  public void setReferenceCurrencyProceedsAfterTax(BigDecimal referenceCurrencyProceedsAfterTax) {
    this.referenceCurrencyProceedsAfterTax = referenceCurrencyProceedsAfterTax;
  }

}
