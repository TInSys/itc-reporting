package com.tinsys.itc_reporting.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;

@RemoteServiceRelativePath("springGwtServices/royaltyReportService")
public interface RoyaltyReportService extends RemoteService {

    public List<RoyaltyReportLine> getCompanyReport(CompanyDTO company,FiscalPeriodDTO startPeriod, FiscalPeriodDTO endPeriod) throws RuntimeException;
}
