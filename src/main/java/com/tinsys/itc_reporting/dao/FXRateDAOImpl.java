package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.FXRate;
import com.tinsys.itc_reporting.model.Period;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DateUtils;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.MonthPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Repository
public class FXRateDAOImpl implements FXRateDAO {

    private SessionFactory factory;
    private MonthPeriodDTO monthlyPeriod;
    
    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<FXRateDTO> getAllFXRates(ZoneDTO zoneDTO) {
        ArrayList<FXRateDTO> result = new ArrayList<FXRateDTO>();
        Zone zone = new Zone();
        zone.setId(zoneDTO.getId());
        zone.setCode(zoneDTO.getCode());
        zone.setName(zoneDTO.getName());
        zone.setCurrencyISO(zoneDTO.getCurrencyISO());

        ArrayList<FXRate> fxRateList;
        try {
            Criteria criteria = factory.getCurrentSession().createCriteria(
                    FXRate.class);
            Criteria subCriteria = criteria.createCriteria("period");
            subCriteria.addOrder(Order.asc("startDate"));
            fxRateList = (ArrayList<FXRate>) criteria.add(
                    Restrictions.eq("zone", zone)).list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (FXRate fxRate : fxRateList) {
            FXRateDTO fxRateDTO = new FXRateDTO();
            fxRateDTO.setId(fxRate.getId());
            fxRateDTO.setRate(fxRate.getRate());
            fxRateDTO.setZone(zoneDTO);
            MonthPeriodDTO periodDTO = new MonthPeriodDTO();
            periodDTO.setId(fxRate.getPeriod().getId());
            Calendar cal = new GregorianCalendar();
            cal.setTime(fxRate.getPeriod().getStartDate());
            
            periodDTO.setMonth(cal.get(Calendar.MONTH)+1);
            periodDTO.setYear(cal.get(Calendar.YEAR));
            fxRateDTO.setPeriod(periodDTO);
            result.add(fxRateDTO);
        }

        return result;
    }

    @Override
    public FXRateDTO findFXRate(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FXRateDTO createFXRate(FXRateDTO aFXRate) {

        FXRate fxRate = new FXRate();
        fxRate.setRate(aFXRate.getRate());
        Zone zone = new Zone();
        zone.setId(aFXRate.getZone().getId());
        zone.setCode(aFXRate.getZone().getCode());
        zone.setName(aFXRate.getZone().getName());
        zone.setCurrencyISO(aFXRate.getZone().getCurrencyISO());
        fxRate.setZone(zone);
        PeriodDTO periodDTO = DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear());    
        Period period = new Period();
        period.setStartDate(periodDTO.getStartDate());
        period.setStopDate(periodDTO.getStopDate());
        period.setId(aFXRate.getPeriod().getId());
        fxRate.setPeriod(period);
        try {
            factory.getCurrentSession().persist(fxRate);
            Long id = (Long) factory.getCurrentSession().getIdentifier(fxRate);
            aFXRate.setId(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return aFXRate;
    }

    @Override
    public FXRateDTO updateFXRate(FXRateDTO aFXRate) {
        FXRate fxRate = (FXRate) factory.getCurrentSession()
                .get(FXRate.class, aFXRate.getId());
        fxRate.setRate(aFXRate.getRate());
        Zone zone = new Zone();
        zone.setId(aFXRate.getZone().getId());
        zone.setCode(aFXRate.getZone().getCode());
        zone.setName(aFXRate.getZone().getName());
        zone.setCurrencyISO(aFXRate.getZone().getCurrencyISO());
        fxRate.setZone(zone);
        PeriodDTO periodDTO = DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear());    
        Period period = new Period();
        period.setStartDate(periodDTO.getStartDate());
        period.setStopDate(periodDTO.getStopDate());
        period.setId(aFXRate.getPeriod().getId());
        fxRate.setPeriod(period);
        factory.getCurrentSession().saveOrUpdate(fxRate);
        return aFXRate;
    }

    @Override
    public void deleteFXRate(FXRateDTO aFXRate) {
        FXRate fxRate = (FXRate) factory.getCurrentSession()
                .get(FXRate.class, aFXRate.getId());
        fxRate.setRate(aFXRate.getRate());
        Zone zone = new Zone();
        zone.setId(aFXRate.getZone().getId());
        zone.setCode(aFXRate.getZone().getCode());
        zone.setName(aFXRate.getZone().getName());
        zone.setCurrencyISO(aFXRate.getZone().getCurrencyISO());
        fxRate.setZone(zone);
        PeriodDTO periodDTO = DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear());    
        Period period = new Period();
        period.setStartDate(periodDTO.getStartDate());
        period.setStopDate(periodDTO.getStopDate());
        period.setId(aFXRate.getPeriod().getId());
        factory.getCurrentSession().delete(fxRate);

    }

   @SuppressWarnings("unchecked")
   @Override
   public ArrayList<FXRateDTO> getAllFXRatesForPeriod(
         PeriodDTO monthYearToPeriod) {
      monthlyPeriod = null;
      ArrayList<FXRateDTO> result = new ArrayList<FXRateDTO>();
      ArrayList<ZoneDTO> zoneDTOList = new ArrayList<ZoneDTO>();
      ArrayList<FXRate> fxRateList;
      
      // fetch existing zones
      ArrayList<Zone> dbZoneList;
      dbZoneList = (ArrayList<Zone>) factory.getCurrentSession().createCriteria(Zone.class).list();
      //for each zone, add an entry in result table
      for (Zone zone : dbZoneList) {
          ZoneDTO zoneDTO = new ZoneDTO();
          zoneDTO.setId(zone.getId());
          zoneDTO.setCode(zone.getCode());
          zoneDTO.setName(zone.getName());
          zoneDTO.setCurrencyISO(zone.getCurrencyISO());
          zoneDTOList.add(zoneDTO);
      }
      // fetch fx rate for given month
      try {
          Query query = factory.getCurrentSession().createQuery("select fx from FXRate fx left join fx.zone Zone where fx.period.startDate = :sDate ");
          query.setParameter("sDate", monthYearToPeriod.getStartDate());
          fxRateList = (ArrayList<FXRate>) query.list();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
      // add rates to corresponding lines of result table.

      for (FXRate fxRate : fxRateList) {
          ZoneDTO zoneDTO = new ZoneDTO();
          zoneDTO.setCode(fxRate.getZone().getCode());
          zoneDTO.setName(fxRate.getZone().getName());
          zoneDTO.setCurrencyISO(fxRate.getZone().getCurrencyISO());
          zoneDTO.setId(fxRate.getZone().getId());
          zoneDTOList.remove(zoneDTO);
          FXRateDTO fxRateDTO = new FXRateDTO();
          fxRateDTO.setId(fxRate.getId());
          fxRateDTO.setRate(fxRate.getRate());

          fxRateDTO.setZone(zoneDTO);
          if (monthlyPeriod == null){
              monthlyPeriod = new MonthPeriodDTO();
              monthlyPeriod.setId(fxRate.getPeriod().getId());
              Calendar cal = new GregorianCalendar();
              cal.setTime(fxRate.getPeriod().getStartDate());
              monthlyPeriod.setMonth(cal.get(Calendar.MONTH)+1);
              monthlyPeriod.setYear(cal.get(Calendar.YEAR));
          }
          fxRateDTO.setPeriod(monthlyPeriod);
          result.add(fxRateDTO);
      }
      if (monthlyPeriod == null){
          monthlyPeriod = new MonthPeriodDTO();
          monthlyPeriod.setId(monthYearToPeriod.getId());
          Calendar cal = new GregorianCalendar();
          cal.setTime(monthYearToPeriod.getStartDate());
          monthlyPeriod.setMonth(cal.get(Calendar.MONTH)+1);
          monthlyPeriod.setYear(cal.get(Calendar.YEAR));
      }
      for (ZoneDTO zoneDTO : zoneDTOList) {
          FXRateDTO fxRateDTO = new FXRateDTO();
          fxRateDTO.setZone(zoneDTO);
          fxRateDTO.setPeriod(monthlyPeriod);
        result.add(fxRateDTO);
    }
      Collections.sort(result);
      return result;
   }

@Override
public void saveOrUpdate(List<FXRateDTO> fxRateList) {
    for (FXRateDTO fxRateDTO : fxRateList) {
        FXRate fxRate = new FXRate();
        fxRate.setId(fxRateDTO.getId());
        fxRate.setRate(fxRateDTO.getRate());
        Zone zone = new Zone();
        zone.setId(fxRateDTO.getZone().getId());
        zone.setCode(fxRateDTO.getZone().getCode());
        zone.setCurrencyISO(fxRateDTO.getZone().getCurrencyISO());
        zone.setName(fxRateDTO.getZone().getName());
        fxRate.setZone(zone);
        Period period = new Period();

        PeriodDTO periodDTO = DateUtils.monthYearToPeriod(fxRateDTO.getPeriod().getId(), fxRateDTO.getPeriod().getMonth(), fxRateDTO.getPeriod().getYear());
        period.setId(periodDTO.getId());
        period.setStartDate(periodDTO.getStartDate());
        period.setStopDate(periodDTO.getStopDate());
        if (period.getId()==null){
            factory.getCurrentSession().save(period);
            period.setId((Long) factory.getCurrentSession().getIdentifier(period));
        }
        fxRate.setPeriod(period);
        
        factory.getCurrentSession().saveOrUpdate(fxRate);
    }
    
}

}
