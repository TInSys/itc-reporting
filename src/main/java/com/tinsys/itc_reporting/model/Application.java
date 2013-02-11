package com.tinsys.itc_reporting.model;

import java.io.Serializable;

public class Application implements Serializable {

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
  public String toString() {
    return this.getVendorID();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && (obj.getClass().equals(this.getClass()))) {
      Application app = (Application) obj;
      return this.id == app.getId();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(this.id).hashCode();
  }
  
}
