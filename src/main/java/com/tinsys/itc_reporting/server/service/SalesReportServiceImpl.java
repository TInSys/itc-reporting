package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
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
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.Sales;
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
    @Autowired
    @Qualifier("salesDAO")
    private SalesDAO salesDAO;

    @Autowired
    @Qualifier("fxRateDAO")
    private FXRateDAO fxRateDAO;

    @Override
    public List<ZoneReportSummary> getMonthlyReport(FiscalPeriodDTO period) {
       logger.debug("Preparing report");
        List<Sales> sales = salesDAO.getAllSales(period);
        ArrayList<FXRateDTO> fxRates = fxRateDAO.getAllFXRatesForPeriod(period);
        BigDecimal changeRate = new BigDecimal(0);
        String currency = new String();
        Zone currentZone = null;
        Application currentApplication = null;
        List<ZoneReportSummary> monthReportList = null;
        ApplicationReportSummary applicationSumary = null;
        ZoneReportSummary monthReportLine = new ZoneReportSummary();
        monthReportLine
                .setApplications(new ArrayList<ApplicationReportSummary>());
        ZoneReportSummary monthReportLineTotal = new ZoneReportSummary();
        monthReportLineTotal.setApplications(new ArrayList<ApplicationReportSummary>());
        if (sales != null && sales.size() > 0) {
           logger.debug("Processing  "+sales.size()+" lines");
            for (Sales sale : sales) {
                Zone zone = sale.getZone();
                if (zone != currentZone && monthReportList != null) {
                    if (applicationSumary != null) {
                        monthReportLine.getApplications()
                                .add(applicationSumary);
                        applicationSumary = null;
                    }
                    monthReportList.add(monthReportLine);
                    monthReportLineTotal = appsTotal(monthReportLineTotal, monthReportLine);
                    monthReportLine = new ZoneReportSummary();
                    monthReportLine
                            .setApplications(new ArrayList<ApplicationReportSummary>());
                    changeRate = new BigDecimal(0);
                    currency = new String();
                    for (FXRateDTO fxRate : fxRates) {
                        if (fxRate.getId() != null
                                && fxRate.getZone().getId() == zone.getId()) {
                            changeRate = fxRate.getRate();
                            currency = fxRate.getCurrencyIso();
                            break;
                        }
                    }
                } else {
                    if (currentZone == null) {
                        changeRate = new BigDecimal(0);
                        for (FXRateDTO fxRate : fxRates) {
                            if (fxRate.getId() != null
                                    && fxRate.getZone().getId() == zone.getId()) {
                                changeRate = fxRate.getRate();
                                currency = fxRate.getCurrencyIso();
                                break;
                            }
                        }
                    }
                }
                Application application = sale.getApplication();
                if (application != currentApplication
                        && applicationSumary != null) {
                    monthReportLine.getApplications().add(applicationSumary);
                    applicationSumary = new ApplicationReportSummary();
                    applicationSumary.setOriginalCurrencyAmount(new BigDecimal(
                            0));
                    applicationSumary.setOriginalCurrencyProceeds(new BigDecimal(
                            0));
                    applicationSumary
                            .setReferenceCurrencyAmount(new BigDecimal(0));
                } else {
                    if (applicationSumary == null) {
                        applicationSumary = new ApplicationReportSummary();
                        applicationSumary
                                .setOriginalCurrencyAmount(new BigDecimal(0));
                        applicationSumary
                        .setOriginalCurrencyProceeds(new BigDecimal(0));
                        applicationSumary
                                .setReferenceCurrencyAmount(new BigDecimal(0));
                    }
                }
                applicationSumary.setApplicationName(application.getName());
                applicationSumary.setSalesNumber(applicationSumary
                        .getSalesNumber() + sale.getSoldUnits());
                applicationSumary.setOriginalCurrency(sale.getZone()
                        .getCurrencyISO());
                if (applicationSumary.getOriginalCurrencyAmount()==null){
                   applicationSumary.setOriginalCurrencyAmount(new BigDecimal(0));
                }
                applicationSumary.setOriginalCurrencyAmount(applicationSumary
                        .getOriginalCurrencyAmount().add(sale.getTotalPrice()));
                if (applicationSumary.getOriginalCurrencyProceeds()==null){
                    applicationSumary.setOriginalCurrencyProceeds(new BigDecimal(0));
                 }
                 applicationSumary.setOriginalCurrencyProceeds(applicationSumary
                         .getOriginalCurrencyProceeds().add((sale.getTotalProceeds()!=null)?sale.getTotalProceeds():new BigDecimal(0)));
                applicationSumary.setReferenceCurrency(currency);
                if (applicationSumary.getReferenceCurrencyAmount()==null){
                   applicationSumary.setReferenceCurrencyAmount(new BigDecimal(0));
                }
                applicationSumary.setReferenceCurrencyAmount(applicationSumary
                        .getReferenceCurrencyAmount().add(
                                ((sale.getTotalPrice()!=null)?sale.getTotalPrice():new BigDecimal(0)).multiply(changeRate)));

                if (applicationSumary.getReferenceCurrencyProceeds()==null){
                    applicationSumary.setReferenceCurrencyProceeds(new BigDecimal(0));
                 }
                 applicationSumary.setReferenceCurrencyProceeds(applicationSumary
                         .getReferenceCurrencyProceeds().add(
                                 ((sale.getTotalProceeds()!=null)?sale.getTotalProceeds():new BigDecimal(0)).multiply(changeRate)));
                 
                monthReportLine.setZoneName(zone.getName());
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
           logger.debug("No sales found for period  "+period.getMonth()+"/"+period.getYear());

        }
        monthReportList = new ArrayList<ZoneReportSummary>();
        return monthReportList;
    }

    private ZoneReportSummary appsTotal(
            ZoneReportSummary monthReportLineTotal,
            ZoneReportSummary monthReportLine) {
        monthReportLineTotal.setZoneName("Total by App :");
        ApplicationReportSummary total = new  ApplicationReportSummary();
        total.setReferenceCurrencyAmount(new BigDecimal(0));
        total.setReferenceCurrencyProceeds(new BigDecimal(0));
        for ( ApplicationReportSummary reportSummary: monthReportLine.getApplications()) {
            total.setApplicationName(ZONE_TOTAL_COLUMN);
            total.setReferenceCurrencyAmount(total.getReferenceCurrencyAmount().add(reportSummary.getReferenceCurrencyAmount()));
            total.setReferenceCurrencyProceeds(total.getReferenceCurrencyProceeds().add(reportSummary.getReferenceCurrencyProceeds()));
            total.setReferenceCurrency(reportSummary.getReferenceCurrency());
            total.setSalesNumber(total.getSalesNumber()+reportSummary.getSalesNumber());
            boolean appFound = false;
            if (monthReportLineTotal != monthReportLine){
            for (ApplicationReportSummary reportSummaryTotal : monthReportLineTotal.getApplications()){
                if (reportSummaryTotal.getApplicationName().equals(reportSummary.getApplicationName())){
                    appFound = true;
                    reportSummaryTotal.setSalesNumber(reportSummaryTotal.getSalesNumber()+reportSummary.getSalesNumber());
                    reportSummaryTotal.setReferenceCurrencyAmount(reportSummaryTotal.getReferenceCurrencyAmount().add(reportSummary.getReferenceCurrencyAmount()));
                    reportSummaryTotal.setReferenceCurrencyProceeds(reportSummaryTotal.getReferenceCurrencyProceeds().add(reportSummary.getReferenceCurrencyProceeds()));
                }
            }
            if (!appFound){
                ApplicationReportSummary reportSummaryTotal = new ApplicationReportSummary();
                reportSummaryTotal.setApplicationName(reportSummary.getApplicationName());
                reportSummaryTotal.setSalesNumber(reportSummary.getSalesNumber());
                reportSummaryTotal.setReferenceCurrencyAmount(reportSummary.getReferenceCurrencyAmount());
                reportSummaryTotal.setReferenceCurrencyProceeds(reportSummary.getReferenceCurrencyProceeds());
                monthReportLineTotal.getApplications().add(reportSummaryTotal);
            }}
        }
        monthReportLine.getApplications().add(total);
        return monthReportLineTotal;
    }
}
