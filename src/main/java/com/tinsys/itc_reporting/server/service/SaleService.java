package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.dao.ApplicationDAO;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.FiscalPeriodDAO;
import com.tinsys.itc_reporting.dao.SalesDAO;
import com.tinsys.itc_reporting.dao.ZoneDAO;
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Zone;

@Service("saleService")
@Transactional
public class SaleService {

   @Autowired
   @Qualifier("applicationDAO")
   private ApplicationDAO applicationDAO;

   @Autowired
   @Qualifier("fxRateDAO")
   private FXRateDAO fxRateDAO;

   @Autowired
   @Qualifier("fiscalPeriodDAO")
   private FiscalPeriodDAO periodDAO;

   @Autowired
   @Qualifier("zoneDAO")
   private ZoneDAO zoneDAO;

   @Autowired
   @Qualifier("salesDAO")
   private SalesDAO saleDAO;

   private List<Sales> summarizedSales;

   public FiscalPeriod findPeriod(FiscalPeriod period) {
      FiscalPeriod result = periodDAO.findPeriod(period);
      return result;
   }

   public Zone findZone(String code) {
      Zone result = zoneDAO.findZoneByCode(code);
      return result;
   }

   public Application findApplication(String vendorID) {
      Application result = applicationDAO.findApplicationByVendorID(vendorID);
      return result;
   }

   public void summarizeSale(Sales tmpSale) {
      boolean summarized = false;
      if (summarizedSales == null) {
         summarizedSales = new ArrayList<Sales>();
      } else {
         for (Sales sale : summarizedSales) {
            if (sale.getApplication().equals(tmpSale.getApplication())
                  && sale.getPeriod().equals(tmpSale.getPeriod())
                  && sale.getZone().equals(tmpSale.getZone())
                  && sale.getCountryCode().equals(tmpSale.getCountryCode())
                  && sale.getIndividualPrice().equals(
                        tmpSale.getIndividualPrice())) {
               summarized = true;
               sale.setSoldUnits(sale.getSoldUnits() + tmpSale.getSoldUnits());
               sale.setTotalPrice(sale.getTotalPrice().add(
                     tmpSale.getIndividualPrice().multiply(
                           new BigDecimal(tmpSale.getSoldUnits()))));
            }
         }
      }
      if (!summarized) {
         summarizedSales.add(tmpSale);
      }

   }

   public void saveOrUpdate() {
      for (Sales sale : summarizedSales) {
         Sales tmpSale = new Sales();
         Sales existingSale = saleDAO.findSale(tmpSale);
         if (existingSale != null) {
            sale.setId(existingSale.getId());
         }
      }
      saleDAO.saveOrUpdate(summarizedSales);
   }
}
