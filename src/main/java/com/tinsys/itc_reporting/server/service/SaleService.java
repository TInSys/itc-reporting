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
import com.tinsys.itc_reporting.server.utils.DTOUtils;

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

    public FiscalPeriod findOrCreatePeriod(FiscalPeriod period) {
        FiscalPeriod result = periodDAO.findPeriod(period);
        if (result==null){
            result = DTOUtils.periodDTOtoPeriod(periodDAO.createPeriod(DTOUtils.periodToPeriodDTO(period)));
        }
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
            for (Sales sale : summarizedSales) {
                if (sale.getApplication().equals(tmpSale.getApplication())
                        && sale.getPeriod().equals(tmpSale.getPeriod())
                        && sale.getZone().equals(tmpSale.getZone())
                        && sale.getCountryCode().equals(
                                tmpSale.getCountryCode())
                        && sale.getPromoCode().equals(
                                tmpSale.getPromoCode())
                        && sale.getIndividualPrice().equals(
                                tmpSale.getIndividualPrice())) {
                    summarized = true;
                    sale.setSoldUnits(sale.getSoldUnits());
                    sale.setTotalPrice(tmpSale.getIndividualPrice().multiply(
                                    new BigDecimal(tmpSale.getSoldUnits())));
                }
            }
        if (!summarized) {
            summarizedSales.add(tmpSale);
        }

    }

    public void saveOrUpdate() {
        if (summarizedSales.size() != 0) {
            for (Sales sale : summarizedSales) {
                Sales existingSale = saleDAO.findSale(sale);
                if (existingSale != null) {
                    sale.setId(existingSale.getId());
                }
            }
            saleDAO.saveOrUpdate(summarizedSales);
        }
    }

    public void reset() {
        summarizedSales = new ArrayList<Sales>();
    }
}
