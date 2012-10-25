package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

public interface SalesDAO {

    public ArrayList<SalesDTO> getAllSales();

    public SalesDTO findSale(Long id);

    public Sales findSale(Sales sale);
    
    public Sales createSale(Sales aSale);

    public Sales updateSale(Sales aSale);

    public void deleteSale(SalesDTO aSale);

   public void saveOrUpdate(List<Sales> summarizedSales);

}
