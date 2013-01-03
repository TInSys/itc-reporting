package com.tinsys.itc_reporting.dao;

import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

public interface PreferencesDAO {

  public PreferencesDTO createPreference(PreferencesDTO aPreferences);

  public PreferencesDTO updatePreference(PreferencesDTO aPreferences);

  public PreferencesDTO findPreference(PreferencesDTO aPreferences);
}
