package com.tinsys.itc_reporting.client.widgets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.tinsys.itc_reporting.client.service.CompanyService;
import com.tinsys.itc_reporting.client.service.CompanyServiceAsync;
import com.tinsys.itc_reporting.client.service.RoyaltyReportService;
import com.tinsys.itc_reporting.client.service.RoyaltyReportServiceAsync;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyReportLine;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class RoyaltiesReport extends Composite implements
        WidgetSwitchManagement {

    private static Binder uiBinder = GWT.create(Binder.class);
    private RoyaltyReportServiceAsync royaltyReportService = GWT
            .create(RoyaltyReportService.class);
    private CompanyServiceAsync companyService = GWT
    .create(CompanyService.class);
    private boolean editionInProgress;
    private CompanyDTO currentCompany;
    private CompanyDTO dummyCompany;

    private static int STARTING_YEAR = 2008;
    private int startYear;
    private int startMonth;
    private int endYear;
    private int endMonth;

    @UiField(provided = true)
    ValueListBox<CompanyDTO> companyListBox = new ValueListBox<CompanyDTO>(
          new Renderer<CompanyDTO>() {

             @Override
             public String render(CompanyDTO object) {
                if (object != null) {
                   return (object.getName() + " " + object.getCurrencyISO());
                }
                return null;
             }

             @Override
             public void render(CompanyDTO object, Appendable appendable)
                   throws IOException {
                String s = render(object);
                appendable.append(s);

             }
          });


    @UiField
    ListBox startMonthPeriodListBox = new ListBox();

    @UiField
    ListBox startYearPeriodListBox = new ListBox();

    @UiField
    ListBox endMonthPeriodListBox = new ListBox();

    @UiField
    ListBox endYearPeriodListBox = new ListBox();
    
    @UiField
    HorizontalPanel widgetRootPanel = new HorizontalPanel();

    @UiField
    FlexTable royaltyReport;
    
    protected ArrayList<ZoneDTO> zoneList;

    @UiTemplate("RoyaltiesReport.ui.xml")
    interface Binder extends UiBinder<Widget, RoyaltiesReport> {
    }

    public RoyaltiesReport() {
        initWidget(uiBinder.createAndBindUi(this));

        for (int i = 1; i <= 12; i++) {
            startMonthPeriodListBox.addItem(Integer.toString(i));
        }
        for (int i = STARTING_YEAR; i <= 2040; i++) {
            startYearPeriodListBox.addItem(Integer.toString(i));
        }
        
        for (int i = 1; i <= 12; i++) {
           endMonthPeriodListBox.addItem(Integer.toString(i));
       }
       for (int i = STARTING_YEAR; i <= 2040; i++) {
           endYearPeriodListBox.addItem(Integer.toString(i));
       }

        Date today = new Date();
        startMonth = new Integer(DateTimeFormat.getFormat("MM").format(today));
        startYear = new Integer(DateTimeFormat.getFormat("yyyy")
                .format(today));
        endMonth = new Integer(startMonth);
        endYear = new Integer(startYear);
        startYearPeriodListBox.setSelectedIndex(startYear - STARTING_YEAR);
        startMonthPeriodListBox.setSelectedIndex(startMonth - 1);
        endYearPeriodListBox.setSelectedIndex(endYear - STARTING_YEAR);
        endMonthPeriodListBox.setSelectedIndex(endMonth - 1);
        companyService.getAllCompanies(new AsyncCallback<ArrayList<CompanyDTO>>() {
         
         @Override
         public void onSuccess(ArrayList<CompanyDTO> result) {
               dummyCompany = new CompanyDTO();
               dummyCompany.setName("Please choose a Company");
               dummyCompany.setCurrencyISO("");
               dummyCompany.setId(-1L);
               result.add(0, dummyCompany);
               currentCompany = dummyCompany;
               companyListBox.setValue(result.get(0));
               companyListBox.setAcceptableValues(result);  
               companyListBox
               .addValueChangeHandler(new ValueChangeHandler<CompanyDTO>() {
                  @Override
                  public void onValueChange(ValueChangeEvent<CompanyDTO> event) {
                     currentCompany = event.getValue();
                  }
               });      
         
         }         
         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error fetching Company list :  "
                  + caught.getMessage());            
         }
      });
        
    }

    private void getReport() {
        FiscalPeriodDTO startMonthPeriodDto = new FiscalPeriodDTO();
        startMonthPeriodDto.setId(null);
        startMonthPeriodDto.setMonth(startMonthPeriodListBox.getSelectedIndex() + 1);
        startMonthPeriodDto.setYear(startYearPeriodListBox.getSelectedIndex() + STARTING_YEAR);
        FiscalPeriodDTO endMonthPeriodDto = new FiscalPeriodDTO();
        endMonthPeriodDto.setId(null);
        endMonthPeriodDto.setMonth(endMonthPeriodListBox.getSelectedIndex() + 1);
        endMonthPeriodDto.setYear(endYearPeriodListBox.getSelectedIndex() + STARTING_YEAR);

        royaltyReportService.getCompanyReport(currentCompany, startMonthPeriodDto, endMonthPeriodDto, new AsyncCallback<List<RoyaltyReportLine>>() {
         
         @Override
         public void onSuccess(List<RoyaltyReportLine> result) {
            int lineIdx = 0;
            FiscalPeriodDTO currentPeriod = null;
            ApplicationDTO currentApplication = null;
            ZoneDTO currentZone = null;
            BigDecimal currentPrice = null;
            BigDecimal periodRoyalty = new BigDecimal(0);
            BigDecimal totalRoyalty = new BigDecimal(0);
            
            
            for (RoyaltyReportLine sales : result) {
                if (lineIdx == 0){
                    currentPeriod = sales.getPeriod();
                    currentApplication = sales.getApplication();
                    currentZone = sales.getZone();
                    currentPrice = sales.getOriginalCurrencyAmount();
                    royaltyReport.setText(lineIdx,0,sales.getPeriod().toString());
                    lineIdx +=1;
                }
               // period total 
               if (currentPeriod.getId() != sales.getPeriod().getId() ) {
                   // print period total and show new total header
                       lineIdx +=1;
                       royaltyReport.setText(lineIdx,0,"Total Royalties for period "+currentPeriod+" = "+periodRoyalty+" "+sales.getReferenceCurrency());
                       currentPeriod = sales.getPeriod();
                       lineIdx +=2;
                       totalRoyalty = totalRoyalty.add(periodRoyalty);
                       periodRoyalty = new BigDecimal(0);
                       royaltyReport.setText(lineIdx,0," ");
                       royaltyReport.setText(lineIdx,0,sales.getPeriod().toString());
               
                       lineIdx +=2;
                   } 
                       
                       royaltyReport.setText(lineIdx,1,sales.getApplication().getVendorID());
                       royaltyReport.setText(lineIdx,2,sales.getZone().getName());
                       royaltyReport.setText(lineIdx,3,String.valueOf(sales.getSalesNumber()));
                       royaltyReport.getCellFormatter().setAlignment(lineIdx, 3, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
                       royaltyReport.setText(lineIdx,4,(sales.getOriginalCurrencyAmount().toString()+" "+sales.getOriginalCurrency()));
                       royaltyReport.getCellFormatter().setAlignment(lineIdx, 4, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
                       royaltyReport.setText(lineIdx,5,(sales.getOriginalCurrencyTotalAmount().toString()+" "+sales.getOriginalCurrency()));
                       royaltyReport.getCellFormatter().setAlignment(lineIdx, 5, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
                       royaltyReport.setText(lineIdx,6,(sales.getReferenceCurrencyProceedsAfterTaxTotalAmount().toString()+" "+sales.getReferenceCurrency()));
                       royaltyReport.getCellFormatter().setAlignment(lineIdx, 6, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
                       royaltyReport.setText(lineIdx,7,(sales.getReferenceCurrencyCompanyRoyaltiesTotalAmount().toString()+" "+sales.getReferenceCurrency()));
                       royaltyReport.getCellFormatter().setAlignment(lineIdx, 7, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
                       periodRoyalty = periodRoyalty.add(sales.getReferenceCurrencyCompanyRoyaltiesTotalAmount());
                       
               lineIdx += 1;
            }
            totalRoyalty = totalRoyalty.add(periodRoyalty);
            royaltyReport.setText(lineIdx,0," ");
            lineIdx += 1;
            royaltyReport.setText(lineIdx,0,"Total Royalties for period "+currentPeriod+" = "+periodRoyalty);
            lineIdx +=3;
            royaltyReport.setText(lineIdx,0,"Grand Total Royalties  = "+totalRoyalty);
            lineIdx +=2;
            royaltyReport.setText(lineIdx,0,"End of Report ");
         }
         
         @Override
         public void onFailure(Throwable caught) {
             Window.alert("Error fetching Roaylties report lines :  "
                     + caught.getMessage()); 
         }
      });
        
    }



    @Override
    public boolean isEditing() {
        return this.editionInProgress;
    }

    @UiHandler("showReportButton")
    void onClickShowReportButton(ClickEvent e) {
        getReport();
    }

}
