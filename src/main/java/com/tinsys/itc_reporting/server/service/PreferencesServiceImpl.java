package com.tinsys.itc_reporting.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.PreferencesService;
import com.tinsys.itc_reporting.dao.PreferencesDAO;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

@Service("preferencesService")
@Transactional
public class PreferencesServiceImpl implements PreferencesService {

  @Autowired
  @Qualifier("preferencesDAO")
  private PreferencesDAO preferencesDAO;

  @Override
  public PreferencesDTO createPreference(PreferencesDTO aPreferences) {
    return preferencesDAO.createPreference(aPreferences);
  }

  @Override
  public PreferencesDTO updatePreference(PreferencesDTO aPreferences) {
    return preferencesDAO.updatePreference(aPreferences);
  }

  @Override
  public PreferencesDTO findPreference(PreferencesDTO aPreferences) {
    return preferencesDAO.findPreference(aPreferences);
  }

}
