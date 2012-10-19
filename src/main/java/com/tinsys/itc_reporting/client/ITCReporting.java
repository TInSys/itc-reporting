package com.tinsys.itc_reporting.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tinsys.itc_reporting.client.widgets.FXRateManagement;
import com.tinsys.itc_reporting.client.widgets.TaxManagement;
import com.tinsys.itc_reporting.client.widgets.WidgetSwitchManagement;
import com.tinsys.itc_reporting.client.widgets.ZoneManagement;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ITCReporting implements EntryPoint {

    @UiField
    ScrollPanel mainPanel;
    
    private static final Binder binder = GWT.create(Binder.class);
    
    @UiTemplate("ITCReporting.ui.xml")
    interface Binder extends UiBinder<Widget, ITCReporting> {
    }
    
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
      RootLayoutPanel.get().add(binder.createAndBindUi(this));
  }
  
  @UiHandler("zoneManagementPushButton")
  void handleClickZoneManagementPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new ZoneManagement());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new ZoneManagement());
      }
  }
  
  @UiHandler("taxManagementPushButton")
  void handleClickTaxManagementPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new TaxManagement());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new TaxManagement());
      }
  }

  @UiHandler("FXRateManagementPushButton")
  void handleClickFXRateManagementPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new FXRateManagement());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new FXRateManagement());
      }
  }
  
  
  private void showSaveAlert() {
      final DialogBox simplePopup = new DialogBox(true);
      simplePopup.setWidth("500px");
      simplePopup.setText("!");
      VerticalPanel dialogContent = new VerticalPanel();
      dialogContent.add(new HTML("Please save or cancel your changes before switching to another screen"));
      simplePopup.center();
      simplePopup.show();
      simplePopup.setAutoHideEnabled(true);
      Button closeButton = new Button("Close", new ClickHandler() {
          public void onClick(ClickEvent arg0) {
              simplePopup.hide();
          }
      });
      dialogContent.add(closeButton);
      simplePopup.add(dialogContent);
  }
  
}
