package com.tinsys.itc_reporting.client.widgets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.tinsys.itc_reporting.client.service.FXRateService;
import com.tinsys.itc_reporting.client.service.FXRateServiceAsync;
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.client.service.ZoneServiceAsync;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class FXRateManagementByZone extends Composite implements WidgetSwitchManagement {

  private static Binder uiBinder = GWT.create(Binder.class);
  private FXRateServiceAsync fxRateService = GWT.create(FXRateService.class);
  private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
  private FXRateDTO selectedFXRate;
  private int currentPage = 0;
  private boolean editionInProgress;
  private String oldFXRateRateTextBoxContent;
  private String oldFXRateCurrencyTextBoxContent;
  private int oldFXRateMonthPeriodValue;
  private int oldFXRateYearPeriodValue;
  private boolean reverseSelection = false;
  private ZoneDTO currentZone;
  private List<FXRateDTO> fxRateList;
  private static int STARTING_YEAR = 2008;
  private int currentYear;
  private ZoneDTO dummyZone;
  private PreferencesDTO prefs;

  SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);

  @UiField(provided = true)
  ValueListBox<ZoneDTO> zoneListBox = new ValueListBox<ZoneDTO>(new Renderer<ZoneDTO>() {

    @Override
    public String render(ZoneDTO object) {
      if (object != null) {
        return (object.getCode() + " " + object.getName() + " " + object.getCurrencyISO());
      }
      return null;
    }

    @Override
    public void render(ZoneDTO object, Appendable appendable) throws IOException {
      String s = render(object);
      appendable.append(s);

    }
  });

  @UiField
  SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);

  ListDataProvider<FXRateDTO> provider = new ListDataProvider<FXRateDTO>();
  final Column<FXRateDTO, String> fxRateRateColumn = new Column<FXRateDTO, String>(new ClickableTextCell()) {

    @Override
    public String getValue(FXRateDTO fxRateDTO) {
      if (fxRateDTO.getRate() != null) {
        return fxRateDTO.getRate().toString();
      } else {
        fxRateDTO.setRate(new BigDecimal(0));
        return fxRateDTO.getRate().toString();
      }
    }
  };
  final Column<FXRateDTO, String> fxRatePeriodColumn = new Column<FXRateDTO, String>(new ClickableTextCell()) {

    @Override
    public String getValue(FXRateDTO fxRateDTO) {
      return fxRateDTO.getPeriod().toString();
    }
  };

  final Column<FXRateDTO, String> fxRateCurrencyColumn = new Column<FXRateDTO, String>(new ClickableTextCell()) {

    @Override
    public String getValue(FXRateDTO fxRateDTO) {
      return fxRateDTO.getCurrencyIso();
    }
  };

  @UiField
  CellTable<FXRateDTO> fxRateCellTable = new CellTable<FXRateDTO>();
  private SingleSelectionModel<FXRateDTO> selectionModel;

  @UiField
  TextBox fxRateRateTextBox = new TextBox();

  @UiField
  ListBox monthPeriod = new ListBox();

  @UiField
  ListBox yearPeriod = new ListBox();

  @UiField
  TextBox currencyISOTextBox = new TextBox();
  
  @UiField
  Button saveFXRate = new Button();

  @UiField
  Button deleteFXRate = new Button();

  @UiField
  Button cancelUpdateFXRate = new Button();

  @UiTemplate("FXRateManagementByZone.ui.xml")
  interface Binder extends UiBinder<Widget, FXRateManagementByZone> {
  }

  public void setPrefs(PreferencesDTO prefs) {
    this.prefs = prefs;
  }

  public PreferencesDTO getPrefs() {
    return prefs;
  }

  public FXRateManagementByZone() {
    initWidget(uiBinder.createAndBindUi(this));
    zoneService.getAllZones(new AsyncCallback<ArrayList<ZoneDTO>>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error fetching zone list :  " + caught.getMessage());
      }

      @Override
      public void onSuccess(ArrayList<ZoneDTO> result) {
        dummyZone = new ZoneDTO();
        dummyZone.setCode("Please choose a Zone ");
        dummyZone.setName("");
        dummyZone.setCurrencyISO("");
        dummyZone.setId(-1L);
        result.add(0, dummyZone);
        currentZone = dummyZone;
        zoneListBox.setValue(result.get(0));
        zoneListBox.setAcceptableValues(result);
      }
    });
    for (int i = 1; i <= 12; i++) {
      monthPeriod.addItem(Integer.toString(i));
    }
    for (int i = STARTING_YEAR; i <= 2040; i++) {
      yearPeriod.addItem(Integer.toString(i));
    }
    Date today = new Date();
    currentYear = new Integer(DateTimeFormat.getFormat("yyyy").format(today));

    yearPeriod.setSelectedIndex(currentYear - STARTING_YEAR);
    selectionModel = new SingleSelectionModel<FXRateDTO>();
    fxRateCellTable.setTableLayoutFixed(true);
    fxRateCellTable.addColumn(fxRateRateColumn, "Rate ");
    fxRateCellTable.addColumn(fxRatePeriodColumn, "Period ");
    fxRateCellTable.addColumn(fxRateCurrencyColumn, "Currency");
    fxRateCellTable.setSelectionModel(selectionModel);
    selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
    fxRateCellTable.setPageSize(25);
    pager.setDisplay(fxRateCellTable);
    pager.setRangeLimited(true);

    provider.addDataDisplay(fxRateCellTable);
    fxRateRateTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> arg0) {
        editionInProgress = true;
        cancelUpdateFXRate.setVisible(true);
      }
    });

    fxRateRateTextBox.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent arg0) {
        editionInProgress = true;
        cancelUpdateFXRate.setVisible(true);
        oldFXRateRateTextBoxContent = fxRateRateTextBox.getText();
      }
    });

    currencyISOTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> arg0) {
        editionInProgress = true;
        cancelUpdateFXRate.setVisible(true);
      }
    });

    currencyISOTextBox.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent arg0) {
        editionInProgress = true;
        cancelUpdateFXRate.setVisible(true);
        oldFXRateCurrencyTextBoxContent = currencyISOTextBox.getText();
      }
    });
    zoneListBox.addValueChangeHandler(new ValueChangeHandler<ZoneDTO>() {
      @Override
      public void onValueChange(ValueChangeEvent<ZoneDTO> event) {
        if (event.getValue().getId() < 0) {
          resetUpdateStatus();
        } else {
          currentZone = event.getValue();
          getFXRateList();
        }

      }
    });
  }

  private void getFXRateList() {
    fxRateService.getAllFXRates(currentZone, new AsyncCallback<ArrayList<FXRateDTO>>() {

      @Override
      public void onSuccess(ArrayList<FXRateDTO> result) {
        if (currentPage == 999) {
          currentPage = ((result.size() - 1) < 0) ? 0 : (result.size() - 1) / 10;
        }
        provider.getList().clear();
        provider.getList().addAll(result);
        fxRateList = result;
        fxRateCellTable.setRowCount(result.size());
        pager.setPage(currentPage);
      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error fetching fxRate list :  " + caught.getMessage());
      }
    });
  }

  private Handler getSelectionChangeHandler() {
    return new SelectionChangeEvent.Handler() {

      public void onSelectionChange(SelectionChangeEvent event) {
        if ((editionInProgress && selectionModel.getSelectedObject() != selectedFXRate) || reverseSelection) {
          selectionModel.setSelected(selectedFXRate, true);
          fxRateRateTextBox.setText(oldFXRateRateTextBoxContent);
          currencyISOTextBox.setText(oldFXRateCurrencyTextBoxContent);
          monthPeriod.setItemSelected(oldFXRateMonthPeriodValue, true);
          yearPeriod.setItemSelected(oldFXRateYearPeriodValue, true);

          if (!reverseSelection) {
            List<String> errors = new ArrayList<String>();
            errors.add("Please save or cancel changes before changing selection");
            showAlert(errors);
            reverseSelection = true;
          } else {
            reverseSelection = false;
          }
        } else {
          reverseSelection = false;
          selectedFXRate = selectionModel.getSelectedObject();
          if (selectedFXRate != null) {
            fxRateRateTextBox.setText(selectedFXRate.getRate().toPlainString());
            currencyISOTextBox.setText(selectedFXRate.getCurrencyIso());
            monthPeriod.setItemSelected(selectedFXRate.getPeriod().getMonth() - 1, true);
            yearPeriod.setItemSelected(selectedFXRate.getPeriod().getYear() - STARTING_YEAR, true);
            cancelUpdateFXRate.setVisible(true);
            deleteFXRate.setVisible(true);
            saveFXRate.setText("Update");
          }
        }
      }
    };

  };

  @UiHandler("saveFXRate")
  void handleClickSave(ClickEvent e) {
    if (fxRateIsValid()) {
      if (selectedFXRate == null) {
        selectedFXRate = new FXRateDTO();
        selectedFXRate.setZone(currentZone);

        selectedFXRate.setRate(new BigDecimal(fxRateRateTextBox.getText()));
        selectedFXRate.setCurrencyIso(currencyISOTextBox.getText());
        FiscalPeriodDTO monthPeriodDTO = new FiscalPeriodDTO();
        monthPeriodDTO.setMonth(monthPeriod.getSelectedIndex() + 1);
        monthPeriodDTO.setYear(yearPeriod.getSelectedIndex() + STARTING_YEAR);
        selectedFXRate.setPeriod(monthPeriodDTO);
        createFXRate();
      } else {
        selectedFXRate.setRate(new BigDecimal(fxRateRateTextBox.getText()));
        FiscalPeriodDTO monthPeriodDTO = new FiscalPeriodDTO();
        selectedFXRate.setCurrencyIso(currencyISOTextBox.getText());
        monthPeriodDTO.setId(selectedFXRate.getPeriod().getId());
        monthPeriodDTO.setMonth(monthPeriod.getSelectedIndex() + 1);
        monthPeriodDTO.setYear(yearPeriod.getSelectedIndex() + STARTING_YEAR);
        selectedFXRate.setPeriod(monthPeriodDTO);
        currentPage = pager.getPage();
        fxRateService.updateFXRate(selectedFXRate, new AsyncCallback<FXRateDTO>() {

          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Error updating fxRate :" + caught.getMessage());
          }

          @Override
          public void onSuccess(FXRateDTO result) {
            getFXRateList();
            fxRateRateTextBox.setText("");
            monthPeriod.setItemSelected(monthPeriod.getSelectedIndex(), false);
            yearPeriod.setItemSelected(currentYear - STARTING_YEAR, true);
            resetUpdateStatus();
          }
        });
      }
    }
  }

  private void createFXRate() {
    fxRateService.createFXRate(selectedFXRate, new AsyncCallback<FXRateDTO>() {

      @Override
      public void onSuccess(FXRateDTO result) {
        currentPage = 999;
        getFXRateList();
        fxRateRateTextBox.setText("");
        monthPeriod.setItemSelected(monthPeriod.getSelectedIndex(), false);
        yearPeriod.setItemSelected(currentYear - STARTING_YEAR, true);
        resetUpdateStatus();

      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error saving new fxRate :" + caught.getMessage());
      }
    });
  }

  private boolean fxRateIsValid() {
    List<String> errors = new ArrayList<String>();
    if (currentZone.getId() == -1) {
      errors.add("Please select a Zone ");
    }
    if (fxRateRateTextBox.getText().length() == 0 || !fxRateRateTextBox.getText().matches("\\-?[0-9]*\\.?[0-9]*")) {
      errors.add("Rate should not be empty and be numeric");
    }
    if (fxRateList != null) {
      for (FXRateDTO fxRateDTO : fxRateList) {
        if (fxRateDTO != selectedFXRate) {
          if (fxRateDTO.getPeriod().getMonth() == Integer.parseInt(monthPeriod.getValue(monthPeriod.getSelectedIndex()))
              && fxRateDTO.getPeriod().getYear() == Integer.parseInt(yearPeriod.getValue(yearPeriod.getSelectedIndex()))) {
            errors.add("Duplicate of fxRate rates is not allowed");
          }
        }
      }
    }
    showAlert(errors);
    return errors.size() == 0;
  }

  @UiHandler("cancelUpdateFXRate")
  void handleClickCancel(ClickEvent e) {
    resetUpdateStatus();
    currentPage = 999;
  }

  @UiHandler("deleteFXRate")
  void handleClickDelete(ClickEvent e) {
    deleteFXRate();
    currentPage = 999;
  }

  private void deleteFXRate() {
    fxRateService.deleteFXRate(selectedFXRate, new AsyncCallback<Void>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error deleting fxRate :" + caught.getMessage());

      }

      @Override
      public void onSuccess(Void result) {
        currentPage = 999;
        getFXRateList();
        resetUpdateStatus();
      }
    });
  }

  private void resetUpdateStatus() {
    cancelUpdateFXRate.setVisible(false);
    deleteFXRate.setVisible(false);
    fxRateRateTextBox.setValue("");
    currencyISOTextBox.setValue("");
    zoneListBox.setValue(dummyZone);
    monthPeriod.setItemSelected(monthPeriod.getSelectedIndex(), false);
    yearPeriod.setItemSelected(currentYear - STARTING_YEAR, true);
    fxRateCellTable.getSelectionModel().setSelected(null, true);
    selectedFXRate = null;
    saveFXRate.setText("Save FXRate");
    editionInProgress = false;
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
}
