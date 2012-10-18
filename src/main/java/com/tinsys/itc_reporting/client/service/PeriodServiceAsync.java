package com.tinsys.itc_reporting.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;

public interface PeriodServiceAsync {

   void updatePeriod(PeriodDTO aPeriod, AsyncCallback<PeriodDTO> callback);

}
