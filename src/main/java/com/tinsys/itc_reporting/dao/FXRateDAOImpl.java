package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Period;
import com.tinsys.itc_reporting.model.FXRate;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DateUtils;
import com.tinsys.itc_reporting.shared.dto.MonthPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Repository
public class FXRateDAOImpl implements FXRateDAO {

    private SessionFactory factory;

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
            fxRateDTO.setCurrencyISO(fxRate.getCurrencyISO());
            fxRateDTO.setZone(zoneDTO);
            MonthPeriodDTO periodDTO = new MonthPeriodDTO();
            periodDTO.setId(fxRate.getPeriod().getId());
            Calendar cal = new GregorianCalendar();
            cal.setTime(fxRate.getPeriod().getStartDate());
            
            periodDTO.setMonth(cal.get(Calendar.MONTH));
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
        fxRate.setCurrencyISO(aFXRate.getCurrencyISO());
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
        fxRate.setCurrencyISO(aFXRate.getCurrencyISO());
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
        fxRate.setCurrencyISO(aFXRate.getCurrencyISO());
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

}
