package com.tinsys.itc_reporting.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tinsys.itc_reporting.dao.FXrateDAOTest;
import com.tinsys.itc_reporting.dao.SalesDAOTest;
import com.tinsys.itc_reporting.dao.TaxDAOTest;
import com.tinsys.itc_reporting.server.service.SalesReportServiceImpl;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneReportSummary;

public class SalesReportServiceTest {

  private SalesReportServiceImpl report;
  @Before
  public void setUp() throws Exception {
    report = new SalesReportServiceImpl();
    report.setFxRateDAO(new FXrateDAOTest());
    report.setTaxDAO(new TaxDAOTest());
  }
  
 
  @Test
  public void testOneZone(){
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(0);
    report.setSalesDAO(salesDAO);
    FiscalPeriodDTO aPeriod = new FiscalPeriodDTO();
    aPeriod.setId(5L);
    aPeriod.setMonth(5);
    aPeriod.setYear(2013);
    List<ZoneReportSummary> result = report.getMonthlyReport(aPeriod);
    Assert.assertEquals(2,result.size());
    ZoneReportSummary total = result.get(1);
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyAmount());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceeds());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceedsAfterTax());
    
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyAmount().setScale(2, RoundingMode.HALF_UP).compareTo(total.getApplications().get(1).getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP).compareTo(total.getApplications().get(1).getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP).compareTo(total.getApplications().get(1).getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP)) == 0);

  }
  
    @Test
  public void testMultipleZones(){
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(1);
    report.setSalesDAO(salesDAO);
    FiscalPeriodDTO aPeriod = new FiscalPeriodDTO();
    aPeriod.setId(9L);
    aPeriod.setMonth(9);
    aPeriod.setYear(2013);
    List<ZoneReportSummary> result = report.getMonthlyReport(aPeriod);
    Assert.assertEquals(3,result.size());
    ZoneReportSummary total = result.get(2);
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyAmount());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceeds());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceedsAfterTax());
    
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyAmount().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(80).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(64).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(64).setScale(2, RoundingMode.HALF_UP)) == 0);

  }
  
  @Test
  public void testMultipleZonesWithFxRate(){
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(0);
    report.setSalesDAO(salesDAO);

    FiscalPeriodDTO aPeriod = new FiscalPeriodDTO();
    aPeriod.setId(6L);
    aPeriod.setMonth(6);
    aPeriod.setYear(2013);
    List<ZoneReportSummary> result = report.getMonthlyReport(aPeriod);
    Assert.assertEquals(3,result.size());
    ZoneReportSummary total = result.get(2);
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyAmount());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceeds());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceedsAfterTax());
    
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyAmount().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(44.98080).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(35.98464).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(35.98464).setScale(2, RoundingMode.HALF_UP)) == 0);
  }

  @Test
  public void testMultipleZonesWithTaxRateAndFXRate(){
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(2);
    report.setSalesDAO(salesDAO);
    FiscalPeriodDTO aPeriod = new FiscalPeriodDTO();
    aPeriod.setId(10L);
    aPeriod.setMonth(10);
    aPeriod.setYear(2013);
    List<ZoneReportSummary> result = report.getMonthlyReport(aPeriod);
    Assert.assertEquals(3,result.size());
    ZoneReportSummary total = result.get(2);
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyAmount());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceeds());
    Assert.assertNull(total.getApplications().get(0).getOriginalCurrencyProceedsAfterTax());
    
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyAmount().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(88).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(70.40).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(62.336).setScale(2, RoundingMode.HALF_UP)) == 0);
 
  }

}
