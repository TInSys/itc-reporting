package com.tinsys.itc_reporting.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;
import com.tinsys.itc_reporting.client.service.SalesReportService;
import com.tinsys.itc_reporting.client.service.SalesReportServiceAsync;
import com.tinsys.itc_reporting.client.widgets.utils.CellTableRes;
import com.tinsys.itc_reporting.shared.dto.ApplicationReportSummary;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneReportSummary;

public class MonthlySalesReport extends Composite implements WidgetSwitchManagement {
  private final static String ZONE_TOTAL_COLUMN = "Total by Zone";
  private final static String PROCEEDS_REPORT = "Proceeds";
  private final static String SALES_REPORT = "Sales";
  private final static String PROCEEDS_AFTER_TAX_REPORT = "Proceeds after taxes";
  private String currentReport;

  private static Binder uiBinder = GWT.create(Binder.class);
  private CellTable.Resources tableRes = GWT.create(CellTableRes.class);
  private SalesReportServiceAsync salesReportService = GWT.create(SalesReportService.class);
  private boolean editionInProgress;

  private static int STARTING_YEAR = 2008;
  private int currentYear;
  private int currentMonth;
  private List<String> headers = new ArrayList<String>();

  SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);

  @UiField
  SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);

  @UiField
  SimplePager hiddenPager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);

  ListDataProvider<ZoneReportSummary> provider = new ListDataProvider<ZoneReportSummary>();

  @UiField
  ListBox reportTypeListBox = new ListBox();

  @UiField
  ListBox monthPeriodListBox = new ListBox();

  @UiField
  ListBox yearPeriodListBox = new ListBox();

  @UiField
  HorizontalPanel widgetRootPanel = new HorizontalPanel();

  @UiField
  ResizeLayoutPanel resizablePanel = new ResizeLayoutPanel();

  @UiField(provided = true)
  CellTable<ZoneReportSummary> salesDataGrid = new CellTable<ZoneReportSummary>(25, tableRes);

  @UiField(provided = true)
  CellTable<ZoneReportSummary> salesFixedColumn = new CellTable<ZoneReportSummary>(25, tableRes);

  protected ArrayList<ZoneDTO> zoneList;

  @UiTemplate("MonthlySalesReport.ui.xml")
  interface Binder extends UiBinder<Widget, MonthlySalesReport> {
  }

  public MonthlySalesReport() {
    initWidget(uiBinder.createAndBindUi(this));
    Window.addResizeHandler(new ResizeHandler() {

      public void onResize(ResizeEvent event) {
        int width = event.getWidth() - 400;
        resizablePanel.setWidth(width + "px");
      }
    });
    reportTypeListBox.addItem(SALES_REPORT);
    reportTypeListBox.addItem(PROCEEDS_REPORT);
    reportTypeListBox.addItem(PROCEEDS_AFTER_TAX_REPORT);
    reportTypeListBox.setSelectedIndex(0);
    currentReport = reportTypeListBox.getItemText(0);
    reportTypeListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        currentReport = reportTypeListBox.getItemText(reportTypeListBox.getSelectedIndex());
        headers.clear();
        getReport();
      }
    });

    for (int i = 1; i <= 12; i++) {
      monthPeriodListBox.addItem(Integer.toString(i));
    }
    for (int i = STARTING_YEAR; i <= 2040; i++) {
      yearPeriodListBox.addItem(Integer.toString(i));
    }
    salesDataGrid.setEmptyTableWidget(new HTML("There is no data to display"));
    Date today = new Date();
    currentYear = new Integer(DateTimeFormat.getFormat("yyyy").format(today));
    currentMonth = new Integer(DateTimeFormat.getFormat("MM").format(today));
    yearPeriodListBox.setSelectedIndex(currentYear - STARTING_YEAR);
    monthPeriodListBox.setSelectedIndex(currentMonth - 1);
    monthPeriodListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        currentMonth = monthPeriodListBox.getSelectedIndex() + 1;
        headers.clear();
        getReport();
      }
    });

    yearPeriodListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        currentYear = yearPeriodListBox.getSelectedIndex() + STARTING_YEAR;
        headers.clear();
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
    if (salesDataGrid.getColumnCount() > 0) {
      int colCount = salesDataGrid.getColumnCount();
      for (int i = colCount - 1; i >= 0; i--) {
        salesDataGrid.removeColumn(i);
      }
      salesFixedColumn.removeColumn(0);
    }
    salesReportService.getMonthlyReport(monthPeriodDto, new AsyncCallback<List<ZoneReportSummary>>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error fetching monthly FX rates list :  " + caught.getMessage());
      }

      @Override
      public void onSuccess(List<ZoneReportSummary> result) {
        showResult(result);
      }
    });
  }

  protected void showResult(List<ZoneReportSummary> result) {
    final List<String> applications = new ArrayList<String>();
    if (result.size() > 0) {
      // find all applications that will be displayed
      for (ZoneReportSummary zoneReportSummary : result) {
        for (ApplicationReportSummary applicationReportSummary : zoneReportSummary.getApplications()) {
          if (!applications.contains(applicationReportSummary.getApplicationName()) && !applicationReportSummary.getApplicationName().equals(ZONE_TOTAL_COLUMN)) {
            applications.add(applicationReportSummary.getApplicationName());
          }
        }
      }
      applications.add(ZONE_TOTAL_COLUMN);
      headers.clear();
      for (int i = 0; i < (applications.size() + 1); i++) {
        TextColumn<ZoneReportSummary> aColumn;
        final int index = i;
        if (i == 0) {
          aColumn = new TextColumn<ZoneReportSummary>() {
            public String getValue(ZoneReportSummary object) {
              return object.getZoneName().replace(" ", " ");
            }
          };
          salesFixedColumn.addColumn(aColumn, "Zone ");
          salesFixedColumn.setColumnWidth(aColumn, 10, Unit.EM);
        } else {
          generateColumn("SalesNumber", applications, index);
          if (currentReport.equals(SALES_REPORT)) {
            generateColumn("OriginalCurrencyAmount", applications, index);
            generateColumn("ReferenceCurrencyAmount", applications, index);
          } else if (currentReport.equals(PROCEEDS_REPORT)) {
            generateColumn("OriginalCurrencyProceeds", applications, index);
            generateColumn("ReferenceCurrencyProceeds", applications, index);
          } else if (currentReport.equals(PROCEEDS_AFTER_TAX_REPORT)) {
            generateColumn("OriginalCurrencyProceedsAfterTax", applications, index);
            generateColumn("ReferenceCurrencyProceedsAfterTax", applications, index);
          }
        }
      }
      headers.addAll(applications);
    }
    if (result.size() > 0) {
      salesDataGrid.setHeaderBuilder(new CustomHeaderBuilder());
      salesFixedColumn.setHeaderBuilder(new CustomHeaderBuilderBis());
    }
    salesFixedColumn.setRowCount(result.size(), true);
    if (hiddenPager.getDisplay() == null) {
      hiddenPager.setDisplay(salesDataGrid);
    }
    if (pager.getDisplay() == null) {
      pager.setDisplay(salesFixedColumn);
    }
    pager.setRangeLimited(true);
    hiddenPager.setRangeLimited(true);
    provider.getList().clear();
    if (provider.getDataDisplays().contains(salesFixedColumn)) {
      provider.removeDataDisplay(salesFixedColumn);
    }
    provider.addDataDisplay(salesFixedColumn);
    salesDataGrid.setRowCount(result.size(), true);

    provider.getList().clear();
    if (provider.getDataDisplays().contains(salesDataGrid)) {
      provider.removeDataDisplay(salesDataGrid);
    }
    provider.addDataDisplay(salesDataGrid);

    provider.refresh();
    provider.getList().addAll(result);
    RangeChangeEvent.Handler handler = new RangeChangeEvent.Handler() {

      @Override
      public void onRangeChange(RangeChangeEvent event) {
        if (event.getSource() == salesFixedColumn) {
          Window.alert(" range change " + event.getNewRange());
          hiddenPager.setPage(pager.getPage());
          hiddenPager.setPageSize(pager.getPageSize());
          hiddenPager.setPageStart(pager.getPageStart());
        }
      }
    };
    salesFixedColumn.addRangeChangeHandler(handler);

    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        resizablePanel.setHeight("" + (salesFixedColumn.getOffsetHeight()) + "px");
        resizablePanel.setWidth("" + (widgetRootPanel.getOffsetWidth() - 180) + "px");
      }
    });
  }

  private void generateColumn(final String col, final List<String> applications, final int index) {
    final NumberFormat myFormatter = NumberFormat.getFormat("####.##");
    TextColumn<ZoneReportSummary> aColumn = new TextColumn<ZoneReportSummary>() {
      public String getValue(ZoneReportSummary object) {
        String content = "";
        for (ApplicationReportSummary applicationReportSummary : object.getApplications()) {
          if (applicationReportSummary.getApplicationName().equals(applications.get(index - 1))) {
            if (col.equals("OriginalCurrencyAmount")) {
              if (applicationReportSummary.getOriginalCurrencyAmount() != null) {
                content = myFormatter.format(applicationReportSummary.getOriginalCurrencyAmount()) + " " + applicationReportSummary.getOriginalCurrency();
              }
            } else if (col.equals("ReferenceCurrencyAmount")) {
              content = myFormatter.format(applicationReportSummary.getReferenceCurrencyAmount()) + " " + applicationReportSummary.getReferenceCurrency();
            } else if (col.equals("SalesNumber")) {
              content = String.valueOf(applicationReportSummary.getSalesNumber());
            } else if (col.equals("OriginalCurrencyProceeds")) {
              if (applicationReportSummary.getOriginalCurrencyProceeds() != null) {
                content = myFormatter.format(applicationReportSummary.getOriginalCurrencyProceeds()) + " " + applicationReportSummary.getOriginalCurrency();
              }
            } else if (col.equals("ReferenceCurrencyProceeds")) {
              content = myFormatter.format(applicationReportSummary.getReferenceCurrencyProceeds()) + " " + applicationReportSummary.getReferenceCurrency();
            } else if (col.equals("OriginalCurrencyProceedsAfterTax")) {
              if (applicationReportSummary.getOriginalCurrencyProceeds() != null) {
                content = myFormatter.format(applicationReportSummary.getOriginalCurrencyProceedsAfterTax()) + " " + applicationReportSummary.getOriginalCurrency();
              }
            } else if (col.equals("ReferenceCurrencyProceedsAfterTax")) {
              content = myFormatter.format(applicationReportSummary.getReferenceCurrencyProceedsAfterTax()) + " " + applicationReportSummary.getReferenceCurrency();
            }
          }
        }
        return content;
      }
    };
    if (!col.equals("SalesNumber")) {
      aColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    }
    salesDataGrid.addColumn(aColumn, applications.get(index - 1));
  }

  @Override
  public boolean isEditing() {
    return this.editionInProgress;
  }

  @UiHandler("exportToXLS")
  void onClickExportToXLS(ClickEvent e) {
    String fileDownloadURL = GWT.getModuleBaseURL() + "download?month=" + currentMonth + "&year=" + currentYear;
    Frame fileDownloadFrame = new Frame(fileDownloadURL);
    fileDownloadFrame.setSize("0px", "0px");
    fileDownloadFrame.setVisible(false);
    RootPanel panel = RootPanel.get("__gwt_downloadFrame");
    while (panel.getWidgetCount() > 0)
      panel.remove(0);
    panel.add(fileDownloadFrame);
  }

  private class CustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<ZoneReportSummary> {

    public CustomHeaderBuilder() {
      super(salesDataGrid, false);
      setSortIconStartOfLine(false);
    }

    @Override
    protected boolean buildHeaderOrFooterImpl() {
      if (headers.size() > 0) {
        TableRowBuilder tr = startRow();
        /*
         * tr.startTH().colSpan(1).rowSpan(1); tr.endTH();
         */

        String styleDescription = "border-bottom: 2px solid #6f7277;padding: 3px 15px;text-align: left;color: #4b4a4a;text-shadow: #ddf 1px 1px 0;overflow: hidden;height: 60px;";
        TableCellBuilder th = tr.startTH().colSpan(3).attribute("style", styleDescription);
        for (int i = 0; i < headers.size(); i++) {
          th.text(headers.get(i)).endTH();
          styleDescription = styleToggle(styleDescription, "background-color:#E8E8E8;", "background-color:#A8A8A8;");
          if (i != headers.size() - 1) {
            th = tr.startTH().colSpan(3).attribute("style", styleDescription);
          } else {
            th = tr.startTH();
          }
        }
        tr.endTH();

        tr = startRow();
        int colGroupIndex = 0;
        for (int i = 0; i < salesDataGrid.getColumnCount(); i++) {

          Header<String> theHeader = new TextHeader("");
          switch (colGroupIndex) {
          case 0:
            theHeader = new TextHeader("Sales #            ");
            buildHeader(tr, theHeader, salesDataGrid.getColumn(i), false, false);
            break;
          case 1:
            if (currentReport.equals(SALES_REPORT)) {
              theHeader = new TextHeader("Total orig. currency             ");
            } else if (currentReport.equals(PROCEEDS_REPORT)) {
              theHeader = new TextHeader("Proceeds orig. currency          ");
            } else if (currentReport.equals(PROCEEDS_AFTER_TAX_REPORT)) {
              theHeader = new TextHeader("Proceeds orig. curr. after Tax   ");
            }
            buildHeader(tr, theHeader, salesDataGrid.getColumn(i), false, false);
            break;
          case 2:
            if (currentReport.equals(SALES_REPORT)) {
              theHeader = new TextHeader("Total ref. currency              ");
            } else if (currentReport.equals(PROCEEDS_REPORT)) {
              theHeader = new TextHeader("Proceeds ref. currency           ");
            } else if (currentReport.equals(PROCEEDS_AFTER_TAX_REPORT)) {
              theHeader = new TextHeader("Proceeds ref. curr. after Tax    ");
            }
            buildHeader(tr, theHeader, salesDataGrid.getColumn(i), false, false);
            colGroupIndex = -1;
            break;
          default:
            break;
          }
          colGroupIndex += 1;

        }
        tr.endTR();

        return true;
      }
      return false;
    }

    private String styleToggle(String currentStyle, String color1, String color2) {
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
     *          the table row to build into
     * @param header
     *          the {@link Header} to render
     * @param column
     *          the column to associate with the header
     * @param sortedColumn
     *          the column that is currently sorted
     * @param isSortAscending
     *          true if the sorted column is in ascending order
     * @param isFirst
     *          true if this the first column
     * @param isLast
     *          true if this the last column
     */
    private void buildHeader(TableRowBuilder out, Header<?> header, Column<ZoneReportSummary, ?> column, boolean isFirst, boolean isLast) {
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
      TableCellBuilder th = out.startTH().className(classesBuilder.toString());

      // Render the header.
      Context context = new Context(0, 2, header.getKey());
      renderHeader(th, context, header);
      // End the table cell.
      th.endTH();

    }
  }

  private class CustomHeaderBuilderBis extends AbstractHeaderOrFooterBuilder<ZoneReportSummary> {

    public CustomHeaderBuilderBis() {
      super(salesFixedColumn, false);
      setSortIconStartOfLine(false);
    }

    @Override
    protected boolean buildHeaderOrFooterImpl() {
      if (salesFixedColumn.getColumnCount() > 0) {
        TableRowBuilder tr = startRow();
        String styleDescription = "border-bottom: 2px solid #6f7277;padding: 3px 15px;text-align: left;color: #4b4a4a;text-shadow: #ddf 1px 1px 0;overflow: hidden;height: 60px;";
        TableCellBuilder th = tr.startTH().attribute("style", styleDescription);

        th.text(" ").endTH();
        tr = startRow();
        Header<String> zoneHeader = new TextHeader("");
        buildHeader(tr, zoneHeader, salesFixedColumn.getColumn(0), false, false);
        tr.endTR();

        return true;
      }
      return false;

    }

    /**
     * Renders the header of one column, with the given options.
     * 
     * @param out
     *          the table row to build into
     * @param header
     *          the {@link Header} to render
     * @param column
     *          the column to associate with the header
     * @param sortedColumn
     *          the column that is currently sorted
     * @param isSortAscending
     *          true if the sorted column is in ascending order
     * @param isFirst
     *          true if this the first column
     * @param isLast
     *          true if this the last column
     */
    private void buildHeader(TableRowBuilder out, Header<?> header, Column<ZoneReportSummary, ?> column, boolean isFirst, boolean isLast) {
      // Choose the classes to include with the element.
      Style style = salesFixedColumn.getResources().style();
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
      TableCellBuilder th = out.startTH().className(classesBuilder.toString());
      th.html(new SafeHtmlBuilder().appendEscapedLines("Zone").toSafeHtml()).endTH();
    }
  }

}
