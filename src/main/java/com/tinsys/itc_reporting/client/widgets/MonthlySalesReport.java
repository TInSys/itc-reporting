package com.tinsys.itc_reporting.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.tinsys.itc_reporting.client.service.SalesReportService;
import com.tinsys.itc_reporting.client.service.SalesReportServiceAsync;
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.client.service.ZoneServiceAsync;
import com.tinsys.itc_reporting.shared.dto.ApplicationReportSummary;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.MonthReportSummary;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class MonthlySalesReport extends Composite implements
        WidgetSwitchManagement {

    private static Binder uiBinder = GWT.create(Binder.class);
    private SalesReportServiceAsync salesReportService = GWT
            .create(SalesReportService.class);
    private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
    private boolean editionInProgress;

    private static int STARTING_YEAR = 2008;
    private int currentYear;
    private int currentMonth;

    
    SimplePager.Resources pagerResources = GWT
            .create(SimplePager.Resources.class);

    @UiField
    SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
            pagerResources, false, 0, true);

    ListDataProvider<MonthReportSummary> provider = new ListDataProvider<MonthReportSummary>();
    

    @UiField
    ListBox monthPeriodListBox = new ListBox();

    @UiField
    ListBox yearPeriodListBox = new ListBox();

    @UiField
    CellTable<MonthReportSummary> salesDataGrid = new CellTable<MonthReportSummary>();
    protected ArrayList<ZoneDTO> zoneList;

    @UiTemplate("MonthlySalesReport.ui.xml")
    interface Binder extends UiBinder<Widget, MonthlySalesReport> {
    }

    public MonthlySalesReport() {
        initWidget(uiBinder.createAndBindUi(this));
        for (int i = 1; i <= 12; i++) {
            monthPeriodListBox.addItem(Integer.toString(i));
        }
        for (int i = STARTING_YEAR; i <= 2040; i++) {
            yearPeriodListBox.addItem(Integer.toString(i));
        }
        Date today = new Date();
        currentYear = new Integer(DateTimeFormat.getFormat("yyyy")
                .format(today));
        currentMonth = new Integer(DateTimeFormat.getFormat("MM").format(today));
        yearPeriodListBox.setSelectedIndex(currentYear - STARTING_YEAR);
        monthPeriodListBox.setSelectedIndex(currentMonth - 1);
        monthPeriodListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                currentMonth = monthPeriodListBox.getSelectedIndex() + 1;
                getReport();
            }
        });

        yearPeriodListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                currentYear = yearPeriodListBox.getSelectedIndex()
                        + STARTING_YEAR;
                getReport();
            }
        });
        getReport();
    }

    private void getReport() {
        FiscalPeriodDTO monthPeriodDto = new FiscalPeriodDTO();
        monthPeriodDto.setId(null);
        monthPeriodDto.setMonth(currentMonth);
        monthPeriodDto.setYear(currentYear);
        salesReportService.getMonthlyReport(monthPeriodDto,
                new AsyncCallback<List<MonthReportSummary>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error fetching monthly FX rates list :  "
                                + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(List<MonthReportSummary> result) {
                        if (result != null) {
                            showResult(result);
                        }
                    }
                });
    }

    protected void showResult(List<MonthReportSummary> result) {
        final List<String> applications = new ArrayList<String>();
        // find all applications that will be displayed
        for (MonthReportSummary monthReportSummary : result) {
            for (ApplicationReportSummary applicationReportSummary : monthReportSummary
                    .getApplications()) {
                if (!applications.contains(applicationReportSummary
                        .getApplicationName())) {
                    applications.add(applicationReportSummary
                            .getApplicationName());
                }
            }
        }

        for (int i = 0; i < (applications.size() + 1); i++) {
            TextColumn<MonthReportSummary> nameColumn;
            final int index = i;
            if (i == 0) {
                nameColumn = new TextColumn<MonthReportSummary>() {
                    public String getValue(MonthReportSummary object) {
                        return object.getZoneName();
                    }
                };
                salesDataGrid.addColumn(nameColumn, "Zone ");
                salesDataGrid.setColumnWidth(nameColumn, 100, Unit.PX);
            } else {
                nameColumn = new TextColumn<MonthReportSummary>() {
                    public String getValue(MonthReportSummary object) {
                        String content = "";
                        for (ApplicationReportSummary applicationReportSummary : object
                                .getApplications()) {
                            if (applicationReportSummary.getApplicationName()
                                    .equals(applications.get(index-1))) {
                                content = applicationReportSummary
                                        .getOriginalCurrencyAmount().toString()
                                        + " "
                                        + applicationReportSummary
                                                .getOriginalCurrency();
                            }
                        }
                        return content;
                    }
                };
                salesDataGrid.addColumn(nameColumn,applications.get(index-1));
                salesDataGrid.setColumnWidth(nameColumn, 100, Unit.PX);
            }
        }
        salesDataGrid.setRowCount(result.size(), true);
        salesDataGrid.setWidth("1000px");
        salesDataGrid.setPageSize(15);
        pager.setDisplay(salesDataGrid);
        pager.setRangeLimited(true);
        provider.addDataDisplay(salesDataGrid);
        provider.getList().addAll(result);
    }

    @Override
    public boolean isEditing() {
        return this.editionInProgress;
    }
}
