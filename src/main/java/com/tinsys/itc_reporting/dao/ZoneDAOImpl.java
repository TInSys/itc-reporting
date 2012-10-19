package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Repository
public class ZoneDAOImpl implements ZoneDAO {

    private SessionFactory factory;

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public ArrayList<ZoneDTO> getAllZones() {
        ArrayList<ZoneDTO> result = new ArrayList<ZoneDTO>();
        @SuppressWarnings("unchecked")
        ArrayList<Zone> zoneList = (ArrayList<Zone>) factory
                .getCurrentSession().createCriteria(Zone.class)
                .addOrder(Order.asc("code")).list();

        for (Zone zone : zoneList) {
            ZoneDTO zoneDTO = new ZoneDTO();
            zoneDTO.setId(zone.getId());
            zoneDTO.setCode(zone.getCode());
            zoneDTO.setName(zone.getName());
            zoneDTO.setCurrencyISO(zone.getCurrencyISO());
            result.add(zoneDTO);
        }

        return result;
    }

    public ZoneDTO findZone(Long id) {
        Zone zone = (Zone) factory.getCurrentSession().get(Zone.class, id);
        ZoneDTO result = new ZoneDTO();
        result.setId(zone.getId());
        result.setCode(zone.getCode());
        result.setName(zone.getName());
        result.setCurrencyISO(zone.getCurrencyISO());
        return result;
    }

    public ZoneDTO createZone(ZoneDTO aZone) {
        Zone zone = new Zone();
        zone.setCode(aZone.getCode());
        zone.setName(aZone.getName());
        zone.setCurrencyISO(aZone.getCurrencyISO());
        factory.getCurrentSession().persist(zone);
        Long id = (Long) factory.getCurrentSession().getIdentifier(zone);
        aZone.setId(id);
        return aZone;
    }

    public ZoneDTO updateZone(ZoneDTO aZone) {
        Zone zone = (Zone) factory.getCurrentSession().get(Zone.class,
                aZone.getId());
        zone.setCode(aZone.getCode());
        zone.setName(aZone.getName());
        zone.setCurrencyISO(aZone.getCurrencyISO());
        factory.getCurrentSession().saveOrUpdate(zone);
        return aZone;
    }

    public void deleteZone(ZoneDTO aZone) {
        Zone zone = new Zone();
        zone.setId(aZone.getId());
        zone.setCode(aZone.getCode());
        zone.setName(aZone.getName());
        zone.setCurrencyISO(aZone.getCurrencyISO());
        factory.getCurrentSession().delete(zone);
    }

}
