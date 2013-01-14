package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Repository
public class SalesDAOImpl implements SalesDAO {

  private static final Logger logger = Logger.getLogger(SalesDAOImpl.class);

  private SessionFactory factory;

  public SessionFactory getFactory() {
    return factory;
  }

  public void setFactory(SessionFactory factory) {
    this.factory = factory;
  }

  @Override
  public Sales findSale(Sales aSale) {
    Sales sales;
    if (aSale.getPromoCode() == null) {
      sales = (Sales) factory.getCurrentSession().createCriteria(Sales.class).add(Restrictions.eq("application", aSale.getApplication()))
          .add(Restrictions.eq("zone", aSale.getZone())).add(Restrictions.eq("period", aSale.getPeriod())).add(Restrictions.eq("countryCode", aSale.getCountryCode()))
          .add(Restrictions.isNull("promoCode")).add(Restrictions.eq("individualPrice", aSale.getIndividualPrice())).uniqueResult();
    } else {
      sales = (Sales) factory.getCurrentSession().createCriteria(Sales.class).add(Restrictions.eq("application", aSale.getApplication()))
          .add(Restrictions.eq("zone", aSale.getZone())).add(Restrictions.eq("period", aSale.getPeriod())).add(Restrictions.eq("countryCode", aSale.getCountryCode()))
          .add(Restrictions.eq("promoCode", aSale.getPromoCode())).add(Restrictions.eq("individualPrice", aSale.getIndividualPrice())).uniqueResult();
    }

    return sales;
  }

  @Override
  public void saveOrUpdate(List<Sales> summarizedSales) {
    for (Sales sale : summarizedSales) {
      factory.getCurrentSession().merge(sale);
    }
  }

  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO) {
    FiscalPeriod period = (FiscalPeriod) factory.getCurrentSession().createCriteria(FiscalPeriod.class).add(Restrictions.eq("month", aFiscalPeriodDTO.getMonth()))
        .add(Restrictions.eq("year", aFiscalPeriodDTO.getYear())).uniqueResult();
    if (period != null) {
      Criteria criteria = factory.getCurrentSession().createCriteria(Sales.class);
      Criteria subCriteriaZone = criteria.createCriteria("zone");
      subCriteriaZone.addOrder(Order.asc("name"));
      Criteria subCriteriaApplication = criteria.createCriteria("application");
      subCriteriaApplication.addOrder(Order.asc("name"));
      @SuppressWarnings("unchecked")
      List<Sales> sales = criteria.add(Restrictions.eq("period", period)).list();

      return sales;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Sales> getAllSales(FiscalPeriodDTO fromFiscalPeriodDTO, FiscalPeriodDTO toFiscalPeriodDTO, List<RoyaltyDTO> royaltiesDTO) {
    List<Sales> sales = new ArrayList<Sales>();
    LogicalExpression firstYear = Restrictions.and(Restrictions.eq("year", fromFiscalPeriodDTO.getYear()), Restrictions.ge("month", fromFiscalPeriodDTO.getMonth()));
    LogicalExpression lastYear = Restrictions.and(Restrictions.eq("year", toFiscalPeriodDTO.getYear()), Restrictions.le("month", toFiscalPeriodDTO.getMonth()));
    LogicalExpression betweenYears = Restrictions.and(Restrictions.gt("year", fromFiscalPeriodDTO.getYear()), Restrictions.lt("year", toFiscalPeriodDTO.getYear()));
    List<FiscalPeriod> periods = null;
    if (fromFiscalPeriodDTO.getYear() == toFiscalPeriodDTO.getYear()) {
      periods = (List<FiscalPeriod>) factory.getCurrentSession().createCriteria(FiscalPeriod.class).add(Restrictions.or(Restrictions.and(firstYear, lastYear), betweenYears))
          .addOrder(Order.asc("year")).addOrder(Order.asc("month")).list();
    } else {
      periods = (List<FiscalPeriod>) factory.getCurrentSession().createCriteria(FiscalPeriod.class).add(Restrictions.or(Restrictions.or(firstYear, lastYear), betweenYears))
          .addOrder(Order.asc("year")).addOrder(Order.asc("month")).list();
    }

    if (periods != null && periods.size() > 0) {
      logger.debug(" periods found :" + periods.size());

      Criteria criteria = factory.getCurrentSession().createCriteria(Sales.class);
      Criteria subCriteriaZone = criteria.createCriteria("zone");
      subCriteriaZone.addOrder(Order.asc("name"));
      Criteria subCriteriaApplication = criteria.createCriteria("application");
      subCriteriaApplication.addOrder(Order.asc("name"));

      Criterion royaltyFilter = null;
      for (RoyaltyDTO royaltyDTO : royaltiesDTO) {
        Application application = DTOUtils.applicationDTOToApplication(royaltyDTO.getApplication());
        List<Zone> zoneList = new ArrayList<Zone>();
        for (ZoneDTO zoneDTO : royaltyDTO.getZones()) {
          zoneList.add(DTOUtils.zoneDTOToZone(zoneDTO));
        }
        if (royaltyFilter == null) {
          royaltyFilter = Restrictions.and(Restrictions.eq("application", application), Restrictions.in("zone", zoneList));
        } else {
          royaltyFilter = Restrictions.or(royaltyFilter, Restrictions.and(Restrictions.eq("application", application), Restrictions.in("zone", zoneList)));
        }
      }
      if (royaltyFilter != null) {
        sales = (List<Sales>) criteria.add(Restrictions.in("period", periods)).add(royaltyFilter).list();
        logger.debug(" # sales lines : " + sales.size());
      }
      return sales;
    }
    return null;
  }

}
