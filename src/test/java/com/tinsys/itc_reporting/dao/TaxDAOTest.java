package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class TaxDAOTest implements TaxDAO {

  @Override
  public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TaxDTO findTax(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TaxDTO createTax(TaxDTO aTax) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TaxDTO updateTax(TaxDTO aTax) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteTax(TaxDTO aTax) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public List<Tax> getTaxesForPeriod(FiscalPeriodDTO period) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Tax> getAllTaxs() {
    // TODO Auto-generated method stub
    return null;
  }

}
