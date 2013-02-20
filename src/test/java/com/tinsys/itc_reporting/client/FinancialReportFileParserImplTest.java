package com.tinsys.itc_reporting.client;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.tinsys.itc_reporting.dao.ApplicationDAOTest;
import com.tinsys.itc_reporting.dao.FiscalPeriodDAOTest;
import com.tinsys.itc_reporting.dao.SalesDAOTest;
import com.tinsys.itc_reporting.dao.ZoneDAOTest;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.server.service.SaleService;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.server.utils.FinancialReportFileParserImpl;
import com.tinsys.itc_reporting.utils.MockFileItem;

public class FinancialReportFileParserImplTest {

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testParsePeriodNotOkGivesError() {
    FileItem fileItem = getFileItem("unparsablePeriod.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(), 1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("could not retrieve period in file name"));
  }

  @Test
  public void testParsePeriodOkButNotNumericGivesError() {
    FileItem fileItem = getFileItem("parsableperiod_0a0b.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(), 1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("Period can't be parsed in the file name"));
  }

  @Test
  public void testParsePeriodOkButNotBeeingAPeriodGivesError() {
    FileItem fileItem = getFileItem("parsableperiod_1322.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(), 1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("Period can't be parsed in the file name"));
  }

  @Test
  public void testParsePeriodOKButZoneGivesError() {
    FileItem fileItem = getFileItem("unparsableZone_0102.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(), 1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("zone code can't be parsed in the file name"));
  }

  @Test
  public void testParseFileNameWithoutExtensionGivesError() {
    FileItem fileItem = getFileItem("unparsableZone_0102_XX");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(), 1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("should have an extension"));
  }

  @Test
  public void testUnsupportedMimetypeGivesError() throws IOException {
    FileItem fileItem = getFileItem("unsupportedMIMEType_0102_EU.png");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.setPeriodDAO(new FiscalPeriodDAOTest());
    saleService.setZoneDAO(new ZoneDAOTest());
    fileParser.setSaleService(saleService);
    fileParser.parseContent();
    Assert.assertEquals(1, fileParser.getErrorList().size());
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("file MIME type"));
  }

  @Test
  public void testUnKnownZoneGivesError() throws IOException {
    FileItem fileItem = getFileItem("unknownZone_0102_XX.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.setPeriodDAO(new FiscalPeriodDAOTest());
    saleService.setZoneDAO(new ZoneDAOTest());
    fileParser.setSaleService(saleService);
    fileParser.parseContent();
    Assert.assertEquals(1, fileParser.getErrorList().size());
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("No corresponding Zone found in database"));
  }

  @Test
  public void testEmptyFile() throws IOException {
    FileItem fileItem = getFileItem("emptyFile_0212_EU.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.setPeriodDAO(new FiscalPeriodDAOTest());
    saleService.setZoneDAO(new ZoneDAOTest());
    fileParser.setSaleService(saleService);
    Assert.assertEquals(false, fileParser.parseContent());
    Assert.assertEquals(0, fileParser.getErrorList().size());
  }

  @Test
  public void testMissingVendorIdentifierHeaderInFileGivesError() throws IOException {
    FileItem fileItem = getFileItem("missingVendorIdentifier_0212_EU.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.setPeriodDAO(new FiscalPeriodDAOTest());
    saleService.setZoneDAO(new ZoneDAOTest());
    fileParser.setSaleService(saleService);
    Assert.assertEquals(true, fileParser.parseContent());
    Assert.assertEquals(1, fileParser.getErrorList().size());
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("No 'Vendor Identifier' header found"));
  }

  @Test
  public void testMissingQuantityLabelHeader() throws IOException {
    FileItem fileItem = getFileItem("missingQuantityLabel_0212_EU.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.setPeriodDAO(new FiscalPeriodDAOTest());
    saleService.setZoneDAO(new ZoneDAOTest());
    fileParser.setSaleService(saleService);
    Assert.assertEquals(false, fileParser.parseContent());
  }

  @Test
  public void testUnknowApplicationInFileGivesError() throws IOException {
    FileItem fileItem = getFileItem("unknownApplication_0212_EU.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.setPeriodDAO(new FiscalPeriodDAOTest());
    saleService.setZoneDAO(new ZoneDAOTest());
    saleService.setApplicationDAO(new ApplicationDAOTest());
    fileParser.setSaleService(saleService);
    Assert.assertEquals(true, fileParser.parseContent());
    Assert.assertEquals(2, fileParser.getErrorList().size());
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("No corresponding Application found"));
  }

  @Test
  public void testDataOK() throws IOException {
    FileItem fileItem = getFileItem("dataOK_0212_EU.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.reset();
    FiscalPeriodDAOTest fiscalPeriodDAO = new FiscalPeriodDAOTest();
    saleService.setPeriodDAO(fiscalPeriodDAO);
    saleService.setZoneDAO(new ZoneDAOTest());
    saleService.setApplicationDAO(new ApplicationDAOTest());
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(0);
    saleService.setSaleDAO(salesDAO);
    fileParser.setSaleService(saleService);

    FiscalPeriod aPeriod = new FiscalPeriod();
    aPeriod.setId(0L);
    aPeriod.setMonth(2);
    aPeriod.setYear(2012);
    Assert.assertEquals(true, fileParser.parseContent());
    Assert.assertEquals(0, fileParser.getErrorList().size());
    saleService.saveOrUpdate();
    Assert.assertEquals(2, salesDAO.getAllSales(DTOUtils.periodToPeriodDTO(fiscalPeriodDAO.findPeriod(aPeriod))).size());
  }
  
  // check summarization 
  @Test
  public void testSummarizationOK() throws IOException {
    FileItem fileItem = getFileItem("dataOK_0212_EU.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    SaleService saleService = new SaleService();
    saleService.reset();
    FiscalPeriodDAOTest fiscalPeriodDAO = new FiscalPeriodDAOTest();
    saleService.setPeriodDAO(fiscalPeriodDAO);
    saleService.setZoneDAO(new ZoneDAOTest());
    saleService.setApplicationDAO(new ApplicationDAOTest());
    SalesDAOTest salesDAO = new SalesDAOTest();
    salesDAO.createSales(0);
    saleService.setSaleDAO(salesDAO);
    fileParser.setSaleService(saleService);
    Assert.assertEquals(true, fileParser.parseContent());
    saleService.saveOrUpdate();

    fileItem = getFileItem("dataSummarizationOK_0212_EU.txt");
    fileParser = new FinancialReportFileParserImpl(fileItem);
    fileParser.setSaleService(saleService);
    FiscalPeriod aPeriod = new FiscalPeriod();
    aPeriod.setId(0L);
    aPeriod.setMonth(2);
    aPeriod.setYear(2012);
    Assert.assertEquals(true, fileParser.parseContent());
    Assert.assertEquals(0, fileParser.getErrorList().size());
    saleService.saveOrUpdate();
    Assert.assertEquals(2, salesDAO.getAllSales(DTOUtils.periodToPeriodDTO(fiscalPeriodDAO.findPeriod(aPeriod))).size());
  }

  private FileItem getFileItem(String fileName) {
    URL filePath = getClass().getResource("/financialFiles/" + fileName);
    MockFileItem fileItem = null;
    try {
      fileItem = new MockFileItem(filePath, fileName);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return fileItem;
  }

}
