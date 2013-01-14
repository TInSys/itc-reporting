package com.tinsys.itc_reporting.dao;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.model.TaxPeriod;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;

public class TaxPeriodDAOImpl implements TaxPeriodDAO {

  private SessionFactory factory;

  public SessionFactory getFactory() {
    return factory;
  }

  public void setFactory(SessionFactory factory) {
    this.factory = factory;
  }

  @Transactional
  @Override
  public TaxPeriodDTO createPeriod(TaxPeriodDTO aPeriod) {

    TaxPeriod period = DTOUtils.taxPeriodDTOtoTaxPeriod(aPeriod);
    try {
      factory.getCurrentSession().persist(period);
      Long id = (Long) factory.getCurrentSession().getIdentifier(period);
      aPeriod.setId(id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return aPeriod;
  }

  @Override
  public TaxPeriodDTO updatePeriod(TaxPeriodDTO aPeriod) {
    TaxPeriod period = (TaxPeriod) factory.getCurrentSession().get(TaxPeriod.class, aPeriod.getId());
    period.setStartDate(aPeriod.getStartDate());
    period.setStopDate(aPeriod.getStopDate());
    factory.getCurrentSession().saveOrUpdate(period);
    return aPeriod;
  }

  @Override
  public void deletePeriod(TaxPeriodDTO aPeriod) {
    TaxPeriod period = (TaxPeriod) factory.getCurrentSession().get(TaxPeriod.class, aPeriod.getId());
    period.setStartDate(aPeriod.getStartDate());
    period.setStopDate(aPeriod.getStopDate());
    factory.getCurrentSession().delete(period);

  }

}
