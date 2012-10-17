package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@RemoteServiceRelativePath("springGwtServices/zoneService")
public interface ZoneService extends RemoteService {

    public ArrayList<ZoneDTO> getAllZones();

    public ZoneDTO findZone(Long id);

    public ZoneDTO createZone(ZoneDTO aZone);

    public ZoneDTO updateZone(ZoneDTO aZone);

    public void deleteZone(ZoneDTO aZone);
}
