package com.tinsys.itc_reporting.client.widgets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.tinsys.itc_reporting.client.widgets.utils.CustomTextInputCell;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class FXRateManagementByMonth extends Composite implements
      WidgetSwitchManagement {

   private static Binder uiBinder = GWT.create(Binder.class);
   private FXRateServiceAsync fxRateService = GWT.create(FXRateService.class);
   private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
   private int currentPage = 0;
   private boolean editionInProgress;
   private PreferencesDTO prefs;

   private List<FXRateDTO> fxRateList;
   private static int STARTING_YEAR = 2008;
   private int currentYear;
   private int currentMonth;
   private List<FXRateDTO> errorList = new ArrayList<FXRateDTO>();

   SimplePager.Resources pagerResources = GWT
         .create(SimplePager.Resources.class);

   @UiField
   SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
         pagerResources, false, 0, true);

   ListDataProvider<FXRateDTO> provider = new ListDataProvider<FXRateDTO>();
   final TextColumn<FXRateDTO> fxRateZoneColumn = new TextColumn<FXRateDTO>() {

      @Override
      public String getValue(FXRateDTO fxRateDTO) {
         return fxRateDTO.getZone().getCode() + " "
               + fxRateDTO.getZone().getName();
      }
   };
   CustomTextInputCell fxRateRateCell = new CustomTextInputCell();

   final Column<FXRateDTO, String> fxRateRateColumn = new Column<FXRateDTO, String>(
         fxRateRateCell) {

      @Override
      public String getValue(FXRateDTO fxRateDTO) {
         return (fxRateDTO.getRate() != null) ? fxRateDTO.getRate().toString()
               : "0";
      }
   };
   CustomTextInputCell fxRateISOCurrencyCell = new CustomTextInputCell();
   final Column<FXRateDTO, String> fxRateISOCurrencyColumn = new Column<FXRateDTO, String>(
         fxRateISOCurrencyCell) {

      @Override
      public String getValue(FXRateDTO fxRateDTO) {
         if (fxRateDTO.getCurrencyIso() == null) {
            fxRateDTO.setCurrencyIso(prefs.getReferenceCurrency());
         }
         return fxRateDTO.getCurrencyIso();
      }
   };
   final Column<FXRateDTO, String> deleteFXRateColumn = new Column<FXRateDTO, String>(
         new ButtonCell()) {

      @Override
      public String getValue(FXRateDTO fxRateDTO) {
         return "Delete";
      }
   };
   @UiField
   CellTable<FXRateDTO> fxRateCellTable = new CellTable<FXRateDTO>();
   @UiField
   ListBox monthPeriodListBox = new ListBox();

   @UiField
   ListBox yearPeriodListBox = new ListBox();

   @UiField
   Button saveFXRate = new Button();

   @UiField
   Button cancelUpdateFXRate = new Button();
   protected ArrayList<ZoneDTO> zoneList;

   @UiTemplate("FXRateManagementByMonth.ui.xml")
   interface Binder extends UiBinder<Widget, FXRateManagementByMonth> {
   }

   public PreferencesDTO getPrefs() {
      return prefs;
   }

   public void setPrefs(PreferencesDTO prefs) {
      this.prefs = prefs;
   }

   public FXRateManagementByMonth() {
      initWidget(uiBinder.createAndBindUi(this));
      fxRateISOCurrencyCell.setMaxLength("3");
      fxRateRateCell.setMaxLength("15");
      for (int i = 1; i <= 12; i++) {
         monthPeriodListBox.addItem(Integer.toString(i));
      }
      for (int i = STARTING_YEAR; i <= 2040; i++) {
         yearPeriodListBox.addItem(Integer.toString(i));
      }
      Date today = new Date();
      currentYear = new Integer(DateTimeFormat.getFormat("yyyy").format(today));
      currentMonth = new Integer(DateTimeFormat.getFormat("MM").format(today));
      yearPeriodListBox.setSelectedIndex(currentYear - STARTING_YEAR);
      monthPeriodListBox.setSelectedIndex(currentMonth - 1);
      monthPeriodListBox.addChangeHandler(new ChangeHandler() {

         @Override
         public void onChange(ChangeEvent event) {
            currentMonth = monthPeriodListBox.getSelectedIndex() + 1;
            getFXRateList();
         }
      });

      yearPeriodListBox.addChangeHandler(new ChangeHandler() {

         @Override
         public void onChange(ChangeEvent event) {
            currentYear = yearPeriodListBox.getSelectedIndex() + STARTING_YEAR;
            getFXRateList();
         }
      });
      zoneService.getAllZones(new AsyncCallback<ArrayList<ZoneDTO>>() {

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error fetching zone list :  " + caught.getMessage());
         }

         @Override
         public void onSuccess(ArrayList<ZoneDTO> result) {
            zoneList = result;
            getFXRateList();
         }
      });

      fxRateCellTable.setTableLayoutFixed(true);
      fxRateCellTable.addColumn(fxRateZoneColumn, "Zone ");
      fxRateCellTable.addColumn(fxRateRateColumn, "Rate ");
      fxRateCellTable.addColumn(fxRateISOCurrencyColumn, "Currency ISO");
      fxRateCellTable.setColumnWidth(fxRateISOCurrencyColumn, "50px");
      fxRateCellTable.setPageSize(15);
      pager.setDisplay(fxRateCellTable);
      pager.setRangeLimited(true);
      fxRateCellTable
            .setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

      provider.addDataDisplay(fxRateCellTable);

      fxRateRateColumn.setFieldUpdater(new FieldUpdater<FXRateDTO, String>() {

         @Override
         public void update(int index, FXRateDTO object, String value) {
            editionInProgress = true;
            cancelUpdateFXRate.setVisible(true);
            if (value.matches("\\-?[0-9]*\\.?[0-9]*")) {
               object.setRate(new BigDecimal(value));
               errorList.remove(object);
            } else {
               errorList.add(object);
            }
         }
      });

      fxRateISOCurrencyColumn
            .setFieldUpdater(new FieldUpdater<FXRateDTO, String>() {

               @Override
               public void update(int index, FXRateDTO object, String value) {
                  editionInProgress = true;
                  cancelUpdateFXRate.setVisible(true);
                  object.setCurrencyIso(value);
               }
            });
   }

   private void getFXRateList() {
      FiscalPeriodDTO monthPeriodDto = new FiscalPeriodDTO();
      monthPeriodDto.setId(null);
      monthPeriodDto.setMonth(currentMonth);
      monthPeriodDto.setYear(currentYear);
      fxRateService.getAllFXRatesForPeriod(monthPeriodDto,
            new AsyncCallback<ArrayList<FXRateDTO>>() {

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error fetching monthly FX rates list :  "
                        + caught.getMessage());
               }

               @Override
               public void onSuccess(ArrayList<FXRateDTO> result) {

                  if (currentPage == 999) {
                     currentPage = ((result.size() - 1) < 0) ? 0 : (result
                           .size() - 1) / 15;
                  }
                  provider.getList().clear();
                  provider.getList().addAll(result);
                  fxRateList = result;
                  fxRateCellTable.setRowCount(result.size());
                  pager.setPage(currentPage);
               }
            });
   }

   @UiHandler("cancelUpdateFXRate")
   void handleClickCancel(ClickEvent e) {
      currentPage = 999;
      getFXRateList();
      editionInProgress = false;
      cancelUpdateFXRate.setVisible(false);
      errorList.clear();
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
      if (errorList.size() == 0) {
         fxRateService.saveOrUpdate(this.fxRateList, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
               Window.alert("Error saving/updating  fxRates :"
                     + caught.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
               getFXRateList();
               editionInProgress = false;
               cancelUpdateFXRate.setVisible(false);
            }
         });
      } else {
         List<String> dummyList = new ArrayList<String>();
         for (FXRateDTO fxRateDTO : errorList) {
            dummyList
                  .add("Rate "
                        + fxRateDTO.getRate()
                        + " at line "
                        + (fxRateList.indexOf(fxRateDTO) + 1)
                        + " could not be updated to its new value because it's not a number. Save action was cancelled. Please correct the value or cancel. ");
         }
         showAlert(dummyList);

      }
   }
}
