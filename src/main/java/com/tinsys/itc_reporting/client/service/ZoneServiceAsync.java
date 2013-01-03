package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface ZoneServiceAsync {

  void createZone(ZoneDTO aZone, AsyncCallback<ZoneDTO> callback);

  void deleteZone(ZoneDTO aZone, AsyncCallback<Void> callback);

  void findZone(Long id, AsyncCallback<ZoneDTO> callback);

  void getAllZones(AsyncCallback<ArrayList<ZoneDTO>> callback);

  void updateZone(ZoneDTO aZone, AsyncCallback<ZoneDTO> callback);

}
