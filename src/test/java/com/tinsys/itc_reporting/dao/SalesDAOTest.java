package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

public class SalesDAOTest implements SalesDAO {

  ArrayList<SalesDTO> salesDTOList = new ArrayList<SalesDTO>();
  
  public void init() {
    salesDTOList.add(this.getSalesDTO());
  }

  @Override
  public ArrayList<SalesDTO> getAllSales() {
    return salesDTOList;
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
    
  }

  private SalesDTO getSalesDTO() {
    // TODO Auto-generated method stub
    SalesDTO salesDTO = new SalesDTO();
    salesDTO.setApplication(new ApplicationDTO());
    return null;
  }
  
}
