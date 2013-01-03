package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@RemoteServiceRelativePath("springGwtServices/taxService")
public interface TaxService extends RemoteService {

  public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO) throws RuntimeException;

  public TaxDTO findTax(Long id);

  public TaxDTO createTax(TaxDTO aTax, TaxPeriodDTO aPeriod) throws RuntimeException;

  public TaxDTO updateTax(TaxDTO aTax);

  public void deleteTax(TaxDTO aTax);
}
