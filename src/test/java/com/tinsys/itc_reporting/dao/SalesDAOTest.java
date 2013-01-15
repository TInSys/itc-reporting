package com.tinsys.itc_reporting.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

public class SalesDAOTest implements SalesDAO {

  ArrayList<Sales> salesList = new ArrayList<Sales>();
  ApplicationDAOTest applicationDAO = new ApplicationDAOTest();
  
  public SalesDAOTest() {
    this.getSales();
  }
  
  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO fromFiscalPeriodDTO, FiscalPeriodDTO toFiscalPeriodDTO, List<RoyaltyDTO> royalties) {
    ArrayList<Sales> tmpSales = null;
    for (RoyaltyDTO royaltyDTO : royalties) {
      for (Sales sale : salesList) {
        SalesDTO saleDTO = DTOUtils.salesToSalesDTO(sale);
        if (saleDTO.getApplication().getId() == royaltyDTO.getApplication().getId() && royaltyDTO.getZones().contains(saleDTO.getZone()) && (sale.getPeriod().compareTo(DTOUtils.periodDTOtoPeriod(fromFiscalPeriodDTO)) <= 0) && (sale.getPeriod().compareTo(DTOUtils.periodDTOtoPeriod(toFiscalPeriodDTO)) >= 0)) {
          if (tmpSales == null) {
            tmpSales = new ArrayList<Sales>();
          }
          tmpSales.add(sale);
        }
      }
    }
    return tmpSales;
  }

  @Override
  public Sales findSale(Sales sale) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void saveOrUpdate(List<Sales> summarizedSales) {
    
  }

  private void getSales() {
    
    Sales sales = new Sales();
    // sales ok
    sales.setId(0L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    FiscalPeriod period = new FiscalPeriod();
    period.setMonth(1);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    Zone zone = new Zone();
    zone.setCode("ZONE0");
    zone.setName("ZONE0");
    zone.setId(0L);
    sales.setZone(zone);
    salesList.add(sales);
    
    // no fxChange rate defined for period
    sales = new Sales();
    sales.setId(1L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setMonth(3);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    zone = new Zone();
    zone.setCode("ZONE0");
    zone.setName("ZONE0");
    zone.setId(0L);
    sales.setZone(zone);
    salesList.add(sales);
    
    // no royalties
    sales = new Sales();
    sales.setId(2L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setMonth(4);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    zone = new Zone();
    zone.setCode("ZONE0");
    zone.setName("ZONE0");
    zone.setId(0L);
    sales.setZone(zone);
    salesList.add(sales);
    
    // tax amount
    sales.setId(3L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setMonth(4);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    zone = new Zone();
    zone.setCode("ZONE0");
    zone.setName("ZONE0");
    zone.setId(0L);
    sales.setZone(zone);
    salesList.add(sales);
  }
  
}
