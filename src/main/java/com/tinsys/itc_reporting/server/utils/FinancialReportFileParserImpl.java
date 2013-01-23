package com.tinsys.itc_reporting.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.fileupload.FileItem;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.service.SaleService;

public class FinancialReportFileParserImpl implements FinancialReportFileParser {

  private SaleService saleService; 
  private FileItem fileItem;
  private Zone zone;
  private FiscalPeriod period;
  private String fileName;
  
  private List<String> errorList = new ArrayList<String>();
  
  public FinancialReportFileParserImpl() {
  }
  
  public SaleService getSaleService() {
    return saleService;
  }

  public void setSaleService(SaleService saleService) {
    this.saleService = saleService;
  }

  @Override
  public List<String> getErrorList() {
    return errorList;
  }

  public void setErrorList(List<String> errorList) {
    this.errorList = errorList;
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public FinancialReportFileParserImpl(FileItem item) {
    this.fileItem = item;
    this.init();
  }
  
  private void init() {
    if (fileItem.isFormField()) {
      return;
    }
    this.period = this.parsePeriod();
    if (this.period != null) {
      this.zone = this.parseZone();
      if (this.zone != null) {
        this.fileName = this.parseFileName(); 
      }
    }
  }


  @Override
  public FiscalPeriod parsePeriod() {
    int periodIndex = this.fileItem.getName().indexOf("_");
    if (periodIndex == -1 || this.fileItem.getName().length() < periodIndex + 5) {
      this.errorList.add("For file "+this.fileItem.getName()+" ,could not retrieve period in file name ");
      return null;
    }
    String fileMonth = fileItem.getName().substring(periodIndex + 1, periodIndex + 3);
    String fileYear = fileItem.getName().substring(periodIndex + 3, periodIndex + 5);
    if ((!fileMonth.matches("[0-9]+") || Integer.parseInt(fileMonth) < 1 || Integer.parseInt(fileMonth) > 12) || !fileYear.matches("[0-9]+")) {
      this.errorList.add("Period can't be parsed in the file name :" + fileItem.getName());
      return null;
    }
    FiscalPeriod period = new FiscalPeriod();
    period.setMonth(Integer.parseInt(fileMonth));
    period.setYear(Integer.parseInt("20" + fileYear));
    return period;
  }
  
  @Override
  public Zone parseZone() {
    int periodIndex = this.fileItem.getName().indexOf("_");
    int zoneIndex = this.fileItem.getName().indexOf("_", periodIndex+1);
    if (zoneIndex == -1 || this.fileItem.getName().length() < zoneIndex + 2 ) {
      this.errorList.add("zone code can't be parsed in the file name :" + fileItem.getName());
      return null;
    }
    Zone zone = new Zone();
    zone.setCode(this.fileItem.getName().substring(zoneIndex + 1, zoneIndex + 3));
    return zone;
  }
  
  private String parseFileName() {
    int fileNameIndex = fileItem.getName().indexOf(".");
    if (fileNameIndex == -1) {
      this.errorList.add("file "+fileItem.getName()+" should have an extension :");
      return null;
    }
    return fileItem.getName().substring(0,fileNameIndex);
  }

  @Override
  public boolean parseContent() throws IOException {
    
    this.zone = saleService.findZone(this.zone.getCode());
    period = saleService.findOrCreatePeriod(period);
    if (zone == null) {
      this.errorList.add("No corresponding Zone found in database for :" + this.zone.getCode() + " . File " + this.fileItem.getName() + " won't be processed");
      return false;
    }
    InputStream fis;
    GZIPInputStream gfis;
    LabeledCSVParser lcsvp;
    if (this.fileItem.getContentType().equalsIgnoreCase("application/x-gzip")) {
      gfis = new GZIPInputStream(this.fileItem.getInputStream());
      lcsvp = new LabeledCSVParser(new CSVParser(gfis));
    } else {
      fis = this.fileItem.getInputStream();
      lcsvp = new LabeledCSVParser(new CSVParser(fis));
    }
    lcsvp.changeDelimiter('\t');
    while (lcsvp.getLine() != null && lcsvp.getValueByLabel("Quantity") != null) {
      Sales tmpSale = new Sales();
      String fileApp = lcsvp.getValueByLabel("Vendor Identifier");
      Application application = saleService.findApplication(fileApp);
      if (application == null) {
        this.errorList.add("No corresponding Application found in database for :" + fileApp + " . File " + this.fileItem.getName() + " won't be processed");
        continue;
      }
      tmpSale.setPeriod(period);
      tmpSale.setZone(zone);
      tmpSale.setApplication(application);
      tmpSale.setCountryCode(lcsvp.getValueByLabel("Country Of Sale"));
      tmpSale.setPromoCode(lcsvp.getValueByLabel("Promo Code"));
      if (tmpSale.getPromoCode().length() == 0) {
        tmpSale.setPromoCode(null);
      }
      tmpSale.setIndividualPrice(new BigDecimal(lcsvp.getValueByLabel("Customer Price")));
      tmpSale.setIndividualProceeds(new BigDecimal(lcsvp.getValueByLabel("Partner Share")));
      tmpSale.setSoldUnits(Integer.parseInt(lcsvp.getValueByLabel("Quantity")));
      tmpSale.setTotalPrice(tmpSale.getIndividualPrice().multiply(new BigDecimal(tmpSale.getSoldUnits())));
      tmpSale.setTotalProceeds(tmpSale.getIndividualProceeds().multiply(new BigDecimal(tmpSale.getSoldUnits())));
      if (this.errorList.size() == 0) {
        saleService.summarizeSale(tmpSale);
      }
    }
    return true;
  }

}
