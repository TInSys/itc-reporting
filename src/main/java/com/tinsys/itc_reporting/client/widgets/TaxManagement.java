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
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.tinsys.itc_reporting.client.service.TaxService;
import com.tinsys.itc_reporting.client.service.TaxServiceAsync;
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.client.service.ZoneServiceAsync;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class TaxManagement extends Composite implements WidgetSwitchManagement {

    private static Binder uiBinder = GWT.create(Binder.class);
    private TaxServiceAsync taxService = GWT.create(TaxService.class);
    private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
    private TaxDTO selectedTax;
    private int currentPage = 0;
    private boolean editionInProgress;
    private String oldTaxRateTextBoxContent;
    private Date oldTaxPeriodStartDateLabelContent;
    private Date oldTaxPeriodStopDateLabelContent;
    private boolean reverseSelection = false;
    private ZoneDTO currentZone;
    private List<TaxDTO> taxList;
    private PeriodDTO periodToClose;

    SimplePager.Resources pagerResources = GWT
            .create(SimplePager.Resources.class);

    @UiField(provided = true)
    ValueListBox<ZoneDTO> zoneListBox = new ValueListBox<ZoneDTO>(
            new Renderer<ZoneDTO>() {

                @Override
                public String render(ZoneDTO object) {
                    return (object.getCode() + " " + object.getName() + " " + object
                            .getCurrencyISO());
                }

                @Override
                public void render(ZoneDTO object, Appendable appendable)
                        throws IOException {
                    String s = render(object);
                    appendable.append(s);

                }
            });

    @UiField
    SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
            pagerResources, false, 0, true);

    ListDataProvider<TaxDTO> provider = new ListDataProvider<TaxDTO>();
    final Column<TaxDTO, String> taxRateColumn = new Column<TaxDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(TaxDTO taxDTO) {
            return taxDTO.getRate().toString();
        }
    };
    final Column<TaxDTO, String> taxPeriodColumn = new Column<TaxDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(TaxDTO taxDTO) {
            return taxDTO.getPeriod().toString();
        }
    };

    @UiField
    CellTable<TaxDTO> taxCellTable = new CellTable<TaxDTO>();
    private SingleSelectionModel<TaxDTO> selectionModel;

    @UiField
    TextBox taxRateTextBox = new TextBox();

    @UiField
    DateBox taxPeriodStartDateDateBox = new DateBox();

    @UiField
    DateBox taxPeriodStopDateDateBox = new DateBox();

    @UiField
    Button saveTax = new Button();

    @UiField
    Button deleteTax = new Button();

    @UiField
    Button cancelUpdateTax = new Button();

    @UiTemplate("TaxManagement.ui.xml")
    interface Binder extends UiBinder<Widget, TaxManagement> {
    }

    public TaxManagement() {
        initWidget(uiBinder.createAndBindUi(this));
        zoneService.getAllZones(new AsyncCallback<ArrayList<ZoneDTO>>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error fetching zone list :  "
                        + caught.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<ZoneDTO> result) {
                ZoneDTO dummyZone = new ZoneDTO();
                dummyZone.setCode("Please choose a Zone ");
                dummyZone.setName("");
                dummyZone.setCurrencyISO("");
                dummyZone.setId(-1L);
                result.add(0, dummyZone);
                zoneListBox.setAcceptableValues(result);
            }
        });

        selectionModel = new SingleSelectionModel<TaxDTO>();
        taxCellTable.setTableLayoutFixed(true);
        taxCellTable.addColumn(taxRateColumn, "Rate ");
        taxCellTable.addColumn(taxPeriodColumn, "Period ");
        taxCellTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
        taxCellTable.setPageSize(15);
        pager.setDisplay(taxCellTable);
        pager.setRangeLimited(true);

        provider.addDataDisplay(taxCellTable);
        taxRateTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> arg0) {
                editionInProgress = true;
                cancelUpdateTax.setVisible(true);
            }
        });

        taxRateTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent arg0) {
                editionInProgress = true;
                cancelUpdateTax.setVisible(true);
                oldTaxRateTextBoxContent = taxRateTextBox.getText();
            }
        });

        taxPeriodStartDateDateBox.setFormat(new DateBox.DefaultFormat(
                DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));
        taxPeriodStopDateDateBox.setFormat(new DateBox.DefaultFormat(
                DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));

        zoneListBox.addValueChangeHandler(new ValueChangeHandler<ZoneDTO>() {
            @Override
            public void onValueChange(ValueChangeEvent<ZoneDTO> event) {
                if (event.getValue().getId() < 0) {
                    resetUpdateStatus();
                } else {
                    currentZone = event.getValue();
                    getTaxList();
                }

            }
        });
    }

    private void getTaxList() {
        taxService.getAllTaxs(currentZone,
                new AsyncCallback<ArrayList<TaxDTO>>() {

                    @Override
                    public void onSuccess(ArrayList<TaxDTO> result) {
                        if (currentPage == 999) {
                            currentPage = ((result.size() - 1) < 0) ? 0
                                    : (result.size() - 1) / 10;
                        }
                        provider.getList().clear();
                        provider.getList().addAll(result);
                        taxList = result;
                        taxCellTable.setRowCount(result.size());
                        pager.setPage(currentPage);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error fetching tax list :  "
                                + caught.getMessage());
                    }
                });
    }

    private Handler getSelectionChangeHandler() {
        return new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                if ((editionInProgress && selectionModel.getSelectedObject() != selectedTax)
                        || reverseSelection) {
                    selectionModel.setSelected(selectedTax, true);
                    taxRateTextBox.setText(oldTaxRateTextBoxContent);
                    taxPeriodStartDateDateBox
                            .setValue(oldTaxPeriodStartDateLabelContent);
                    taxPeriodStopDateDateBox
                            .setValue(oldTaxPeriodStopDateLabelContent);
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
                    selectedTax = selectionModel.getSelectedObject();
                    if (selectedTax != null) {
                        taxRateTextBox.setText(selectedTax.getRate()
                                .toPlainString());

                        taxPeriodStartDateDateBox.setValue(selectedTax
                                .getPeriod().getStartDate());
                        taxPeriodStopDateDateBox.setValue(selectedTax
                                .getPeriod().getStopDate());
                        cancelUpdateTax.setVisible(true);
                        deleteTax.setVisible(true);
                        saveTax.setText("Update");
                    }
                }
            }
        };

    };

    @UiHandler("saveTax")
    void handleClickSave(ClickEvent e) {
        if (taxIsValid()) {
            if (selectedTax == null) {
                selectedTax = new TaxDTO();
                selectedTax.setZone(currentZone);
                selectedTax.setRate(new BigDecimal(taxRateTextBox.getText()));
                PeriodDTO periodDTO = new PeriodDTO();
                periodDTO.setStartDate(taxPeriodStartDateDateBox.getValue());
                periodDTO.setStopDate(taxPeriodStopDateDateBox.getValue());
                selectedTax.setPeriod(periodDTO);
                createTax();
            } else {
                selectedTax.setRate(new BigDecimal(taxRateTextBox.getText()));
                PeriodDTO periodDTO = new PeriodDTO();
                periodDTO.setId(selectedTax.getPeriod().getId());
                periodDTO.setStartDate(taxPeriodStartDateDateBox.getValue());
                periodDTO.setStopDate(taxPeriodStopDateDateBox.getValue());
                selectedTax.setPeriod(periodDTO);
                currentPage = pager.getPage();
                taxService.updateTax(selectedTax, new AsyncCallback<TaxDTO>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error updating tax :"
                                + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(TaxDTO result) {
                        getTaxList();
                        taxRateTextBox.setText("");
                        taxPeriodStartDateDateBox.setValue(null);
                        taxPeriodStopDateDateBox.setValue(null);
                        resetUpdateStatus();
                    }
                });
            }
        }
    }

    private void createTax() {
        taxService.createTax(selectedTax, periodToClose,
                new AsyncCallback<TaxDTO>() {

                    @Override
                    public void onSuccess(TaxDTO result) {
                        currentPage = 999;
                        getTaxList();
                        taxRateTextBox.setText("");
                        taxPeriodStartDateDateBox.setValue(null);
                        taxPeriodStopDateDateBox.setValue(null);
                        resetUpdateStatus();

                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error saving new tax :"
                                + caught.getMessage());
                    }
                });
    }

    private boolean taxIsValid() {
        periodToClose = null;
        List<String> errors = new ArrayList<String>();
        if (currentZone == null) {
            errors.add("Please select a Zone ");
        }
        if (taxRateTextBox.getText().length() == 0
                || !taxRateTextBox.getText().matches("\\-?[0-9]*\\.?[0-9]*")) {
            errors.add("Rate should not be empty and be numeric");
        }
        if (taxPeriodStartDateDateBox.getValue() == null) {
            errors.add("Period Start date should not be empty");
        }
        if (taxPeriodStopDateDateBox.getValue() != null
                && taxPeriodStartDateDateBox.getValue().getTime() > taxPeriodStopDateDateBox
                        .getValue().getTime()) {
            errors.add("Period Start date should be lower or equal to period end date");
        }
        if (errors.size() == 0) {
            boolean intervalLimitOK = false;
            int lastDateindex = taxList.size() - 1;
            int currentIndex = 0;
            String periodError = "Overlapping of tax rates is not allowed";
            if (lastDateindex > -1) {
                errors.add(periodError);
            }
            if (selectedTax != null) {
                long newStartDate = taxPeriodStartDateDateBox.getValue()
                        .getTime();
                long newStopDate = (taxPeriodStopDateDateBox.getValue() != null) ? taxPeriodStopDateDateBox
                        .getValue().getTime() : Long.MAX_VALUE;
                int taxPosition = taxList.indexOf(selectedTax);
                if (lastDateindex == 0){
                    errors.remove(periodError);
                } else {
                if (taxPosition == 0){
                    PeriodDTO existingPeriodTaxToCheck = taxList.get(1).getPeriod();
                    if (newStopDate < existingPeriodTaxToCheck.getStartDate().getTime()){
                        errors.remove(periodError);
                    }
                } else if(taxPosition == lastDateindex){
                    PeriodDTO existingPeriodTaxToCheck = taxList.get(lastDateindex-1).getPeriod();
                    if (newStartDate > existingPeriodTaxToCheck.getStopDate().getTime()){
                        errors.remove(periodError);                        
                    }
                } else {
                    long existingAntePeriodTaxToCheck = 0;
                    long existingPostPeriodTaxToCheck = 0;
                    for (TaxDTO taxDTO : taxList) {  
                        if (taxDTO.getPeriod().getStartDate().getTime()<=newStartDate){
                        existingAntePeriodTaxToCheck = taxDTO.getPeriod().getStopDate().getTime();
                        }
                        if (taxDTO.getPeriod().getStopDate().getTime()>=newStopDate){
                                existingPostPeriodTaxToCheck = taxDTO
                                        .getPeriod().getStartDate().getTime();
                        break;
                        }
                    }
                    if (newStartDate > existingAntePeriodTaxToCheck && newStopDate<existingPostPeriodTaxToCheck){
                        errors.remove(periodError);                                                
                    }
                }}
            } else {
                for (TaxDTO taxDTO : taxList) {

                    long newStartDate = taxPeriodStartDateDateBox.getValue()
                            .getTime();
                    long newStopDate = (taxPeriodStopDateDateBox.getValue() != null) ? taxPeriodStopDateDateBox
                            .getValue().getTime() : Long.MAX_VALUE;
                    long existingStartDate = taxDTO.getPeriod().getStartDate()
                            .getTime();
                    long existingStopDate = (taxDTO.getPeriod().getStopDate() != null) ? taxDTO
                            .getPeriod().getStopDate().getTime()
                            : Long.MAX_VALUE;
                    if (taxDTO != selectedTax) {
                        if (existingStartDate > newStopDate
                                && existingStopDate > newStopDate
                                && currentIndex == 0) {
                            errors.remove(periodError);
                            break;
                        }
                        if (currentIndex == lastDateindex
                                && newStartDate > existingStartDate
                                && (newStartDate > existingStopDate || existingStopDate == Long.MAX_VALUE)) {
                            errors.remove(periodError);
                            periodToClose = taxDTO.getPeriod();
                            Date newDate = taxPeriodStartDateDateBox.getValue();
                            CalendarUtil.addDaysToDate(newDate, -1);
                            periodToClose.setStopDate(newDate);
                            break;
                        }

                        if ((newStartDate <= existingStartDate && newStopDate >= existingStartDate)
                                || (newStartDate <= existingStopDate && newStopDate >= existingStopDate)
                                || (newStartDate <= existingStartDate && newStopDate >= existingStopDate)) {
                            break;
                        }

                        if (existingStartDate < newStartDate
                                && existingStopDate < newStartDate) {
                            intervalLimitOK = true;
                        } else if (intervalLimitOK == true) {
                            if (existingStartDate > newStopDate) {
                                errors.remove(periodError);
                                break;
                            } else {
                                intervalLimitOK = false;
                            }
                        }
                    }
                    currentIndex++;
                }
            }
        }
        showAlert(errors);
        return errors.size() == 0;
    }

    @UiHandler("cancelUpdateTax")
    void handleClickCancel(ClickEvent e) {
        resetUpdateStatus();
        currentPage = 999;
    }

    @UiHandler("deleteTax")
    void handleClickDelete(ClickEvent e) {
        deletePredefinedComment();
        currentPage = 999;
    }

    private void deletePredefinedComment() {
        taxService.deleteTax(selectedTax, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error deleting tax :" + caught.getMessage());

            }

            @Override
            public void onSuccess(Void result) {
                currentPage = 999;
                getTaxList();
                resetUpdateStatus();
            }
        });
    }

    private void resetUpdateStatus() {
        cancelUpdateTax.setVisible(false);
        deleteTax.setVisible(false);
        taxRateTextBox.setValue("");
        taxPeriodStartDateDateBox.setValue(null);
        taxPeriodStopDateDateBox.setValue(null);
        taxCellTable.getSelectionModel().setSelected(null, true);
        selectedTax = null;
        saveTax.setText("Save Tax");
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
