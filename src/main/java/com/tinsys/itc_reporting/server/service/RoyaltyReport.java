package com.tinsys.itc_reporting.server.service;

import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

public interface RoyaltyReport {
  
  public void init(CompanyDTO company);
  
  public boolean isNewLine();
  
  public void addLine();
  
  public void resetLine();
  
  void setCurrentData();
}
