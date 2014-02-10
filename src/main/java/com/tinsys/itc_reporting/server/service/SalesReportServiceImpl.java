package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.tinsys.itc_reporting.client.service.SalesReportService;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.SalesDAO;
import com.tinsys.itc_reporting.dao.TaxDAO;
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
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

  public void setSalesDAO(SalesDAO salesDAO) {
    this.salesDAO = salesDAO;
  }

  public void setFxRateDAO(FXRateDAO fxRateDAO) {
    this.fxRateDAO = fxRateDAO;
  }

  public void setTaxDAO(TaxDAO taxDAO) {
    this.taxDAO = taxDAO;
  }
  
  
  @Override
  public List<ZoneReportSummary> getMonthlyReport(FiscalPeriodDTO startPeriod,FiscalPeriodDTO endPeriod) throws RuntimeException {
    logger.debug("Preparing report");
    referenceCurrencyAmountGrandTotal = new BigDecimal(0);
    referenceCurrencyProceedsAmountGrandTotal = new BigDecimal(0);
    referenceCurrencyProceedsAfterTaxGrandTotal = new BigDecimal(0);
    
    List<Sales> sales = new ArrayList<Sales>();
    List<Sales> tmpSales = new ArrayList<Sales>();
    List<Tax> taxes = new ArrayList<Tax>();
    ArrayList<FXRateDTO> fxRates = new ArrayList<FXRateDTO>();
    
    if (startPeriod.getYear()==endPeriod.getYear()) {//periods are in same year
      FiscalPeriodDTO period = new FiscalPeriodDTO();
      period.setYear(startPeriod.getYear());
      for (int i = startPeriod.getMonth(); i <= endPeriod.getMonth(); i++) {
        period.setMonth(i);
        tmpSales.addAll(salesDAO.getAllSales(period));
        taxes.addAll(taxDAO.getTaxesForPeriod(period));
        fxRates.addAll(fxRateDAO.getAllFXRatesForPeriod(period));      
      }
    } else { // periods are split on several years
      FiscalPeriodDTO period = new FiscalPeriodDTO();
      for (int i = startPeriod.getYear(); i <= endPeriod.getYear(); i++) {
        period.setYear(i);
        if (i==startPeriod.getYear()) {
          for (int j = startPeriod.getMonth(); j <= 12; j++) {
            period.setMonth(j);
            tmpSales.addAll(salesDAO.getAllSales(period));
            taxes.addAll(taxDAO.getTaxesForPeriod(period));
            fxRates.addAll(fxRateDAO.getAllFXRatesForPeriod(period));
          }
          
        } else if (i==endPeriod.getYear()) {
          for (int j = 1; j <= endPeriod.getMonth(); j++) {
            period.setMonth(j);
            tmpSales.addAll(salesDAO.getAllSales(period));
            taxes.addAll(taxDAO.getTaxesForPeriod(period));
            fxRates.addAll(fxRateDAO.getAllFXRatesForPeriod(period));
          }

        } else {
          for (int j = 1; j <= 12; j++) {
            period.setMonth(j);
            tmpSales.addAll(salesDAO.getAllSales(period));
            taxes.addAll(taxDAO.getTaxesForPeriod(period));
            fxRates.addAll(fxRateDAO.getAllFXRatesForPeriod(period));
          }
        }
      }
      
    }

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
    FiscalPeriod currentPeriod;
    if (tmpSales != null && tmpSales.size() > 0) {
      logger.debug("Processing  " + sales.size() + " lines");
      Collections.sort(tmpSales, new Comparator<Sales>() {

        @Override
        public int compare(Sales o1, Sales o2) {
          int c;
          c = o1.getZone().getCode().compareTo(o2.getZone().getCode());
          if (c==0) {
            c= o1.getApplication().getName().compareTo(o2.getApplication().getName());
          }
          if (c==0) {
            c= o1.getCountryCode().compareTo(o2.getCountryCode());
          }
          
          return c;
        }
      });

      for (Sales sale : tmpSales) {
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
          changeRate = this.getChangeRate(fxRates, zone, sale.getPeriod());
          taxRate = this.getTaxRate(taxes, zone, sale.getPeriod());
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
          changeRate = this.getChangeRate(fxRates, zone, sale.getPeriod());
          taxRate = this.getTaxRate(taxes, zone, sale.getPeriod());
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
      logger.debug("No sales found for periods  " + startPeriod.getMonth() + "/" + startPeriod.getYear()+" to "+endPeriod.getMonth() + "/" + endPeriod.getYear());
    }
    monthReportList = new ArrayList<ZoneReportSummary>();
    return monthReportList;
  }
  
  
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
          changeRate = this.getChangeRate(fxRates, zone, null);
          taxRate = this.getTaxRate(taxes, zone, null);
        } else {
          if (currentZone == null) { // first pass, fetch change rate and
                                     // taxRate
            changeRate = this.getChangeRate(fxRates, zone, null);
            taxRate = this.getTaxRate(taxes, zone, null);
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

  private BigDecimal getTaxRate(List<Tax> taxes, Zone zone, FiscalPeriod fiscalPeriod) {
      BigDecimal taxRate = new BigDecimal(0);
      Date startDate = null;
      Date endDate = null;
      if (fiscalPeriod != null) {
        Calendar cal1 = new GregorianCalendar();
        cal1.set(Calendar.YEAR, fiscalPeriod.getYear());
        cal1.set(Calendar.MONTH, fiscalPeriod.getMonth() - 1);
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        startDate = cal1.getTime();
        Date endOfMonthDate = cal1.getTime();
        CalendarUtil.addMonthsToDate(endOfMonthDate, 1);
        CalendarUtil.addDaysToDate(endOfMonthDate, -1);
        endDate = endOfMonthDate;
      }
      for (Tax tax : taxes) {
        if (tax.getZone().equals(zone)) {
          if (fiscalPeriod !=null) {
            if (tax.getPeriod().getStartDate().getTime() <=startDate.getTime() && (tax.getPeriod().getStopDate() == null || tax.getPeriod().getStopDate().getTime() >=endDate.getTime())) {
              taxRate = tax.getRate();
              break;
            }
          } else {
            taxRate = tax.getRate(); 
            
            break;
          }
        }
      }
      return taxRate;
  }

  private BigDecimal getChangeRate(ArrayList<FXRateDTO> fxRates, Zone zone, FiscalPeriod fiscalPeriod) throws RuntimeException {
    BigDecimal changeRate = new BigDecimal(0);
    String tmpCurrency = null;
    for (FXRateDTO fxRate : fxRates) {
      if (fxRate.getId() != null && fxRate.getZone().getId() == zone.getId()) {
        if (fiscalPeriod !=null) {
          if (fxRate.getPeriod().equals(DTOUtils.periodToPeriodDTO(fiscalPeriod))) {
            changeRate = fxRate.getRate();
            tmpCurrency = fxRate.getCurrencyIso();
            break;
            }
          } else {
          changeRate = fxRate.getRate();
          tmpCurrency = fxRate.getCurrencyIso();
          break;
          }
        }
    }
    
    if (changeRate == null) {
      throw new RuntimeException("No Change Rate found for Zone " + zone.getCode() + " - " + zone.getName() + " for "+((fiscalPeriod!=null)?fiscalPeriod.getMonth()+"/"+fiscalPeriod.getYear():" choosen month"));
    }

    if (tmpCurrency == null) {
      throw new RuntimeException("No Reference currency found for Zone " + zone.getCode() + " - " + zone.getName() + " for "+((fiscalPeriod!=null)?fiscalPeriod.getMonth()+"/"+fiscalPeriod.getYear():" choosen month"));
    }
    
    if (referenceCurrency != null && !referenceCurrency.equals(tmpCurrency)) {
      throw new RuntimeException("Multiple reference currency not allowed. Zone : " + zone.getCode() + " - " + zone.getName() + " will change current currency : "
          + referenceCurrency + " to : " + tmpCurrency);
    }
    referenceCurrency = tmpCurrency;
    return changeRate;
  }
}
