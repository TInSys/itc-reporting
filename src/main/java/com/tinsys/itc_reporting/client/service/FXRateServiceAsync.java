package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface FXRateServiceAsync {

  void createFXRate(FXRateDTO aFXRate, AsyncCallback<FXRateDTO> callback);

  void deleteFXRate(FXRateDTO aFXRate, AsyncCallback<Void> callback);

  void getAllFXRates(ZoneDTO zoneDTO, AsyncCallback<ArrayList<FXRateDTO>> callback);

  void updateFXRate(FXRateDTO aFXRate, AsyncCallback<FXRateDTO> callback);

  void getAllFXRatesForPeriod(FiscalPeriodDTO fiscalPeriodDto, AsyncCallback<ArrayList<FXRateDTO>> callback);

  void saveOrUpdate(List<FXRateDTO> fxRateList, AsyncCallback<Void> callback);

}
