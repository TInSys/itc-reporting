package com.tinsys.itc_reporting.client;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.util.Assert;

import com.tinsys.itc_reporting.dao.FXrateDAOTest;
import com.tinsys.itc_reporting.dao.RoyaltyDAOTest;
import com.tinsys.itc_reporting.dao.TaxDAOTest;
import com.tinsys.itc_reporting.server.service.RoyaltyReportBuilderImpl;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

public class RoyaltyReportBuilderTest {

  RoyaltyReportBuilderImpl royaltyReport;

  @Test
  public void testInitWithCompany() {
    this.init();
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setName("Company 1");
    company.setCurrencyISO("EUR");
    royaltyReport.init(company);
  }

  @Test
  public void testIsNewLineOKWithOneCall() {
    this.init();
    assertTrue(royaltyReport.isNewLine());
  }

  @Test
  public void emptySalesReturnEmptyArray() {
    this.init();
    CompanyDTO company = new CompanyDTO();
    company.setId(0L);
    company.setName("Company 1");
    company.setCurrencyISO("EUR");
    royaltyReport.init(company);
    royaltyReport.addLine();
    Assert.isTrue(royaltyReport.getReport().size()==0);
  }
  
  private void init() {
    royaltyReport = new RoyaltyReportBuilderImpl();
    FXrateDAOTest fxRateDAOTest = new FXrateDAOTest();
    TaxDAOTest taxDAOTest = new TaxDAOTest();
    RoyaltyDAOTest royaltyDAOTest = new RoyaltyDAOTest();
    royaltyReport.setFxRateDAO(fxRateDAOTest);
    royaltyReport.setRoyaltyDAO(royaltyDAOTest);
    royaltyReport.setTaxDAO(taxDAOTest);
  }

}
