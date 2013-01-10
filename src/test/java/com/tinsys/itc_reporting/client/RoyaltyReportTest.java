package com.tinsys.itc_reporting.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import com.tinsys.itc_reporting.dao.FXrateDAOTest;
import com.tinsys.itc_reporting.dao.RoyaltyDAOTest;
import com.tinsys.itc_reporting.dao.SalesDAOTest;
import com.tinsys.itc_reporting.dao.TaxDAOTest;
import com.tinsys.itc_reporting.server.service.RoyaltyReportImpl;

public class RoyaltyReportTest {

  RoyaltyReportImpl royaltyReport;

  @Test(expected = NullPointerException.class)
  public void testInit() {
    this.init();
    royaltyReport.init(null);
  }

  @Test
  public void testIsNewLine() {
    this.init();
    assertTrue(royaltyReport.isNewLine());
    }

  @Ignore
  @Test
  public void testAddLine() {
    this.init();
    royaltyReport.addLine();
  }

  @Ignore
  @Test
  public void testResetLine() {
    fail("Not yet implemented");
  }

  @Ignore
  @Test
  public void testSetCurrentData() {
    fail("Not yet implemented");
  }

  private void init() {
    royaltyReport = new RoyaltyReportImpl();
    SalesDAOTest salesDAO = new SalesDAOTest();
    FXrateDAOTest fxRateDAOTest = new FXrateDAOTest();
    TaxDAOTest taxDAOTest = new TaxDAOTest();
    RoyaltyDAOTest royaltyDAOTest = new RoyaltyDAOTest();
    royaltyReport.setFxRateDAO(fxRateDAOTest);

  }

}
