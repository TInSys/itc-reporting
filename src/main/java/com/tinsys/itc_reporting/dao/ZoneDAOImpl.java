package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ArrayList<ZoneDTO> getAllZones() {
        ArrayList<ZoneDTO> result = new ArrayList<ZoneDTO>();
        @SuppressWarnings("unchecked")
        ArrayList<Zone> zoneList = (ArrayList<Zone>) factory
                .getCurrentSession().createCriteria(Zone.class).list();
        for (Zone zone : zoneList) {
            ZoneDTO zoneDTO = new ZoneDTO();
            zoneDTO.setId(zone.getId());
            zoneDTO.setCode(zone.getCode());
            zoneDTO.setName(zoneDTO.getName());
            zoneDTO.setCurrencyISO(zone.getCurrencyISO());
            result.add(zoneDTO);
        }
        return result;
    }

    public ZoneDTO findZone(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public ZoneDTO createZone(ZoneDTO aZone) {
        // TODO Auto-generated method stub
        return null;
    }

    public ZoneDTO updateZone(ZoneDTO aZone) {
        // TODO Auto-generated method stub
        return null;
    }

    public void deleteZone(ZoneDTO aZone) {
        // TODO Auto-generated method stub

    }

}
