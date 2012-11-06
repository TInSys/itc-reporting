package com.tinsys.itc_reporting.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tinsys.itc_reporting.client.service.PreferencesService;
import com.tinsys.itc_reporting.client.service.PreferencesServiceAsync;
import com.tinsys.itc_reporting.client.widgets.ApplicationManagement;
import com.tinsys.itc_reporting.client.widgets.CompanyManagement;
import com.tinsys.itc_reporting.client.widgets.FXRateManagementByMonth;
import com.tinsys.itc_reporting.client.widgets.FXRateManagementByZone;
import com.tinsys.itc_reporting.client.widgets.FinancialReportFilesImporter;
import com.tinsys.itc_reporting.client.widgets.MonthlySalesReport;
import com.tinsys.itc_reporting.client.widgets.PreferencesManagement;
import com.tinsys.itc_reporting.client.widgets.TaxManagement;
import com.tinsys.itc_reporting.client.widgets.WidgetSwitchManagement;
import com.tinsys.itc_reporting.client.widgets.ZoneManagement;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ITCReporting implements EntryPoint {

    private PreferencesDTO preferences;
    private PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);

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
      preferences = new PreferencesDTO();
      preferencesService.findPreference(preferences, new AsyncCallback<PreferencesDTO>() {
        
        @Override
        public void onSuccess(PreferencesDTO result) {
            preferences = result;
            
        }
        
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Error fetching preferences :  "
                    + caught.getMessage());       
        }
    });
  }
  
  @UiHandler("preferencesPushButton")
  void handleClickPreferencesPushButton(ClickEvent e) {
      PreferencesManagement preferencesWidget = new PreferencesManagement(preferences);
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(preferencesWidget);
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(preferencesWidget);
      }
      
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

  @UiHandler("FXRateManagementByZonePushButton")
  void handleClickFXRateManagementByZonePushButton(ClickEvent e) {
     FXRateManagementByZone fXRateManagementByZone = new FXRateManagementByZone();
     fXRateManagementByZone.setPrefs(preferences);
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
             
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(fXRateManagementByZone);
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(fXRateManagementByZone);
      }
  }
  
  @UiHandler("FXRateManagementByMonthPushButton")
  void handleClickFXRateManagementByMonthPushButton(ClickEvent e) {
     FXRateManagementByMonth fXRateManagementByMonth = new FXRateManagementByMonth();
     fXRateManagementByMonth.setPrefs(preferences);
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(fXRateManagementByMonth);
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(fXRateManagementByMonth);
      }
  }
  
  @UiHandler("ApplicationManagementPushButton")
  void handleClickApplicationManagementPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new ApplicationManagement());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new ApplicationManagement());
      }
  }
  
  
  @UiHandler("CompanyManagementPushButton")
  void handleClickCompanyManagementPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new CompanyManagement());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new CompanyManagement());
      }
  }
  
  @UiHandler("importFinancialFilesPushButton")
  void handleClickImportFinancialFilesPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new FinancialReportFilesImporter());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new FinancialReportFilesImporter());
      }
  }
  
  @UiHandler("byPeriodReport")
  void handleClickByPeriodReportPushButton(ClickEvent e) {
      if (mainPanel.getWidget() != null) {
          WidgetSwitchManagement widgetStatus = (WidgetSwitchManagement) mainPanel
                  .getWidget();
          if (!widgetStatus.isEditing()) {
              mainPanel.remove(mainPanel.getWidget());
              mainPanel.add(new MonthlySalesReport());
          } else {
              showSaveAlert();
          }
      } else {
          mainPanel.add(new MonthlySalesReport());
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
