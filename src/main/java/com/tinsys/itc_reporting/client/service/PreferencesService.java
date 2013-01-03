package com.tinsys.itc_reporting.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

@RemoteServiceRelativePath("springGwtServices/preferencesService")
public interface PreferencesService extends RemoteService {

  public PreferencesDTO createPreference(PreferencesDTO aPreferences);

  public PreferencesDTO updatePreference(PreferencesDTO aPreferences);

  public PreferencesDTO findPreference(PreferencesDTO aPreferences);

}
