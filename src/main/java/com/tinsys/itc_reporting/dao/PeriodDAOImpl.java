package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.model.Period;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class PeriodDAOImpl implements PeriodDAO {

   
   private SessionFactory factory;

   public SessionFactory getFactory() {
       return factory;
   }

   public void setFactory(SessionFactory factory) {
       this.factory = factory;
   }

   

   @Transactional
   @Override
   public PeriodDTO createPeriod(PeriodDTO aPeriod) {

      Period period = new Period();
      period.setStartDate(aPeriod.getStartDate());
      period.setStopDate(aPeriod.getStopDate());
      
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
   public PeriodDTO updatePeriod(PeriodDTO aPeriod) {
      Period period = (Period) factory.getCurrentSession().get(Period.class, aPeriod.getId());
      period.setStartDate(aPeriod.getStartDate());
      period.setStopDate(aPeriod.getStopDate());
      factory.getCurrentSession().saveOrUpdate(period);
      return aPeriod;
   }

   @Override
   public void deletePeriod(PeriodDTO aPeriod) {
      // TODO Auto-generated method stub

   }

}
