package com.tinsys.itc_reporting.client;

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
  public void testRecordNumbers() {
    FiscalPeriodDTO aPeriod = new FiscalPeriodDTO();
    aPeriod.setId(6L);
    aPeriod.setMonth(6);
    aPeriod.setYear(2013);
    List<ZoneReportSummary> result = report.getMonthlyReport(aPeriod);
    Assert.assertEquals(3,result.size());
  }
}
