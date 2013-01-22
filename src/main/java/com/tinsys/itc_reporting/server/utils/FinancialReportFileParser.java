package com.tinsys.itc_reporting.server.utils;

import java.util.List;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Zone;

public interface FinancialReportFileParser {

  public Zone parseZone();
  
  public FiscalPeriod parsePeriod();
  
  void parseContent();
  
  public List<String> getErrorList();
  
  public String getFileName();
  
}
