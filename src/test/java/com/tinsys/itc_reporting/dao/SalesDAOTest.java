package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

public class SalesDAOTest implements SalesDAO {

  @Override
  public ArrayList<SalesDTO> getAllSales() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO fromFiscalPeriodDTO, FiscalPeriodDTO toFiscalPeriodDTO, List<RoyaltyDTO> royalties) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SalesDTO findSale(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Sales findSale(Sales sale) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Sales createSale(Sales aSale) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Sales updateSale(Sales aSale) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteSale(SalesDTO aSale) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void saveOrUpdate(List<Sales> summarizedSales) {
    // TODO Auto-generated method stub
    
  }

}
