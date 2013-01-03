package com.tinsys.itc_reporting.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

public interface PreferencesServiceAsync {

  void createPreference(PreferencesDTO aPreferences, AsyncCallback<PreferencesDTO> callback);

  void findPreference(PreferencesDTO aPreferences, AsyncCallback<PreferencesDTO> callback);

  void updatePreference(PreferencesDTO aPreferences, AsyncCallback<PreferencesDTO> callback);

}
