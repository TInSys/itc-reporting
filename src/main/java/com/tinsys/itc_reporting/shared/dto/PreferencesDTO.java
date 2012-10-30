package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;

public class PreferencesDTO implements Serializable {

   /**
     * 
     */
   private static final long serialVersionUID = 1L;
   private Long id;
   private String referenceCurrency;

   public Long getId() {
       return id;
   }
   public void setId(Long id) {
       this.id = id;
   }
   public void setReferenceCurrency(String referenceCurrency) {
      this.referenceCurrency = referenceCurrency;
   }

   public String getReferenceCurrency() {
      return referenceCurrency;
   }

}
