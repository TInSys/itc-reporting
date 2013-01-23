package com.tinsys.itc_reporting.server.utils;

import java.io.IOException;
import java.util.List;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.service.SaleService;

public interface FinancialReportFileParser {

  public Zone parseZone();
  
  public FiscalPeriod parsePeriod();
  
  public void setSaleService(SaleService saleService);
  
  public boolean parseContent() throws IOException;
  
  public List<String> getErrorList();
  
  public String getFileName();
  
}
