package com.tinsys.itc_reporting.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.MonthReportSummary;

@RemoteServiceRelativePath("springGwtServices/salesReportService")
public interface SalesReportService extends RemoteService {

    public List<MonthReportSummary> getMonthlyReport(FiscalPeriodDTO period);
}