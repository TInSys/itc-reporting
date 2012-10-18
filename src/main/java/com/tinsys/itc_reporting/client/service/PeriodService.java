package com.tinsys.itc_reporting.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;

@RemoteServiceRelativePath("springGwtServices/periodService")
public interface PeriodService extends RemoteService {

    public PeriodDTO updatePeriod(PeriodDTO aPeriod);

}
