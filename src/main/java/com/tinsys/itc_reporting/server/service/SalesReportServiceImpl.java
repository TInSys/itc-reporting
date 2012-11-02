package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.SalesReportService;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.PreferencesDAO;
import com.tinsys.itc_reporting.dao.SalesDAO;
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.ApplicationReportSummary;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.MonthReportSummary;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

@Service("salesReportService")
@Transactional
public class SalesReportServiceImpl implements SalesReportService {

    @Autowired
    @Qualifier("salesDAO")
    private SalesDAO salesDAO;

    @Autowired
    @Qualifier("preferencesDAO")
    private PreferencesDAO preferencesDAO;

    @Autowired
    @Qualifier("fxRateDAO")
    private FXRateDAO fxRateDAO;

    @Override
    public List<MonthReportSummary> getMonthlyReport(FiscalPeriodDTO period) {
        PreferencesDTO preferences = preferencesDAO.findPreference(null);
        List<Sales> sales = salesDAO.getAllSales(period);
        ArrayList<FXRateDTO> fxRates = fxRateDAO.getAllFXRatesForPeriod(period);
        BigDecimal changeRate = new BigDecimal(0);
        Zone currentZone = null;
        Application currentApplication = null;
        List<MonthReportSummary> monthReportList = null;
        ApplicationReportSummary applicationSumary = null;
        MonthReportSummary monthReportLine = new MonthReportSummary();
        monthReportLine
                .setApplications(new ArrayList<ApplicationReportSummary>());
        if (sales != null && sales.size() > 0) {
            for (Sales sale : sales) {
                Zone zone = sale.getZone();
                if (zone != currentZone && monthReportList != null) {
                    if (applicationSumary != null) {
                        monthReportLine.getApplications()
                                .add(applicationSumary);
                        applicationSumary = null;
                    }
                    monthReportList.add(monthReportLine);
                    monthReportLine = new MonthReportSummary();
                    monthReportLine
                            .setApplications(new ArrayList<ApplicationReportSummary>());
                    changeRate = new BigDecimal(0);
                    for (FXRateDTO fxRate : fxRates) {
                        if (fxRate.getId() != null
                                && fxRate.getZone().getId() == zone.getId()) {
                            changeRate = fxRate.getRate();
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
                    applicationSumary
                            .setReferenceCurrencyAmount(new BigDecimal(0));
                } else {
                    if (applicationSumary == null) {
                        applicationSumary = new ApplicationReportSummary();
                        applicationSumary
                                .setOriginalCurrencyAmount(new BigDecimal(0));
                        applicationSumary
                                .setReferenceCurrencyAmount(new BigDecimal(0));
                    }
                }
                applicationSumary.setApplicationName(application.getName());
                applicationSumary.setSalesNumber(applicationSumary
                        .getSalesNumber() + sale.getSoldUnits());
                applicationSumary.setOriginalCurrency(sale.getZone()
                        .getCurrencyISO());
                applicationSumary.setOriginalCurrencyAmount(applicationSumary
                        .getOriginalCurrencyAmount().add(sale.getTotalPrice()));
                applicationSumary.setReferenceCurrency(preferences
                        .getReferenceCurrency());
                applicationSumary.setReferenceCurrencyAmount(applicationSumary
                        .getReferenceCurrencyAmount().add(
                                sale.getTotalPrice().multiply(changeRate)));
                monthReportLine.setZoneName(zone.getName());
                currentApplication = application;
                currentZone = zone;

                if (monthReportList == null) {
                    monthReportList = new ArrayList<MonthReportSummary>();
                }

            }
            if (applicationSumary != null && monthReportLine != null) {
                monthReportLine.getApplications().add(applicationSumary);
            }
            if (monthReportLine != null && monthReportList != null) {
                monthReportList.add(monthReportLine);
            }
            return monthReportList;
        }
        monthReportList = new ArrayList<MonthReportSummary>();
        return monthReportList;
    }
}
