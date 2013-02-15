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
    report.setSalesDAO(new SalesDAOTest());
    report.setTaxDAO(new TaxDAOTest());
  }
  
  
  @Test
  public void testOneZone(){
    
  }
  
  
  //for each of following test, add the same and different apps for the different Zones
  @Test
  public void testMultipleZones(){
    
  }
  
  @Test
  public void testMultipleZonesWithFxRate(){
    
  }

  @Test
  public void testMultipleZonesWithTaxRate(){
    
  }

  @Test
  public void testMultipleZonesWithTaxRateAndFXRate(){
    
  }
  
  @Test
  public void testRecordNumbersAndTotal() {
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
    
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyAmount().setScale(5, RoundingMode.HALF_UP).compareTo(new BigDecimal(44.98080).setScale(5, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceeds().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(35.98464).setScale(2, RoundingMode.HALF_UP)) == 0);
    Assert.assertTrue(total.getApplications().get(0).getReferenceCurrencyProceedsAfterTax().setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(35.98464).setScale(2, RoundingMode.HALF_UP)) == 0);
  }
  

}
