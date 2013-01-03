package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.FXRate;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Repository
public class FXRateDAOImpl implements FXRateDAO {

  private SessionFactory factory;
  private FiscalPeriodDTO monthlyPeriod;

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
    Zone zone = DTOUtils.zoneDTOToZone(zoneDTO);

    ArrayList<FXRate> fxRateList;
    try {
      Criteria criteria = factory.getCurrentSession().createCriteria(FXRate.class);
      Criteria subCriteria = criteria.createCriteria("period");
      subCriteria.addOrder(Order.asc("year"));
      subCriteria.addOrder(Order.asc("month"));
      fxRateList = (ArrayList<FXRate>) criteria.add(Restrictions.eq("zone", zone)).list();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    for (FXRate fxRate : fxRateList) {
      FXRateDTO fxRateDTO = new FXRateDTO();
      fxRateDTO.setId(fxRate.getId());
      fxRateDTO.setRate(fxRate.getRate());
      fxRateDTO.setCurrencyIso(fxRate.getCurrencyIso());
      fxRateDTO.setZone(zoneDTO);
      FiscalPeriodDTO periodDTO = new FiscalPeriodDTO();
      periodDTO = DTOUtils.periodToPeriodDTO(fxRate.getPeriod());
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
    FiscalPeriod period = new FiscalPeriod();
    period = DTOUtils.periodDTOtoPeriod(aFXRate.getPeriod());
    fxRate.setPeriod(period);
    fxRate.setCurrencyIso(aFXRate.getCurrencyIso());
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
    FXRate fxRate = (FXRate) factory.getCurrentSession().get(FXRate.class, aFXRate.getId());
    fxRate.setRate(aFXRate.getRate());
    Zone zone = new Zone();
    zone.setId(aFXRate.getZone().getId());
    zone.setCode(aFXRate.getZone().getCode());
    zone.setName(aFXRate.getZone().getName());
    zone.setCurrencyISO(aFXRate.getZone().getCurrencyISO());
    fxRate.setZone(zone);
    FiscalPeriod period = DTOUtils.periodDTOtoPeriod(aFXRate.getPeriod());
    fxRate.setPeriod(period);
    fxRate.setCurrencyIso(aFXRate.getCurrencyIso());
    factory.getCurrentSession().saveOrUpdate(fxRate);
    return aFXRate;
  }

  @Override
  public void deleteFXRate(FXRateDTO aFXRate) {
    FXRate fxRate = (FXRate) factory.getCurrentSession().get(FXRate.class, aFXRate.getId());
    fxRate.setRate(aFXRate.getRate());
    Zone zone = new Zone();
    zone.setId(aFXRate.getZone().getId());
    zone.setCode(aFXRate.getZone().getCode());
    zone.setName(aFXRate.getZone().getName());
    zone.setCurrencyISO(aFXRate.getZone().getCurrencyISO());
    fxRate.setZone(zone);
    fxRate.setCurrencyIso(aFXRate.getCurrencyIso());
    fxRate.setPeriod(DTOUtils.periodDTOtoPeriod(aFXRate.getPeriod()));
    factory.getCurrentSession().delete(fxRate);

  }

  @SuppressWarnings("unchecked")
  @Override
  public ArrayList<FXRateDTO> getAllFXRatesForPeriod(FiscalPeriodDTO monthYearToPeriod) {
    monthlyPeriod = null;
    ArrayList<FXRateDTO> result = new ArrayList<FXRateDTO>();
    ArrayList<ZoneDTO> zoneDTOList = new ArrayList<ZoneDTO>();
    ArrayList<FXRate> fxRateList;

    // fetch existing zones
    ArrayList<Zone> dbZoneList;
    dbZoneList = (ArrayList<Zone>) factory.getCurrentSession().createCriteria(Zone.class).list();
    // for each zone, add an entry in result table
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
      Query query = factory.getCurrentSession().createQuery("select fx from FXRate fx where fx.period.month = :sMonth and fx.period.year = :sYear ");
      query.setParameter("sMonth", monthYearToPeriod.getMonth());
      query.setParameter("sYear", monthYearToPeriod.getYear());
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
      fxRateDTO.setCurrencyIso(fxRate.getCurrencyIso());

      fxRateDTO.setZone(zoneDTO);
      if (monthlyPeriod == null) {
        monthlyPeriod = DTOUtils.periodToPeriodDTO(fxRate.getPeriod());
      }
      fxRateDTO.setPeriod(monthlyPeriod);
      result.add(fxRateDTO);
    }
    if (monthlyPeriod == null) {
      monthlyPeriod = monthYearToPeriod;
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
    FiscalPeriodDTO createdPeriod = null;
    for (FXRateDTO fxRateDTO : fxRateList) {
      FXRate fxRate = new FXRate();
      fxRate.setId(fxRateDTO.getId());
      fxRate.setRate(fxRateDTO.getRate());
      fxRate.setCurrencyIso(fxRateDTO.getCurrencyIso());
      Zone zone = new Zone();
      zone.setId(fxRateDTO.getZone().getId());
      zone.setCode(fxRateDTO.getZone().getCode());
      zone.setCurrencyISO(fxRateDTO.getZone().getCurrencyISO());
      zone.setName(fxRateDTO.getZone().getName());
      fxRate.setZone(zone);
      FiscalPeriod period = new FiscalPeriod();
      if (createdPeriod == null) {
        createdPeriod = fxRateDTO.getPeriod();
      }
      period = DTOUtils.periodDTOtoPeriod(createdPeriod);
      if (period.getId() == null) {
        factory.getCurrentSession().save(period);
        createdPeriod.setId((Long) factory.getCurrentSession().getIdentifier(period));
        period.setId(createdPeriod.getId());
      }
      fxRate.setPeriod(period);

      factory.getCurrentSession().saveOrUpdate(fxRate);
    }

  }

}
