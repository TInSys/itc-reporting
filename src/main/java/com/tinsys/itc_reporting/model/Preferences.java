package com.tinsys.itc_reporting.model;

import java.io.Serializable;

public class Preferences implements Serializable {

   /**
     * 
     */
   private static final long serialVersionUID = 1L;
   private String referenceCurrency;

   public void setReferenceCurrency(String referenceCurrency) {
      this.referenceCurrency = referenceCurrency;
   }

   public String getReferenceCurrency() {
      return referenceCurrency;
   }

}
