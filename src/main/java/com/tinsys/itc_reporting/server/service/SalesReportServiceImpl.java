package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.SalesReportService;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.SalesDAO;
import com.tinsys.itc_reporting.dao.TaxDAO;
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.ApplicationReportSummary;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneReportSummary;

@Service("salesReportService")
@Transactional
public class SalesReportServiceImpl implements SalesReportService {

  private static final Logger logger = Logger.getLogger(SalesReportServiceImpl.class);
  private final static String ZONE_TOTAL_COLUMN = "Total by Zone";
  private BigDecimal referenceCurrencyAmountGrandTotal = new BigDecimal(0);
  private BigDecimal referenceCurrencyProceedsAmountGrandTotal = new BigDecimal(0);
  private BigDecimal referenceCurrencyProceedsAfterTaxGrandTotal = new BigDecimal(0);
  private String referenceCurrency = null;

  @Autowired
  @Qualifier("salesDAO")
  private SalesDAO salesDAO;

  @Autowired
  @Qualifier("fxRateDAO")
  private FXRateDAO fxRateDAO;

  @Autowired
  @Qualifier("taxDAO")
  private TaxDAO taxDAO;

  @Override
  public List<ZoneReportSummary> getMonthlyReport(FiscalPeriodDTO period) throws RuntimeException {
    logger.debug("Preparing report");
    referenceCurrencyAmountGrandTotal = new BigDecimal(0);
    referenceCurrencyProceedsAmountGrandTotal = new BigDecimal(0);
    referenceCurrencyProceedsAfterTaxGrandTotal = new BigDecimal(0);
    List<Sales> sales = salesDAO.getAllSales(period);
    List<Tax> taxes = taxDAO.getTaxesForPeriod(period);
    ArrayList<FXRateDTO> fxRates = fxRateDAO.getAllFXRatesForPeriod(period);
    BigDecimal changeRate = new BigDecimal(0);
    Zone currentZone = null;
    Application currentApplication = null;
    List<ZoneReportSummary> monthReportList = null;
    ApplicationReportSummary applicationSumary = null;
    ZoneReportSummary monthReportLine = new ZoneReportSummary();
    monthReportLine.setApplications(new ArrayList<ApplicationReportSummary>());
    ZoneReportSummary monthReportLineTotal = new ZoneReportSummary();
    monthReportLineTotal.setApplications(new ArrayList<ApplicationReportSummary>());
    BigDecimal taxRate = new BigDecimal(0);

    if (sales != null && sales.size() > 0) {
      logger.debug("Processing  " + sales.size() + " lines");
      for (Sales sale : sales) {
        Zone zone = sale.getZone();

        // new Zone and not first pass
        if (zone != currentZone && monthReportList != null) {
          if (applicationSumary != null) {
            monthReportLine.getApplications().add(applicationSumary);
            applicationSumary = null;
          }
          monthReportList.add(monthReportLine);
          monthReportLineTotal = appsTotal(monthReportLineTotal, monthReportLine);
          monthReportLine = new ZoneReportSummary();
          monthReportLine.setApplications(new ArrayList<ApplicationReportSummary>());
          changeRate = this.getChangeRate(fxRates, zone);
          taxRate = this.getTaxRate(taxes, zone);
        } else {
          if (currentZone == null) { // first pass, fetch change rate and
                                     // taxRate
            changeRate = this.getChangeRate(fxRates, zone);
            taxRate = this.getTaxRate(taxes, zone);
          }
        }
        Application application = sale.getApplication();
        if (application != currentApplication && applicationSumary != null) { // add application subtotals "column" to current zone
          monthReportLine.getApplications().add(applicationSumary);
          applicationSumary = new ApplicationReportSummary();
          applicationSumary.init();
        } else {
          if (applicationSumary == null) {// first pass
            applicationSumary = new ApplicationReportSummary();
            applicationSumary.init();
          }
        }
        // add sales data to current application in current zone
        monthReportLine.setZoneName(zone.getName());
        applicationSumary.setApplicationName(application.getName());
        applicationSumary.setSalesNumber(applicationSumary.getSalesNumber() + sale.getSoldUnits());
        applicationSumary.setOriginalCurrency(sale.getZone().getCurrencyISO());
        applicationSumary.setOriginalCurrencyAmount(applicationSumary.getOriginalCurrencyAmount().add(sale.getTotalPrice()));

        applicationSumary.setOriginalCurrencyProceeds(applicationSumary.getOriginalCurrencyProceeds().add(
            (sale.getTotalProceeds() != null) ? sale.getTotalProceeds() : new BigDecimal(0)));
        applicationSumary.setOriginalCurrencyProceedsAfterTax(applicationSumary.getOriginalCurrencyProceedsAfterTax().add(
            (sale.getTotalProceeds() != null) ? (sale.getTotalProceeds().multiply((new BigDecimal(1).subtract(taxRate)))) : new BigDecimal(0)));

        applicationSumary.setReferenceCurrency(referenceCurrency);
        applicationSumary.setReferenceCurrencyAmount(applicationSumary.getReferenceCurrencyAmount().add(
            ((sale.getTotalPrice() != null) ? sale.getTotalPrice() : new BigDecimal(0)).multiply(changeRate)));
        applicationSumary.setReferenceCurrencyProceeds(applicationSumary.getReferenceCurrencyProceeds().add(
            ((sale.getTotalProceeds() != null) ? sale.getTotalProceeds() : new BigDecimal(0)).multiply(changeRate)));
        applicationSumary.setReferenceCurrencyProceedsAfterTax(applicationSumary.getReferenceCurrencyProceedsAfterTax().add(
            ((sale.getTotalProceeds() != null) ? (sale.getTotalProceeds().multiply((new BigDecimal(1).subtract(taxRate)))) : new BigDecimal(0)).multiply(changeRate)));
        currentApplication = application;
        currentZone = zone;

        if (monthReportList == null) {
          monthReportList = new ArrayList<ZoneReportSummary>();
        }
      }
      if (applicationSumary != null && monthReportLine != null) {
        monthReportLine.getApplications().add(applicationSumary);
      }
      if (monthReportLine != null && monthReportList != null) {
        monthReportList.add(monthReportLine);
        monthReportLineTotal = appsTotal(monthReportLineTotal, monthReportLine);
      }
      monthReportLineTotal = appsTotal(monthReportLineTotal, monthReportLineTotal);
      monthReportList.add(monthReportLineTotal);
      return monthReportList;
    } else {
      logger.debug("No sales found for period  " + period.getMonth() + "/" + period.getYear());
    }
    monthReportList = new ArrayList<ZoneReportSummary>();
    return monthReportList;
  }

  private ZoneReportSummary appsTotal(ZoneReportSummary monthReportLineTotal, ZoneReportSummary monthReportLine) {
    monthReportLineTotal.setZoneName("Total by App :");
    ApplicationReportSummary total = new ApplicationReportSummary();
    total.init();
    for (ApplicationReportSummary reportSummary : monthReportLine.getApplications()) {
      total.setApplicationName(ZONE_TOTAL_COLUMN);
      if (reportSummary.getOriginalCurrencyAmount() != null) {
        total.setOriginalCurrencyAmount(total.getOriginalCurrencyAmount().setScale(2, RoundingMode.HALF_UP)
            .add(reportSummary.getOriginalCurrencyAmount().setScale(5, RoundingMode.HALF_UP)));
      }
      if (reportSummary.getOriginalCurrencyProceeds() != null) {
        total.setOriginalCurrencyProceeds(total.getOriginalCurrencyProceeds().setScale(2, RoundingMode.HALF_UP)
            .add(reportSummary.getOriginalCurrencyProceeds().setScale(5, RoundingMode.HALF_UP)));
      }
      if (reportSummary.getOriginalCurrencyProceedsAfterTax() != null) {
        total.setOriginalCurrencyProceedsAfterTax(total.getOriginalCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP)
            .add(reportSummary.getOriginalCurrencyProceedsAfterTax().setScale(5, RoundingMode.HALF_UP)));
      }
      total.setReferenceCurrencyAmount(total.getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP)
          .add(reportSummary.getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP)));
      total.setReferenceCurrencyProceeds(total.getReferenceCurrencyProceeds().setScale(5, RoundingMode.HALF_UP)
          .add(reportSummary.getReferenceCurrencyProceeds().setScale(5, RoundingMode.HALF_UP)));
      total.setReferenceCurrencyProceedsAfterTax(total.getReferenceCurrencyProceedsAfterTax().setScale(5, RoundingMode.HALF_UP)
          .add(reportSummary.getReferenceCurrencyProceedsAfterTax().setScale(5, RoundingMode.HALF_UP)));

      total.setReferenceCurrency(reportSummary.getReferenceCurrency());
      total.setOriginalCurrency(reportSummary.getOriginalCurrency());
      total.setSalesNumber(total.getSalesNumber() + reportSummary.getSalesNumber());
      if (monthReportLineTotal != monthReportLine) {// add zone totals to grand total
        boolean appFound = false;
        for (ApplicationReportSummary reportSummaryTotal : monthReportLineTotal.getApplications()) {
          if (reportSummaryTotal.getApplicationName().equals(reportSummary.getApplicationName())) {// we already have data for that application
            appFound = true;
            reportSummaryTotal.setSalesNumber(reportSummaryTotal.getSalesNumber() + reportSummary.getSalesNumber());
            reportSummaryTotal.setReferenceCurrencyAmount(reportSummaryTotal.getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP)
                .add(reportSummary.getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP)));
            reportSummaryTotal.setReferenceCurrencyProceeds(reportSummaryTotal.getReferenceCurrencyProceeds().setScale(5, RoundingMode.HALF_UP)
                .add(reportSummary.getReferenceCurrencyProceeds().setScale(5, RoundingMode.HALF_UP)));
            reportSummaryTotal.setReferenceCurrencyProceedsAfterTax(reportSummaryTotal.getReferenceCurrencyProceedsAfterTax().setScale(5, RoundingMode.HALF_UP)
                .add(reportSummary.getReferenceCurrencyProceedsAfterTax().setScale(5, RoundingMode.HALF_UP)));
            reportSummaryTotal.setReferenceCurrency(reportSummary.getReferenceCurrency());
          }
        }
        if (!appFound) { // application not yet in Grand Total line
          ApplicationReportSummary reportSummaryTotal = new ApplicationReportSummary();
          reportSummaryTotal.setApplicationName(reportSummary.getApplicationName());
          reportSummaryTotal.setSalesNumber(reportSummary.getSalesNumber());
          reportSummaryTotal.setReferenceCurrencyAmount(reportSummary.getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP));
          reportSummaryTotal.setReferenceCurrencyProceeds(reportSummary.getReferenceCurrencyProceeds().setScale(5, RoundingMode.HALF_UP));
          reportSummaryTotal.setReferenceCurrencyProceedsAfterTax(reportSummary.getReferenceCurrencyProceedsAfterTax().setScale(5, RoundingMode.HALF_UP));
          reportSummaryTotal.setReferenceCurrency(reportSummary.getReferenceCurrency());
          monthReportLineTotal.getApplications().add(reportSummaryTotal);
        }
      }
    }

    if (monthReportLine == monthReportLineTotal) { // grand total is sum of rounded zone subtotal
      total.setOriginalCurrencyAmount(null);
      total.setOriginalCurrencyProceeds(null);
      total.setOriginalCurrencyProceedsAfterTax(null);
      total.setReferenceCurrencyAmount(referenceCurrencyAmountGrandTotal);
      total.setReferenceCurrencyProceeds(referenceCurrencyProceedsAmountGrandTotal);
      total.setReferenceCurrencyProceedsAfterTax(referenceCurrencyProceedsAfterTaxGrandTotal);
    } else {
      referenceCurrencyAmountGrandTotal = referenceCurrencyAmountGrandTotal.add(total.getReferenceCurrencyAmount().setScale(2, RoundingMode.HALF_UP));
      referenceCurrencyProceedsAmountGrandTotal = referenceCurrencyProceedsAmountGrandTotal.add(total.getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP));
      referenceCurrencyProceedsAfterTaxGrandTotal = referenceCurrencyProceedsAfterTaxGrandTotal.add(total.getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP));
    }

    monthReportLine.getApplications().add(total);
    return monthReportLineTotal;
  }

  private BigDecimal getTaxRate(List<Tax> taxes, Zone zone) {
    BigDecimal taxRate = new BigDecimal(0);
    for (Tax tax : taxes) {
      if (tax.getZone().equals(zone)) {
        taxRate = tax.getRate();
        break;
      }
    }
    return taxRate;
  }

  private BigDecimal getChangeRate(ArrayList<FXRateDTO> fxRates, Zone zone) throws RuntimeException {
    BigDecimal changeRate = new BigDecimal(0);
    String tmpCurrency = null;
    for (FXRateDTO fxRate : fxRates) {
      if (fxRate.getId() != null && fxRate.getZone().getId() == zone.getId()) {
        changeRate = fxRate.getRate();
        tmpCurrency = fxRate.getCurrencyIso();
        break;
      }
    }
    if (changeRate == null) {
      throw new RuntimeException("No Change Rate found for Zone " + zone.getCode() + " - " + zone.getName() + " for choosen Month");
    }

    if (tmpCurrency == null) {
      throw new RuntimeException("No Reference currency found for Zone " + zone.getCode() + " - " + zone.getName() + " for choosen Month");
    }
    if (referenceCurrency != null && !referenceCurrency.equals(tmpCurrency)) {
      throw new RuntimeException("Multiple reference currency not allowed. Zone : " + zone.getCode() + " - " + zone.getName() + " will change current currency : "
          + referenceCurrency + " to : " + tmpCurrency);
    }
    referenceCurrency = tmpCurrency;
    return changeRate;
  }
}
