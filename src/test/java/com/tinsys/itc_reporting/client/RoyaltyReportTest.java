package com.tinsys.itc_reporting.client;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinsys.itc_reporting.server.service.RoyaltyReportImpl;

public class RoyaltyReportTest {

  RoyaltyReportImpl royaltyReport;

  @Before
  public void setUp() throws Exception {
    
    royaltyReport = new RoyaltyReportImpl();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test(expected = RuntimeException.class)
  public void testInit() {
    royaltyReport.init(null);
  }

  @Test
  public void testIsNewLine() {
    fail("Not yet implemented");
  }

  @Test
  public void testAddLine() {
    fail("Not yet implemented");
  }

  @Test
  public void testResetLine() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetCurrentData() {
    fail("Not yet implemented");
  }

}
