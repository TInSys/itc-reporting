package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

public interface SalesDAO {

  public ArrayList<SalesDTO> getAllSales();

  public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO);

  public List<Sales> getAllSales(FiscalPeriodDTO fromFiscalPeriodDTO, FiscalPeriodDTO toFiscalPeriodDTO, List<RoyaltyDTO> royalties);

  public SalesDTO findSale(Long id);

  public Sales findSale(Sales sale);

  public Sales createSale(Sales aSale);

  public Sales updateSale(Sales aSale);

  public void deleteSale(SalesDTO aSale);

  public void saveOrUpdate(List<Sales> summarizedSales);

}
