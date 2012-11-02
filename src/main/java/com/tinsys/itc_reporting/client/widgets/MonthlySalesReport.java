package com.tinsys.itc_reporting.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.tinsys.itc_reporting.client.service.SalesReportService;
import com.tinsys.itc_reporting.client.service.SalesReportServiceAsync;
import com.tinsys.itc_reporting.shared.dto.ApplicationReportSummary;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.MonthReportSummary;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class MonthlySalesReport extends Composite implements
        WidgetSwitchManagement {

    private static Binder uiBinder = GWT.create(Binder.class);
    private SalesReportServiceAsync salesReportService = GWT
            .create(SalesReportService.class);
    private boolean editionInProgress;

    private static int STARTING_YEAR = 2008;
    private int currentYear;
    private int currentMonth;
    private List<String> headers = new ArrayList<String>();

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
        salesDataGrid.setEmptyTableWidget(new Label("No records found"));
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
        int colCount = salesDataGrid.getColumnCount();
        for (int i = colCount - 1; i >= 0; i--) {
            salesDataGrid.removeColumn(i);
        }
        salesReportService.getMonthlyReport(monthPeriodDto,
                new AsyncCallback<List<MonthReportSummary>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error fetching monthly FX rates list :  "
                                + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(List<MonthReportSummary> result) {
                        showResult(result);
                    }
                });
    }

    protected void showResult(List<MonthReportSummary> result) {
        final List<String> applications = new ArrayList<String>();
        if (result.size() > 0) {
            // find all applications that will be displayed
            for (MonthReportSummary monthReportSummary : result) {
                for (ApplicationReportSummary applicationReportSummary : monthReportSummary
                        .getApplications()) {
                    if (!applications.contains(applicationReportSummary
                            .getApplicationName()) && !applicationReportSummary
                            .getApplicationName().equals("Total ")) {
                        applications.add(applicationReportSummary
                                .getApplicationName());
                    }
                }
            }
            applications.add("Total ");
            headers.clear();
            for (int i = 0; i < (applications.size() + 1); i++) {
                TextColumn<MonthReportSummary> aColumn;
                final int index = i;
                if (i == 0) {
                    aColumn = new TextColumn<MonthReportSummary>() {
                        public String getValue(MonthReportSummary object) {
                            return object.getZoneName();
                        }
                    };
                    salesDataGrid.addColumn(aColumn, "Zone ");
                    headers.add("Zone");
                    salesDataGrid.setColumnWidth(aColumn, 100, Unit.PX);
                } else {
                    aColumn = new TextColumn<MonthReportSummary>() {
                        public String getValue(MonthReportSummary object) {
                            String content = "";
                            for (ApplicationReportSummary applicationReportSummary : object
                                    .getApplications()) {
                                if (applicationReportSummary
                                        .getApplicationName().equals(
                                                applications.get(index - 1))) {
                                    content = String
                                            .valueOf(applicationReportSummary
                                                    .getSalesNumber());

                                }
                            }
                            return content;
                        }
                    };
                    salesDataGrid.addColumn(aColumn,
                            applications.get(index - 1));
                    salesDataGrid.setColumnWidth(aColumn, 150, Unit.PX);
                    
                    aColumn = new TextColumn<MonthReportSummary>() {
                        public String getValue(MonthReportSummary object) {
                            String content = "";
                            for (ApplicationReportSummary applicationReportSummary : object
                                    .getApplications()) {
                                if (applicationReportSummary
                                        .getApplicationName().equals(
                                                applications.get(index - 1))) {
                                    if (applicationReportSummary
                                            .getOriginalCurrencyAmount()!=null){
                                    content = applicationReportSummary
                                            .getOriginalCurrencyAmount()
                                            .setScale(2).toString();}
                                }
                            }
                            return content;
                        }
                    };
                    salesDataGrid.addColumn(aColumn,
                            applications.get(index - 1));
                    salesDataGrid.setColumnWidth(aColumn, 150, Unit.PX);
                    
                    aColumn = new TextColumn<MonthReportSummary>() {
                        public String getValue(MonthReportSummary object) {
                            String content = "";
                            for (ApplicationReportSummary applicationReportSummary : object
                                    .getApplications()) {
                                if (applicationReportSummary
                                        .getApplicationName().equals(
                                                applications.get(index - 1))) {
                                    content = applicationReportSummary
                                            .getReferenceCurrencyAmount()
                                            .setScale(2).toString();
                                }
                            }
                            return content;
                        }
                    };
                    salesDataGrid.addColumn(aColumn,
                            applications.get(index - 1));
                    salesDataGrid.setColumnWidth(aColumn, 150, Unit.PX);
                }
            }
            headers.addAll(applications);
        }
        if (result.size() > 0) {
            salesDataGrid.setHeaderBuilder(new CustomHeaderBuilder());
        } else {
            salesDataGrid.getHeaderBuilder().buildHeader();
        }
        salesDataGrid.setRowCount(result.size(), true);
        salesDataGrid.setWidth("1000px");
        salesDataGrid.setPageSize(15);
        if (pager.getDisplay() == null) {
            pager.setDisplay(salesDataGrid);
        }
        pager.setRangeLimited(true);
        provider.getList().clear();
        if (provider.getDataDisplays().contains(salesDataGrid)) {
            provider.removeDataDisplay(salesDataGrid);
        }
        provider.addDataDisplay(salesDataGrid);
        provider.refresh();
        provider.getList().addAll(result);
    }

    @Override
    public boolean isEditing() {
        return this.editionInProgress;
    }

    @UiHandler("exportToXLS")
    void onClickDownloadProjectors(ClickEvent e) {
        String fileDownloadURL = GWT.getModuleBaseURL() + "download?month="
                + currentMonth + "&year=" + currentYear;
        Frame fileDownloadFrame = new Frame(fileDownloadURL);
        fileDownloadFrame.setSize("0px", "0px");
        fileDownloadFrame.setVisible(false);
        RootPanel panel = RootPanel.get("__gwt_downloadFrame");
        while (panel.getWidgetCount() > 0)
            panel.remove(0);
        panel.add(fileDownloadFrame);
    }

    private class CustomHeaderBuilder extends
            AbstractHeaderOrFooterBuilder<MonthReportSummary> {

        public CustomHeaderBuilder() {
            super(salesDataGrid, false);
            setSortIconStartOfLine(false);
        }

        @Override
        protected boolean buildHeaderOrFooterImpl() {
            TableRowBuilder tr = startRow();
            tr.startTH().colSpan(1).rowSpan(1);
            tr.endTH();

            String styleDescription = "padding: 3px 15px;font-size: 1.1em; text-align:center;color: #4B4A4A;text-shadow: 1px 1px 0 #DDDDFF;background-color:#F8F8F8;";
            TableCellBuilder th = tr.startTH().colSpan(3)
                    .attribute("style", styleDescription);
            for (int i = 1; i < headers.size(); i++) {
                th.text(headers.get(i)).endTH();
                styleDescription = styleToggle(styleDescription,
                        "background-color:#F8F8F8;",
                        "background-color:#E8E8E8;");
                th = tr.startTH().colSpan(3)
                        .attribute("style", styleDescription);
            }

            tr = startRow();
            int colGroupIndex = 0;
            for (int i = 0; i < salesDataGrid.getColumnCount(); i++) {
                if (i == 0) {
                    Header<String> zoneHeader = new TextHeader(headers.get(i));
                    buildHeader(tr, zoneHeader, salesDataGrid.getColumn(i),
                            false, false);
                    colGroupIndex += 1;
                } else {
                    Header<String> theHeader;
                    switch (colGroupIndex) {
                    case 1:
                        theHeader = new TextHeader("Sales #");
                        buildHeader(tr, theHeader, salesDataGrid.getColumn(i),
                                false, false);
                        break;
                    case 2:
                        theHeader = new TextHeader("Total orig. currency");
                        buildHeader(tr, theHeader, salesDataGrid.getColumn(i),
                                false, false);
                        break;
                    case 3:
                        theHeader = new TextHeader("Total ref. currency");
                        buildHeader(tr, theHeader, salesDataGrid.getColumn(i),
                                false, false);
                        colGroupIndex = 0;
                        break;
                    default:
                        break;
                    }
                    colGroupIndex += 1;

                }

            }
            tr.endTR();

            return true;
        }

        private String styleToggle(String currentStyle, String color1,
                String color2) {
            if (currentStyle.indexOf(color1) != -1) {
                return currentStyle.replace(color1, color2);
            } else {
                return currentStyle.replace(color2, color1);
            }
        }

        /**
         * Renders the header of one column, with the given options.
         * 
         * @param out
         *            the table row to build into
         * @param header
         *            the {@link Header} to render
         * @param column
         *            the column to associate with the header
         * @param sortedColumn
         *            the column that is currently sorted
         * @param isSortAscending
         *            true if the sorted column is in ascending order
         * @param isFirst
         *            true if this the first column
         * @param isLast
         *            true if this the last column
         */
        private void buildHeader(TableRowBuilder out, Header<?> header,
                Column<MonthReportSummary, ?> column, boolean isFirst,
                boolean isLast) {
            // Choose the classes to include with the element.
            Style style = salesDataGrid.getResources().style();
            StringBuilder classesBuilder = new StringBuilder(style.header());
            if (isFirst) {
                classesBuilder.append(" " + style.firstColumnHeader());
            }
            if (isLast) {
                classesBuilder.append(" " + style.lastColumnHeader());
            }
            if (column.isSortable()) {
                classesBuilder.append(" " + style.sortableHeader());
            }

            // Create the table cell.
            TableCellBuilder th = out.startTH().className(
                    classesBuilder.toString());

            // Render the header.
            Context context = new Context(0, 2, header.getKey());
            renderHeader(th, context, header);
            // End the table cell.
            th.endTH();
        }
    }

}
