package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class ZoneDAOTest implements ZoneDAO {

  private List<ZoneDTO> zoneList = new ArrayList<ZoneDTO>();

  
  public ZoneDAOTest() {
   ZoneDTO zone = new ZoneDTO();
   zone.setId(0L);
   zone.setCode("ZONE0");
   zone.setCurrencyISO("USD");
   zone.setName("ZONE0");
   zoneList.add(zone);
   
   zone = new ZoneDTO();
   zone.setId(1L);
   zone.setCode("ZONE1");
   zone.setCurrencyISO("EUR");
   zone.setName("ZONE1");
   zoneList.add(zone);

   zone = new ZoneDTO();
   zone.setId(2L);
   zone.setCode("ZONE2");
   zone.setCurrencyISO("EUR");
   zone.setName("ZONE2");
   zoneList.add(zone);

   zone = new ZoneDTO();
   zone.setId(3L);
   zone.setCode("ZONE3");
   zone.setCurrencyISO("EUR");
   zone.setName("ZONE3");
   zoneList.add(zone);


   zone = new ZoneDTO();
   zone.setId(4L);
   zone.setCode("ZONE4");
   zone.setCurrencyISO("EUR");
   zone.setName("ZONE4");
   zoneList.add(zone);
  }
  @Override
  public ArrayList<ZoneDTO> getAllZones() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZoneDTO findZone(Long id) {
    for (ZoneDTO zoneDTO : this.zoneList) {
      if (zoneDTO.getId()==id){
        return zoneDTO;
      }
    }
    return null;
  }

  @Override
  public ZoneDTO createZone(ZoneDTO aZone) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZoneDTO updateZone(ZoneDTO aZone) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteZone(ZoneDTO aZone) {
    // TODO Auto-generated method stub

  }

  @Override
  public Zone findZoneByCode(String code) {
    for (ZoneDTO zoneDTO : this.zoneList) {
      if (zoneDTO.getCode().equals(code)){
        return DTOUtils.zoneDTOToZone(zoneDTO);
      }
    }
    return null;
  }

}
