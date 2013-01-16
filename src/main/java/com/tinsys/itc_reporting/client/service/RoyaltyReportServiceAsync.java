package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;

public interface RoyaltyReportServiceAsync {

  void getCompanyReport(CompanyDTO company, FiscalPeriodDTO startPeriod, FiscalPeriodDTO endPeriod, AsyncCallback<ArrayList<RoyaltyReportLine>> callback);

}
