package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.RoyaltyDAO;
import com.tinsys.itc_reporting.dao.TaxDAO;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("royaltyReport")
@Transactional
public class RoyaltyReportImpl implements RoyaltyReport {

  @Autowired
  @Qualifier("fxRateDAO")
  private FXRateDAO fxRateDAO;

  @Autowired
  @Qualifier("taxDAO")
  private TaxDAO taxDAO;

  @Autowired
  @Qualifier("royaltyDAO")
  private RoyaltyDAO royaltyDAO;

  private boolean firstLine;

  private RoyaltyReportLine reportLine;
  private List<RoyaltyReportLine> report;

  private FiscalPeriodDTO currentPeriod = null;
  private ApplicationDTO currentApplication = null;
  private ZoneDTO currentZone = null;
  private BigDecimal currentPrice = new BigDecimal(0);

  private BigDecimal changeRate = new BigDecimal(0);
  private BigDecimal taxRate = new BigDecimal(0);

  private List<RoyaltyDTO> royalties;
  private List<Tax> taxes = null;
  private ArrayList<FXRateDTO> fxRates = null;
  private String currency = null;
  private RoyaltyDTO royalty = null;

  private SalesDTO salesDTO;

  public FXRateDAO getFxRateDAO() {
    return fxRateDAO;
  }

  public void setFxRateDAO(FXRateDAO fxRateDAO) {
    this.fxRateDAO = fxRateDAO;
  }

  public TaxDAO getTaxDAO() {
    return taxDAO;
  }

  public void setTaxDAO(TaxDAO taxDAO) {
    this.taxDAO = taxDAO;
  }

  public RoyaltyDAO getRoyaltyDAO() {
    return royaltyDAO;
  }

  public void setRoyaltyDAO(RoyaltyDAO royaltyDAO) {
    this.royaltyDAO = royaltyDAO;
  }

  public boolean isFirstLine() {
    return firstLine;
  }

  public void setFirstLine(boolean firstLine) {
    this.firstLine = firstLine;
  }

  public RoyaltyReportLine getReportLine() {
    return reportLine;
  }

  public void setReportLine(RoyaltyReportLine reportLine) {
    this.reportLine = reportLine;
  }

  public List<RoyaltyReportLine> getReport() {
    return report;
  }

  public void setReport(List<RoyaltyReportLine> report) {
    this.report = report;
  }

  @Override
  public void init(CompanyDTO company) {
    this.firstLine = true;
    this.report = new ArrayList<RoyaltyReportLine>();
    this.currentPeriod = null;
    this.currentPrice = null;
    this.currentApplication = null;
    this.currency = null;
    this.currentZone = null;
    this.resetLine();
    royalties = royaltyDAO.getAllRoyalty(company);
  }

  @Override
  public boolean isNewLine() {
    boolean result = this.firstLine || this.currentPeriod.equals(salesDTO.getPeriod()) || this.currentZone.equals(salesDTO.getZone()) || this.currentApplication.equals(salesDTO.getApplication()) || this.currentPrice.equals(salesDTO.getIndividualPrice());
    this.firstLine = false;
    return result;
  }

  @Override
  public void addLine() {
    if (currentPeriod != null && this.reportLine != null) {
      this.reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().multiply(changeRate));
      this.reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(reportLine.getReferenceCurrencyProceedsAfterTaxTotalAmount().multiply(changeRate));
      this.reportLine.setReferenceCurrencyTotalAmount(reportLine.getReferenceCurrencyTotalAmount().multiply(changeRate));
      this.report.add(reportLine);
    }
  }

  @Override
  public void resetLine() {
    this.reportLine = new RoyaltyReportLine();
    this.reportLine.setOriginalCurrencyAmount(new BigDecimal(0));
    this.reportLine.setOriginalCurrencyTotalAmount(new BigDecimal(0));
    this.reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(new BigDecimal(0));
    this.reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(new BigDecimal(0));
    this.reportLine.setReferenceCurrencyTotalAmount(new BigDecimal(0));
  }

  @Override
  public void setCurrentData() {
    if (this.isNewLine()){
    this.checkNewPeriod(salesDTO.getPeriod());
    this.checkNewZone(salesDTO.getZone());
    this.checkNewApplication(salesDTO.getApplication());
    this.currentPrice = salesDTO.getIndividualPrice();
    this.reportLine.setPeriod(this.currentPeriod);
    currentApplication = salesDTO.getApplication();
    this.reportLine.setApplication(currentApplication);
    this.reportLine.setZone(currentZone);
    this.changeRate = this.getFXRateAndSetCurrency();
    this.taxRate = this.getTaxRate();
    this.royalty = this.getRoyalty();}
    reportLine.setSalesNumber(reportLine.getSalesNumber() + salesDTO.getSoldUnits());
    this.reportLine.setOriginalCurrencyAmount(currentPrice);
    this.reportLine.setOriginalCurrency(currentZone.getCurrencyISO());
    this.reportLine.setOriginalCurrencyTotalAmount(reportLine.getOriginalCurrencyTotalAmount().add(salesDTO.getTotalPrice()));
    this.reportLine.setReferenceCurrency(currency);
    BigDecimal proceedsAfterTax = this.getProceedsAfterTax(salesDTO.getTotalProceeds());
    this.reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(reportLine.getReferenceCurrencyProceedsAfterTaxTotalAmount().add(
        getProceedsAfterTax(salesDTO.getTotalProceeds())));
    BigDecimal totalAmount = ((salesDTO.getTotalPrice() != null) ? salesDTO.getTotalPrice() : new BigDecimal(0));
    this.reportLine.setReferenceCurrencyTotalAmount(reportLine.getReferenceCurrencyTotalAmount().add(totalAmount));
    if (royalty.getShareRate().compareTo(new BigDecimal(0)) != 0 && proceedsAfterTax.compareTo(new BigDecimal(0)) != 0) {
      reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().add(
          this.computeRoyalty(proceedsAfterTax, totalAmount, salesDTO.getTotalPrice())));
    }
  }

  private BigDecimal computeRoyalty(BigDecimal proceedsAfterTax, BigDecimal totalAmount, BigDecimal totalPrice) {

    BigDecimal vatFactor = new BigDecimal(0);
    if (changeRate.equals(new BigDecimal(0))) {
      throw new RuntimeException("Change Rate not found for " + currentZone.getName() + " for period " + currentPeriod);
    }
    try {
      if (royalty.isOnSale()) {
        vatFactor = (totalAmount.multiply(new BigDecimal(0.7))).divide((proceedsAfterTax), 2, RoundingMode.HALF_UP);
        return reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().add(
            ((totalPrice.divide(vatFactor, 5, RoundingMode.HALF_UP)).multiply(royalty.getShareRate().divide(new BigDecimal(100), 5, RoundingMode.HALF_UP))));
      } else {
        return reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().add(
            (proceedsAfterTax.multiply(royalty.getShareRate()).divide(new BigDecimal(100), 5, RoundingMode.HALF_UP)));
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private BigDecimal getProceedsAfterTax(BigDecimal totalProceeds) {
    if (totalProceeds != null) {
      return totalProceeds.multiply((new BigDecimal(1).subtract(taxRate)));
    }
    return new BigDecimal(0);
  }

  private RoyaltyDTO getRoyalty() {
    for (RoyaltyDTO royalty : royalties) {
      if (royalty.getApplication().getId().equals(currentApplication.getId()) && royalty.getZones().contains(currentZone)) {
        return royalty;
      }
    }
    return null;
  }

  private BigDecimal getTaxRate() {
    for (Tax tax : taxes) {
      if (tax.getId() != null && tax.getZone().getId().equals(currentZone.getId())) {
        return tax.getRate();
      }
    }
    return new BigDecimal(0);
  }

  private BigDecimal getFXRateAndSetCurrency() {
    currency = null;
    for (FXRateDTO fxRate : fxRates) {
      if (fxRate.getId() != null && fxRate.getZone().getId() == currentZone.getId()) {
        currency = fxRate.getCurrencyIso();
        return fxRate.getRate();
      }
    }
    return new BigDecimal(0);
  }

  private void checkNewZone(ZoneDTO zone) {
  /*  if (this.currentZone == null || !this.currentZone.equals(zone)) {
      if (this.currentZone != null) {
        this.newZone = true;
      } else {
        this.newZone = false;
      }
      this.currentZone = zone;
      for (Tax tax : taxes) {
        if (tax.getZone().equals(currentZone)) {
          taxRate = tax.getRate();
          break;
        }
      }
    } else {
      this.newZone = false;
    }*/
  }

  private void checkNewPeriod(FiscalPeriodDTO period) {
  /*  if (this.currentPeriod == null || !this.currentPeriod.equals(period)) {
      if (this.currentPeriod != null) {
        this.newPeriod = true;
      } else {
        this.newPeriod = false;
      }
      this.currentPeriod = period;
      taxes = taxDAO.getTaxesForPeriod(this.currentPeriod);
      fxRates = fxRateDAO.getAllFXRatesForPeriod(this.currentPeriod);
    } else {
      this.newPeriod = false;
    }*/
  }
  
  private void checkNewApplication(ApplicationDTO application) {
  /*  if (this.currentApplication == null || !this.currentApplication.equals(application)) {
      if (this.currentApplication != null) {
        this.newApplication = true;
      } else {
        this.newApplication = false;
      }
      this.currentApplication = application;
    } else {
      this.newApplication = false;
    }*/
  }

  public void setSalesDTO(SalesDTO salesDTO) {
    this.salesDTO = salesDTO;
    
  }

}
