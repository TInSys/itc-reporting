package com.tinsys.itc_reporting.server.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Zone;

public class FinancialReportFileParserImpl implements FinancialReportFileParser {

  private FileItem fileItem;
  private Zone zone;
  private FiscalPeriod period;
  private String fileName;
  
  private List<String> errorList = new ArrayList<String>();
  
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
  public void parseContent() {
    

  }

}
