package com.tinsys.itc_reporting.dao;

import java.util.List;

import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public interface SalesDAO {

  public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO);

  public List<Sales> getAllSales(FiscalPeriodDTO fromFiscalPeriodDTO, FiscalPeriodDTO toFiscalPeriodDTO, List<RoyaltyDTO> royalties);

  public Sales findSale(Sales sale);

  public void saveOrUpdate(List<Sales> summarizedSales);

}
