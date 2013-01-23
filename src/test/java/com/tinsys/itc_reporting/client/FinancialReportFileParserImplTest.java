package com.tinsys.itc_reporting.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.tinsys.itc_reporting.server.utils.FinancialReportFileParserImpl;

public class FinancialReportFileParserImplTest {

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testParsePeriodNotOk() {
    FileItem fileItem = getFileItem("/financialFiles/unparsablePeriod.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(),1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("could not retrieve period in file name"));
  }

  /*
  @Test
  public void testParseZone() {
    FileItem fileItem = getFileItem("/financialFiles/unparsableZone_0102_.txt");
    FinancialReportFileParserImpl fileParser = new FinancialReportFileParserImpl(fileItem);
    Assert.assertEquals(fileParser.getErrorList().size(),1);
    Assert.assertThat(fileParser.getErrorList().get(0), JUnitMatchers.containsString("could not retrieve period in file name"));
  }

  @Test
  public void testParseContent() {
    fail("Not yet implemented");
  }
  */

  private FileItem getFileItem(String string) {
    URL filePath = getClass().getResource(string);
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(filePath.getPath());
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    int availableBytes = 0;
    try {
      availableBytes = inputStream.available();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    File outFile = new File(filePath.getPath());
    FileItem fileItem = new DiskFileItem("fileUpload", "plain/text", false, filePath.getFile(), availableBytes, outFile);   
    return fileItem;
  }
  
}
