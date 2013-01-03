package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.dao.ZoneDAO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("zoneService")
@Transactional
public class ZoneServiceImpl implements ZoneService {

  @Autowired
  @Qualifier("zoneDAO")
  private ZoneDAO zoneDAO;

  public ArrayList<ZoneDTO> getAllZones() {
    return zoneDAO.getAllZones();
  }

  public ZoneDTO findZone(Long id) {
    return zoneDAO.findZone(id);
  }

  public ZoneDTO createZone(ZoneDTO aZone) {
    return zoneDAO.createZone(aZone);
  }

  public ZoneDTO updateZone(ZoneDTO aZone) {
    return zoneDAO.updateZone(aZone);
  }

  public void deleteZone(ZoneDTO aZone) {
    zoneDAO.deleteZone(aZone);
  }

}
