package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;

public class ApplicationDTO implements Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  private Long id;
  private String vendorID;
  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVendorID() {
    return vendorID;
  }

  public void setVendorID(String vendorID) {
    this.vendorID = vendorID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && (obj.getClass().equals(this.getClass()))) {
      ApplicationDTO appDTO = (ApplicationDTO) obj;
      return this.id == appDTO.getId();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(this.id).hashCode();
  }
}
