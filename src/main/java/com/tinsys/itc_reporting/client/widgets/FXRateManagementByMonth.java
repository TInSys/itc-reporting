package com.tinsys.itc_reporting.client.widgets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.tinsys.itc_reporting.client.service.FXRateService;
import com.tinsys.itc_reporting.client.service.FXRateServiceAsync;
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.client.service.ZoneServiceAsync;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.MonthPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class FXRateManagementByMonth extends Composite implements
      WidgetSwitchManagement {

   private static Binder uiBinder = GWT.create(Binder.class);
   private FXRateServiceAsync fxRateService = GWT.create(FXRateService.class);
   private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
   private FXRateDTO selectedFXRate;
   private int currentPage = 0;
   private boolean editionInProgress;

   private ZoneDTO currentZone;
   private List<FXRateDTO> fxRateList;
   private static int STARTING_YEAR = 2008;
   private int currentYear;
   private int currentMonth;

   SimplePager.Resources pagerResources = GWT
         .create(SimplePager.Resources.class);

   @UiField
   SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
         pagerResources, false, 0, true);

   ListDataProvider<FXRateDTO> provider = new ListDataProvider<FXRateDTO>();
   final TextColumn<FXRateDTO> fxRateZoneColumn = new TextColumn<FXRateDTO>() {

      @Override
      public String getValue(FXRateDTO fxRateDTO) {
         return fxRateDTO.getZone().getCode()+" "+fxRateDTO.getZone().getName();
      }
   };
   final Column<FXRateDTO, String> fxRateRateColumn = new Column<FXRateDTO, String>(
         new CustomTextInputCell()) {

      @Override
      public String getValue(FXRateDTO fxRateDTO) {
         return fxRateDTO.getRate().toString();
      }
   };

   @UiField
   CellTable<FXRateDTO> fxRateCellTable = new CellTable<FXRateDTO>();
   @UiField
   ListBox monthPeriod = new ListBox();

   @UiField
   ListBox yearPeriod = new ListBox();

   @UiField
   Button saveFXRate = new Button();

   @UiField
   Button deleteFXRate = new Button();

   @UiField
   Button cancelUpdateFXRate = new Button();
   protected ArrayList<ZoneDTO> zoneList;

   @UiTemplate("FXRateManagementByMonth.ui.xml")
   interface Binder extends UiBinder<Widget, FXRateManagementByMonth> {
   }

   public FXRateManagementByMonth() {
      initWidget(uiBinder.createAndBindUi(this));
      for (int i = 1; i <= 12; i++) {
         monthPeriod.addItem(Integer.toString(i));
      }
      for (int i = STARTING_YEAR; i <= 2040; i++) {
         yearPeriod.addItem(Integer.toString(i));
      }
      Date today = new Date();
      currentYear = new Integer(DateTimeFormat.getFormat("yyyy").format(today));
      currentMonth = new Integer(DateTimeFormat.getFormat("MM").format(today));
      yearPeriod.setSelectedIndex(currentYear-STARTING_YEAR);
      monthPeriod.setSelectedIndex(currentMonth-1);
      zoneService.getAllZones(new AsyncCallback<ArrayList<ZoneDTO>>() {

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error fetching zone list :  " + caught.getMessage());
         }

         @Override
         public void onSuccess(ArrayList<ZoneDTO> result) {
            zoneList = result;
            MonthPeriodDTO monthPeriodDto = new MonthPeriodDTO();
            monthPeriodDto.setId(null);
            monthPeriodDto.setMonth(currentMonth);
            monthPeriodDto.setYear(currentYear);
            fxRateService.getAllFXRatesForPeriod(monthPeriodDto, new AsyncCallback<ArrayList<FXRateDTO>>() {

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error fetching monthly FX rates list :  " + caught.getMessage());                  
               }

               @Override
               public void onSuccess(ArrayList<FXRateDTO> result) {
                  
                  if (currentPage == 999) {
                     currentPage = ((result.size() - 1) < 0) ? 0 : (result
                           .size() - 1) / 10;
                  }
                  provider.getList().clear();
                  provider.getList().addAll(result);
                  fxRateList = result;
                  fxRateCellTable.setRowCount(result.size());
                  pager.setPage(currentPage);
               }
            });
         }
      });

      fxRateCellTable.setTableLayoutFixed(true);
      fxRateCellTable.addColumn(fxRateZoneColumn, "Zone ");
      fxRateCellTable.addColumn(fxRateRateColumn, "Period ");
      fxRateCellTable.setPageSize(15);
      pager.setDisplay(fxRateCellTable);
      pager.setRangeLimited(true);
      fxRateCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

      provider.addDataDisplay(fxRateCellTable);

      fxRateRateColumn.setFieldUpdater(new FieldUpdater<FXRateDTO, String>() {

         @Override
         public void update(int index, FXRateDTO object, String value) {
            editionInProgress = true;
            cancelUpdateFXRate.setVisible(true);
            object.setRate(new BigDecimal(value));
         }
      });
   }

   private void getFXRateList() {
      fxRateService.getAllFXRates(currentZone,
            new AsyncCallback<ArrayList<FXRateDTO>>() {

               @Override
               public void onSuccess(ArrayList<FXRateDTO> result) {
                  if (currentPage == 999) {
                     currentPage = ((result.size() - 1) < 0) ? 0 : (result
                           .size() - 1) / 10;
                  }
                  provider.getList().clear();
                  provider.getList().addAll(result);
                  fxRateList = result;
                  fxRateCellTable.setRowCount(result.size());
                  pager.setPage(currentPage);
               }

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error fetching fxRate list :  "
                        + caught.getMessage());
               }
            });
   }

   private void createFXRate() {
      fxRateService.createFXRate(selectedFXRate,
            new AsyncCallback<FXRateDTO>() {

               @Override
               public void onSuccess(FXRateDTO result) {
                  currentPage = 999;
                  getFXRateList();
                  monthPeriod.setItemSelected(monthPeriod.getSelectedIndex(),
                        false);
                  yearPeriod.setItemSelected(currentYear - STARTING_YEAR, true);

               }

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error saving new fxRate :"
                        + caught.getMessage());
               }
            });
   }

   @UiHandler("cancelUpdateFXRate")
   void handleClickCancel(ClickEvent e) {
      currentPage = 999;
   }

   @UiHandler("deleteFXRate")
   void handleClickDelete(ClickEvent e) {
      deletePredefinedComment();
      currentPage = 999;
   }

   private void deletePredefinedComment() {
      fxRateService.deleteFXRate(selectedFXRate, new AsyncCallback<Void>() {

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error deleting fxRate :" + caught.getMessage());

         }

         @Override
         public void onSuccess(Void result) {
            currentPage = 999;
            getFXRateList();
         }
      });
   }

   @Override
   public boolean isEditing() {
      return this.editionInProgress;
   }

   private void showAlert(List<String> content) {
      if (content.size() > 0) {
         final DialogBox simplePopup = new DialogBox(true);
         simplePopup.setWidth("500px");
         simplePopup.setText("Error !");
         VerticalPanel dialogContent = new VerticalPanel();
         for (String line : content) {
            dialogContent.add(new HTML(line));
         }
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
   
   @UiHandler("saveFXRate")
   void handleClickSave(ClickEvent e) {
    Window.alert("test");  
   }
   }

