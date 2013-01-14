package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface TaxDAO {

  public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO);

  public TaxDTO createTax(TaxDTO aTax);

  public TaxDTO updateTax(TaxDTO aTax);

  public void deleteTax(TaxDTO aTax);

  public List<Tax> getTaxesForPeriod(FiscalPeriodDTO period);

}
