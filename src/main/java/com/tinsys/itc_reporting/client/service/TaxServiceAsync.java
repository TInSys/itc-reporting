package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface TaxServiceAsync {

  void createTax(TaxDTO aTax, TaxPeriodDTO periodToClose, AsyncCallback<TaxDTO> callback);

  void deleteTax(TaxDTO aTax, AsyncCallback<Void> callback);

  void findTax(Long id, AsyncCallback<TaxDTO> callback);

  void updateTax(TaxDTO aTax, AsyncCallback<TaxDTO> callback);

  void getAllTaxs(ZoneDTO zoneDTO, AsyncCallback<ArrayList<TaxDTO>> callback);

}
