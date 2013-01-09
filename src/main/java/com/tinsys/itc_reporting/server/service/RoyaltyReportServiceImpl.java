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
  private RoyaltyReportImpl royaltyReport;
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

  @Override
  public List<RoyaltyReportLine> getCompanyReport(CompanyDTO company, FiscalPeriodDTO startPeriod, FiscalPeriodDTO endPeriod) {
    logger.debug("Preparing report");
    List<RoyaltyDTO> royalties = royaltyDAO.getAllRoyalty(company);
    List<Sales> sales = salesDAO.getAllSales(startPeriod, endPeriod, royalties);
    
    Collections.sort(sales, compareSales);
    List<SalesDTO> salesDTOList = new ArrayList<SalesDTO>();
    
    for (Sales sale : sales) {
      salesDTOList.add(DTOUtils.salesToSalesDTO(sale));
    }

    royaltyReport.init(company);
    for (SalesDTO salesDTO : salesDTOList) {
      if (salesDTO.getTotalPrice().compareTo(new BigDecimal(0)) != 0) {
        royaltyReport.setSalesDTO(salesDTO);
        if (royaltyReport.isNewLine()) {
          royaltyReport.addLine();
          royaltyReport.resetLine();
        }
//        royaltyReport.setCurrentData(salesDTO);
      }
    }
    royaltyReport.addLine();
    return royaltyReport.getReport();
  }

}
