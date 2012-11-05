package com.tinsys.itc_reporting.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;

public class FiscalPeriodDAOImpl implements FiscalPeriodDAO {

    private SessionFactory factory;

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Transactional
    @Override
    public FiscalPeriodDTO createPeriod(FiscalPeriodDTO aPeriod) {

        FiscalPeriod period = DTOUtils.periodDTOtoPeriod(aPeriod);
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
    public FiscalPeriodDTO updatePeriod(FiscalPeriodDTO aPeriod) {
        FiscalPeriod period = (FiscalPeriod) factory.getCurrentSession().get(
                FiscalPeriod.class, aPeriod.getId());
        try {
            period.setMonth(aPeriod.getMonth());
            period.setYear(aPeriod.getYear());
            factory.getCurrentSession().saveOrUpdate(period);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return aPeriod;
    }

    @Override
    public void deletePeriod(FiscalPeriodDTO aPeriod) {
        FiscalPeriod period = (FiscalPeriod) factory.getCurrentSession().get(
                FiscalPeriod.class, aPeriod.getId());
        try {
            period.setMonth(aPeriod.getMonth());
            period.setYear(aPeriod.getYear());
            factory.getCurrentSession().delete(period);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public FiscalPeriod findPeriod(FiscalPeriod aPeriod) {
        FiscalPeriod period = (FiscalPeriod) factory.getCurrentSession()
                .createCriteria(FiscalPeriod.class)
                .add(Restrictions.eq("year", aPeriod.getYear()))
                .add(Restrictions.eq("month", aPeriod.getMonth()))
                .uniqueResult();
        return period;
    }

}
