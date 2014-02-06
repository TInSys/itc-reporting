package com.tinsys.itc_reporting.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneReportSummary;

public interface SalesReportServiceAsync {

  void getMonthlyReport(FiscalPeriodDTO startPeriod,FiscalPeriodDTO endPeriod, AsyncCallback<List<ZoneReportSummary>> callback);

}
