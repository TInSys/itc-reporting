package com.tinsys.itc_reporting.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ITCReporting implements EntryPoint {

//    private static final Binder binder = GWT.create(Binder.class);
//    private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
    
    @UiTemplate("ITCReporting.ui.xml")
    interface Binder extends UiBinder<Widget, ITCReporting> {
    }
    
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

  }
}
