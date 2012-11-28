package com.tinsys.itc_reporting.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

public interface RoyaltyReportServiceAsync {

   void getCompanyReport(CompanyDTO company, FiscalPeriodDTO startPeriod,
         FiscalPeriodDTO endPeriod,
         AsyncCallback<List<SalesDTO>> callback);

}
