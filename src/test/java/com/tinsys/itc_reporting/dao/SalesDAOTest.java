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
    this.createSales();
  }

  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO) {
    List<Sales> tmpSales = new ArrayList<Sales>();
    for (Sales sales : this.salesList) {
      if (sales.getPeriod().getMonth() == DTOUtils.periodDTOtoPeriod(aFiscalPeriodDTO).getMonth()
          && sales.getPeriod().getYear() == DTOUtils.periodDTOtoPeriod(aFiscalPeriodDTO).getYear()) {
        tmpSales.add(sales);
      }
    }
    return tmpSales;
  }

  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO fromFiscalPeriodDTO, FiscalPeriodDTO toFiscalPeriodDTO, List<RoyaltyDTO> royalties) {
    ArrayList<Sales> tmpSales = null;
    for (RoyaltyDTO royaltyDTO : royalties) {
      for (Sales sale : salesList) {
        SalesDTO saleDTO = DTOUtils.salesToSalesDTO(sale);
        if (saleDTO.getApplication().getId() == royaltyDTO.getApplication().getId() && royaltyDTO.getZones().contains(saleDTO.getZone())
            && (sale.getPeriod().compareTo(DTOUtils.periodDTOtoPeriod(fromFiscalPeriodDTO)) <= 0)
            && (sale.getPeriod().compareTo(DTOUtils.periodDTOtoPeriod(toFiscalPeriodDTO)) >= 0)) {
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
  public Sales findSale(Sales aSale) {

    for (Sales sales : this.salesList) {

      if (sales.getApplication().equals(aSale.getApplication()) && sales.getZone().equals(aSale.getZone())
          && sales.getPeriod().equals(aSale.getPeriod()) && sales.getCountryCode().equals(aSale.getCountryCode())
          && sales.getIndividualPrice().equals(aSale.getIndividualPrice())) {
        if (aSale.getPromoCode() == null && sales.getPromoCode() == null) {
          return sales;
        } else if (aSale.getPromoCode() != null && aSale.getPromoCode() != null && aSale.getPromoCode().equals(sales.getPromoCode())) {
          return sales;
        }
      }
    }
    return null;
  }

  @Override
  public void saveOrUpdate(List<Sales> summarizedSales) {
    for (Sales sales : summarizedSales) {
      if (sales.getId() != null) {
        for (Sales currentSales : this.salesList) {
          if (currentSales.getId() == sales.getId()) {
            currentSales = sales;
            break;
          }
        }
      } else {
        sales.setId((long) (this.salesList.size() - 1));
        this.salesList.add(sales);
      }
    }
  }

  public void createSales() {

    // sales ok
    Sales sales = new Sales();
    sales.setId(0L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    FiscalPeriod period = new FiscalPeriod();
    period.setId(1L);
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
    period.setId(3L);
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
    period.setId(4L);
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
    sales = new Sales();
    sales.setId(3L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setId(5L);
    period.setMonth(5);
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

    // distinct zones
    sales = new Sales();
    sales.setId(4L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setId(6L);
    period.setMonth(6);
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

    sales = new Sales();
    sales.setId(5L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setId(6L);
    period.setMonth(6);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    zone = new Zone();
    zone.setCode("ZONE1");
    zone.setName("ZONE1");
    zone.setId(1L);
    sales.setZone(zone);
    salesList.add(sales);

    // distinct periods
    sales = new Sales();
    sales.setId(6L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setId(7L);
    period.setMonth(7);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    zone = new Zone();
    zone.setCode("ZONE1");
    zone.setName("ZONE1");
    zone.setId(1L);
    sales.setZone(zone);
    salesList.add(sales);

    sales = new Sales();
    sales.setId(7L);
    sales.setApplication(DTOUtils.applicationDTOToApplication(applicationDAO.findApplication(0L)));
    sales.setIndividualPrice(new BigDecimal(10));
    sales.setIndividualProceeds(new BigDecimal(8));
    period = new FiscalPeriod();
    period.setId(8L);
    period.setMonth(8);
    period.setYear(2013);
    sales.setPeriod(period);
    sales.setSoldUnits(4);
    sales.setTotalPrice(sales.getIndividualPrice().multiply(new BigDecimal(sales.getSoldUnits())));
    sales.setTotalProceeds(sales.getIndividualProceeds().multiply(new BigDecimal(sales.getSoldUnits())));
    zone = new Zone();
    zone.setCode("ZONE1");
    zone.setName("ZONE1");
    zone.setId(1L);
    sales.setZone(zone);
    salesList.add(sales);
  }

}
