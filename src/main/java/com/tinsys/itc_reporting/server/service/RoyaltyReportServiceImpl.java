package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.RoyaltyReportService;
import com.tinsys.itc_reporting.dao.RoyaltyDAO;
import com.tinsys.itc_reporting.dao.SalesDAO;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

@Service("royaltyReportService")
@Transactional
public class RoyaltyReportServiceImpl implements RoyaltyReportService {

  private static final Logger logger = Logger.getLogger(RoyaltyReportServiceImpl.class);

  @Autowired
  @Qualifier("salesDAO")
  private SalesDAO salesDAO;

  @Autowired
  @Qualifier("royaltyDAO")
  private RoyaltyDAO royaltyDAO;

  @Autowired
  @Qualifier("royaltyReport")
  private RoyaltyReportBuilderImpl royaltyReportBuilder;

  private Comparator<Sales> compareSales = new Comparator<Sales>() {

    @Override
    public int compare(Sales o1, Sales o2) {
      int c;
      c = Integer.valueOf(o1.getPeriod().getYear()).compareTo(Integer.valueOf(o2.getPeriod().getYear()));
      if (c == 0) {
        c = Integer.valueOf(o1.getPeriod().getMonth()).compareTo(Integer.valueOf(o2.getPeriod().getMonth()));
      }
      if (c == 0) {
        c = o1.getApplication().getVendorID().compareTo(o2.getApplication().getVendorID());
      }
      if (c == 0) {
        c = o1.getZone().getCode().compareTo(o2.getZone().getCode());
      }

      return c;
    }
  };

  public void setSalesDAO(SalesDAO salesDAO) {
    this.salesDAO = salesDAO;
  }

  public void setRoyaltyDAO(RoyaltyDAO royaltyDAO) {
    this.royaltyDAO = royaltyDAO;
  }

  public void setRoyaltyReportBuilder(RoyaltyReportBuilderImpl royaltyReportBuilder) {
    this.royaltyReportBuilder = royaltyReportBuilder;
  }

  @Override
  public List<RoyaltyReportLine> getCompanyReport(CompanyDTO company, FiscalPeriodDTO startPeriod, FiscalPeriodDTO endPeriod) {
    logger.debug("Preparing report");
    if (company != null) {
    List<RoyaltyDTO> royalties = royaltyDAO.getAllRoyalty(company);
    List<Sales> sales = salesDAO.getAllSales(startPeriod, endPeriod, royalties);
    if (sales != null) {
      Collections.sort(sales, compareSales);
      List<SalesDTO> salesDTOList = new ArrayList<SalesDTO>();

      for (Sales sale : sales) {
        salesDTOList.add(DTOUtils.salesToSalesDTO(sale));
      }

      royaltyReportBuilder.init(company);
      for (SalesDTO salesDTO : salesDTOList) {
        if (salesDTO.getTotalPrice().compareTo(new BigDecimal(0)) != 0) {
          royaltyReportBuilder.setSalesDTO(salesDTO);
          if (royaltyReportBuilder.isNewLine()) {
            royaltyReportBuilder.addLine();
            royaltyReportBuilder.resetLine();
          }
          royaltyReportBuilder.setCurrentData();
        }
      }
      royaltyReportBuilder.addLine();
      return royaltyReportBuilder.getReport();
    }
    }
    return null;
  }

}
