package com.tinsys.itc_reporting.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.tinsys.itc_reporting.dao.FXrateDAOTest;
import com.tinsys.itc_reporting.dao.RoyaltyDAO;
import com.tinsys.itc_reporting.dao.RoyaltyDAOTest;
import com.tinsys.itc_reporting.dao.SalesDAOTest;
import com.tinsys.itc_reporting.dao.TaxDAOTest;
import com.tinsys.itc_reporting.server.service.RoyaltyReportBuilderImpl;
import com.tinsys.itc_reporting.server.service.RoyaltyReportServiceImpl;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;

public class RoyaltyReportServiceImplTest {

  private RoyaltyReportServiceImpl royaltyReportService;

  @Before
  public void setUp() throws Exception {
    royaltyReportService = new RoyaltyReportServiceImpl();
    RoyaltyDAO royaltyDAO = new RoyaltyDAOTest();
    royaltyReportService.setRoyaltyDAO(royaltyDAO);
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(0);
    royaltyReportService.setSalesDAO(salesDAO);
    RoyaltyReportBuilderImpl royaltyReportBuilder = new RoyaltyReportBuilderImpl();
    royaltyReportService.setRoyaltyReportBuilder(royaltyReportBuilder);
    royaltyReportBuilder.setRoyaltyDAO(new RoyaltyDAOTest());
    royaltyReportBuilder.setFxRateDAO(new FXrateDAOTest());
    royaltyReportBuilder.setTaxDAO(new TaxDAOTest());
  }

  @Test
  public void testGetCompanyReportReturnsNullIfArgsAreNull() {
    Assert.assertNull(royaltyReportService.getCompanyReport(null, null, null));
  }

  @Test
  public void testGetCompanyReportReturnsNullIfNoSalesFound() {
    Assert.assertNull(royaltyReportService.getCompanyReport(new CompanyDTO(), new FiscalPeriodDTO(), new FiscalPeriodDTO()));
  }

  @Test
  public void testGetCompanyReportReturnsResultIfSalesFound() {
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setCurrencyISO("EUR");
    company.setName("Company 1");

    FiscalPeriodDTO startPeriod = new FiscalPeriodDTO();
    startPeriod.setId(0L);
    startPeriod.setMonth(1);
    startPeriod.setYear(2013);

    FiscalPeriodDTO endPeriod = new FiscalPeriodDTO();
    endPeriod.setId(1L);
    endPeriod.setMonth(2);
    endPeriod.setYear(2013);
    List<RoyaltyReportLine> test = royaltyReportService.getCompanyReport(company, startPeriod, endPeriod);
    Assert.assertNotNull(test);
  }

  @Test(expected = RuntimeException.class)
  public void testGetCompanyReportReturnsExceptionIfChangeRateNull() {
    
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setCurrencyISO("EUR");
    company.setName("Company 1");

    FiscalPeriodDTO startPeriod = new FiscalPeriodDTO();
    startPeriod.setId(0L);
    startPeriod.setMonth(3);
    startPeriod.setYear(2013);

    FiscalPeriodDTO endPeriod = new FiscalPeriodDTO();
    endPeriod.setId(1L);
    endPeriod.setMonth(3);
    endPeriod.setYear(2013);
    royaltyReportService.getCompanyReport(company, startPeriod, endPeriod);
  }
  
  @Test
  public void testGetCompanyReportReturnsNullIfNoRoyaltiesDefined() {
    CompanyDTO company = new CompanyDTO();
    company.setId(3L);
    company.setCurrencyISO("EUR");
    company.setName("Company 2");

    FiscalPeriodDTO startPeriod = new FiscalPeriodDTO();
    startPeriod.setId(0L);
    startPeriod.setMonth(4);
    startPeriod.setYear(2013);

    FiscalPeriodDTO endPeriod = new FiscalPeriodDTO();
    endPeriod.setId(1L);
    endPeriod.setMonth(4);
    endPeriod.setYear(2013);
    Assert.assertNull(royaltyReportService.getCompanyReport(company, startPeriod, endPeriod));
  }
  
  @Test
  public void testGetCompanyReportTaxCalculation() {
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setCurrencyISO("EUR");
    company.setName("Company 1");

    FiscalPeriodDTO startPeriod = new FiscalPeriodDTO();
    startPeriod.setId(0L);
    startPeriod.setMonth(4);
    startPeriod.setYear(2013);

    FiscalPeriodDTO endPeriod = new FiscalPeriodDTO();
    endPeriod.setId(1L);
    endPeriod.setMonth(4);
    endPeriod.setYear(2013);
    BigDecimal expected = new BigDecimal(0.64).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(expected, royaltyReportService.getCompanyReport(company, startPeriod, endPeriod).get(0).getReferenceCurrencyCompanyRoyaltiesTotalAmount().setScale(2, RoundingMode.HALF_UP));
  }
  
  @Test
  public void testGetCompanyReportDistinctLinesForDistinctZones() {
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setCurrencyISO("EUR");
    company.setName("Company 1");

    FiscalPeriodDTO startPeriod = new FiscalPeriodDTO();
    startPeriod.setId(0L);
    startPeriod.setMonth(6);
    startPeriod.setYear(2013);

    FiscalPeriodDTO endPeriod = new FiscalPeriodDTO();
    endPeriod.setId(1L);
    endPeriod.setMonth(6);
    endPeriod.setYear(2013);
    int expected = 2;
    Assert.assertEquals(expected, royaltyReportService.getCompanyReport(company, startPeriod, endPeriod).size());
  }
  
  @Test
  public void testGetCompanyReportDistinctLinesForDistinctPeriods() {
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setCurrencyISO("EUR");
    company.setName("Company 1");

    FiscalPeriodDTO startPeriod = new FiscalPeriodDTO();
    startPeriod.setId(0L);
    startPeriod.setMonth(7);
    startPeriod.setYear(2013);

    FiscalPeriodDTO endPeriod = new FiscalPeriodDTO();
    endPeriod.setId(1L);
    endPeriod.setMonth(8);
    endPeriod.setYear(2013);
    int expected = 2;
    Assert.assertEquals(expected, royaltyReportService.getCompanyReport(company, startPeriod, endPeriod).size());
  }
 }
