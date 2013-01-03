package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.RoyaltyReportService;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.RoyaltyDAO;
import com.tinsys.itc_reporting.dao.SalesDAO;
import com.tinsys.itc_reporting.dao.TaxDAO;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("royaltyReportService")
@Transactional
public class RoyaltyReportServiceImpl implements RoyaltyReportService {

  private static final Logger logger = Logger.getLogger(RoyaltyReportServiceImpl.class);

  @Autowired
  @Qualifier("salesDAO")
  private SalesDAO salesDAO;

  @Autowired
  @Qualifier("fxRateDAO")
  private FXRateDAO fxRateDAO;

  @Autowired
  @Qualifier("taxDAO")
  private TaxDAO taxDAO;

  @Autowired
  @Qualifier("royaltyDAO")
  private RoyaltyDAO royaltyDAO;

  private Comparator<Sales> compareSales = new Comparator<Sales>() {

    @Override
    public int compare(Sales o1, Sales o2) {
      int c;
      c = Integer.valueOf(o1.getPeriod().getYear()).compareTo(Integer.valueOf(o2.getPeriod().getYear()));
      if (c == 0) {
        c = Integer.valueOf(o1.getPeriod().getMonth()).compareTo(Integer.valueOf(o2.getPeriod().getMonth()));
      }
      if (c == 0) {
        c = o1.getApplication().getVendorID().compareTo(o2.getApplication().getVendorID());
      }
      if (c == 0) {
        c = o1.getZone().getCode().compareTo(o2.getZone().getCode());
      }

      return c;
    }
  };

  @Override
  public List<RoyaltyReportLine> getCompanyReport(CompanyDTO company, FiscalPeriodDTO startPeriod, FiscalPeriodDTO endPeriod) {
    logger.debug("Preparing report");
    List<RoyaltyDTO> royalties = royaltyDAO.getAllRoyalty(company);
    List<Sales> sales = salesDAO.getAllSales(startPeriod, endPeriod, royalties);
    List<Tax> taxes = null;
    ArrayList<FXRateDTO> fxRates = null;
    String currency = new String();
    BigDecimal changeRate = new BigDecimal(0);
    BigDecimal taxRate = new BigDecimal(0);
    BigDecimal shareRate = new BigDecimal(0);
    boolean royaltyOnSales = false;
    Collections.sort(sales, compareSales);
    List<SalesDTO> salesDTOList = new ArrayList<SalesDTO>();
    FiscalPeriodDTO currentPeriod = null;
    ApplicationDTO currentApplication = null;
    ZoneDTO currentZone = null;
    BigDecimal currentPrice = new BigDecimal(0);
    for (Sales sale : sales) {
      salesDTOList.add(DTOUtils.salesToSalesDTO(sale));
    }
    List<RoyaltyReportLine> report = new ArrayList<RoyaltyReportLine>();
    RoyaltyReportLine reportLine = new RoyaltyReportLine();
    BigDecimal vatFactor;

    for (SalesDTO salesDTO : salesDTOList) {
      if (salesDTO.getTotalPrice().compareTo(new BigDecimal(0)) != 0) {
        if (currentPeriod == null || currentPeriod.getId() != salesDTO.getPeriod().getId() || currentApplication.getId() != salesDTO.getApplication().getId()
            || currentZone.getId() != salesDTO.getZone().getId() || !currentPrice.equals(salesDTO.getIndividualPrice())) {
          if (currentPeriod != null) {
            logger.debug("period " + currentPeriod.getId() + " " + salesDTO.getPeriod().getId() + " " + (currentPeriod.getId() != salesDTO.getPeriod().getId()));
            logger.debug("app " + currentApplication + " " + salesDTO.getApplication() + " " + (currentApplication != salesDTO.getApplication()));
            logger.debug("zone " + currentZone + " " + salesDTO.getZone() + " " + (currentZone != salesDTO.getZone()));
            logger.debug("price " + currentPrice + " " + salesDTO.getIndividualPrice() + " " + (!currentPrice.equals(salesDTO.getIndividualPrice())));
          }
          // add royalty line to table
          if (currentPeriod != null) {
            reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().multiply(changeRate));
            reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(reportLine.getReferenceCurrencyProceedsAfterTaxTotalAmount().multiply(changeRate));
            reportLine.setReferenceCurrencyTotalAmount(reportLine.getReferenceCurrencyTotalAmount().multiply(changeRate));
            report.add(reportLine);
          }
          // reset royalty line

          reportLine = new RoyaltyReportLine();
          reportLine.setOriginalCurrencyAmount(new BigDecimal(0));
          reportLine.setOriginalCurrencyTotalAmount(new BigDecimal(0));
          reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(new BigDecimal(0));
          reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(new BigDecimal(0));
          reportLine.setReferenceCurrencyTotalAmount(new BigDecimal(0));
          if (currentPeriod == null || !currentPeriod.equals(salesDTO.getPeriod())) {
            currentPeriod = salesDTO.getPeriod();
            taxes = taxDAO.getTaxesForPeriod(currentPeriod);
            fxRates = fxRateDAO.getAllFXRatesForPeriod(currentPeriod);
          }
          currentApplication = salesDTO.getApplication();
          if (currentZone != salesDTO.getZone() || currentZone == null) {
            currentZone = salesDTO.getZone();
            for (Tax tax : taxes) {
              if (tax.getZone().equals(currentZone)) {
                taxRate = tax.getRate();
                break;
              }
            }
          }
          currentPrice = salesDTO.getIndividualPrice();

          reportLine.setPeriod(currentPeriod);
          reportLine.setApplication(currentApplication);
          reportLine.setZone(currentZone);
          reportLine.setOriginalCurrencyAmount(currentPrice);
          currency = new String();
          changeRate = new BigDecimal(0);
          for (FXRateDTO fxRate : fxRates) {
            if (fxRate.getId() != null && fxRate.getZone().getId() == currentZone.getId()) {
              changeRate = fxRate.getRate();
              currency = fxRate.getCurrencyIso();
              break;
            }
          }
          taxRate = new BigDecimal(0);
          for (Tax tax : taxes) {
            if (tax.getId() != null && tax.getZone().getId().equals(currentZone.getId())) {
              taxRate = tax.getRate();
              break;
            }
          }

          for (RoyaltyDTO royalty : royalties) {
            if (royalty.getApplication().getId().equals(currentApplication.getId()) && royalty.getZones().contains(currentZone)) {
              shareRate = royalty.getShareRate();
              royaltyOnSales = royalty.getShareRateCalculationField().equals("S");
            }
          }
        }

        reportLine.setOriginalCurrency(currentZone.getCurrencyISO());
        reportLine.setReferenceCurrency(currency);
        reportLine.setSalesNumber(reportLine.getSalesNumber() + salesDTO.getSoldUnits());
        reportLine.setOriginalCurrencyTotalAmount(reportLine.getOriginalCurrencyTotalAmount().add(salesDTO.getTotalPrice()));
        BigDecimal proceedsAfterTax = ((salesDTO.getTotalProceeds() != null) ? (salesDTO.getTotalProceeds().multiply((new BigDecimal(1).subtract(taxRate)))) : new BigDecimal(0));
        BigDecimal totalAmount = ((salesDTO.getTotalPrice() != null) ? salesDTO.getTotalPrice() : new BigDecimal(0));
        reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(reportLine.getReferenceCurrencyProceedsAfterTaxTotalAmount().add(proceedsAfterTax));
        reportLine.setReferenceCurrencyTotalAmount(reportLine.getReferenceCurrencyTotalAmount().add(totalAmount));
        if (shareRate.compareTo(new BigDecimal(0)) != 0 && proceedsAfterTax.compareTo(new BigDecimal(0)) != 0) {
          vatFactor = new BigDecimal(0);
          if (changeRate.equals(new BigDecimal(0))) {
            throw new RuntimeException("Change Rate not found for " + currentZone.getName() + " for period " + currentPeriod);
          }
          try {
            if (royaltyOnSales) {
              vatFactor = (totalAmount.multiply(new BigDecimal(0.7))).divide((proceedsAfterTax), 2, RoundingMode.HALF_UP);
              reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().add(
                  ((salesDTO.getTotalPrice().divide(vatFactor, 5, RoundingMode.HALF_UP)).multiply(shareRate.divide(new BigDecimal(100), 5, RoundingMode.HALF_UP)))));
              logger.debug("VAT factor : " + vatFactor + "  Royalties : " + reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount() + "   change Rate :" + changeRate
                  + " total sales(tot price) :" + salesDTO.getTotalPrice());
            } else {
              reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().add(
                  (proceedsAfterTax.multiply(shareRate).divide(new BigDecimal(100), 5, RoundingMode.HALF_UP))));
            }
          } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
          }
        }
      }
    }
    if (reportLine != null && reportLine.getPeriod() != null) {
      reportLine.setReferenceCurrencyCompanyRoyaltiesTotalAmount(reportLine.getReferenceCurrencyCompanyRoyaltiesTotalAmount().multiply(changeRate));
      reportLine.setReferenceCurrencyProceedsAfterTaxTotalAmount(reportLine.getReferenceCurrencyProceedsAfterTaxTotalAmount().multiply(changeRate));
      reportLine.setReferenceCurrencyTotalAmount(reportLine.getReferenceCurrencyTotalAmount().multiply(changeRate));
      report.add(reportLine);
    }
    return report;
  }

}
