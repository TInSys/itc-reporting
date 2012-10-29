package com.tinsys.itc_reporting.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Preferences;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

@Repository
public class PreferencesDAOImpl implements PreferencesDAO {

   private SessionFactory factory;

   public SessionFactory getFactory() {
      return factory;
   }

   public void setFactory(SessionFactory factory) {
      this.factory = factory;
   }

   @Override
   public PreferencesDTO createPreference(PreferencesDTO aPreferences) {
      Preferences preferences = new Preferences();
      preferences.setReferenceCurrency(aPreferences.getReferenceCurrency());
      factory.getCurrentSession().persist(preferences);
      return aPreferences;
   }

   @Override
   public PreferencesDTO updatePreference(PreferencesDTO aPreferences) {
      Preferences preferences = (Preferences) factory.getCurrentSession()
            .createCriteria(Preferences.class).uniqueResult();
      preferences.setReferenceCurrency(aPreferences.getReferenceCurrency());
      factory.getCurrentSession().update(preferences);
      return aPreferences;
   }

   @Override
   public PreferencesDTO findPreference(PreferencesDTO aPreferences) {
      Preferences preferences = (Preferences) factory.getCurrentSession()
            .createCriteria(Preferences.class).uniqueResult();
      PreferencesDTO result = new PreferencesDTO();
      result.setReferenceCurrency(preferences.getReferenceCurrency());
      return result;
   }

}
