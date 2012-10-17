package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface ZoneDAO {

    public ArrayList<ZoneDTO> getAllZones();

    public ZoneDTO findZone(Long id);

    public ZoneDTO createZone(ZoneDTO aZone);

    public ZoneDTO updateZone(ZoneDTO aZone);

    public void deleteZone(ZoneDTO aZone);
}
