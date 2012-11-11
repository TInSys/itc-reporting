package com.tinsys.itc_reporting.client.widgets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.tinsys.itc_reporting.client.service.ApplicationService;
import com.tinsys.itc_reporting.client.service.ApplicationServiceAsync;
import com.tinsys.itc_reporting.client.service.CompanyService;
import com.tinsys.itc_reporting.client.service.CompanyServiceAsync;
import com.tinsys.itc_reporting.client.service.RoyaltyService;
import com.tinsys.itc_reporting.client.service.RoyaltyServiceAsync;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public class RoyaltyManagement extends Composite implements
      WidgetSwitchManagement {

   private static Binder uiBinder = GWT.create(Binder.class);
   private RoyaltyServiceAsync royaltyService = GWT.create(RoyaltyService.class);
   private CompanyServiceAsync companyService = GWT.create(CompanyService.class);
   private ApplicationServiceAsync applicationService = GWT.create(ApplicationService.class);
   private RoyaltyDTO selectedRoyalty;
   private int currentPage = 0;
   private boolean editionInProgress;
   private String oldFXRateRateTextBoxContent;
   private String oldFXRateCurrencyTextBoxContent;
   private int oldFXRateMonthPeriodValue;
   private int oldFXRateYearPeriodValue;
   private boolean reverseSelection = false;
   private CompanyDTO currentCompany;
   private List<RoyaltyDTO> royaltyList;
   private static int STARTING_YEAR = 2008;
   private int currentYear;
   private CompanyDTO dummyCompany;
   private PreferencesDTO prefs;
   
   SimplePager.Resources pagerResources = GWT
         .create(SimplePager.Resources.class);

   @UiField(provided = true)
   ValueListBox<CompanyDTO> companyListBox = new ValueListBox<CompanyDTO>(
         new Renderer<CompanyDTO>() {

            @Override
            public String render(CompanyDTO object) {
               if (object != null) {
                  return (object.getName() + " " + object
                        .getCurrencyISO());
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
   SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
         pagerResources, false, 0, true);

   ListDataProvider<RoyaltyDTO> provider = new ListDataProvider<RoyaltyDTO>();
   final Column<RoyaltyDTO, String> royaltyApplicationColumn = new Column<RoyaltyDTO, String>(
         new ClickableTextCell()) {

      @Override
      public String getValue(RoyaltyDTO royaltyDTO) {
            return royaltyDTO.getApplication().getName();
      }
   };
   
   @UiField
   CellTable<RoyaltyDTO> royaltyCellTable = new CellTable<RoyaltyDTO>();
   private SingleSelectionModel<RoyaltyDTO> selectionModel;

   @UiTemplate("RoyaltyManagement.ui.xml")
   interface Binder extends UiBinder<Widget, RoyaltyManagement> {
   }

   public void setPrefs(PreferencesDTO prefs) {
      this.prefs = prefs;
   }

   public PreferencesDTO getPrefs() {
      return prefs;
   }

   public RoyaltyManagement() {
      initWidget(uiBinder.createAndBindUi(this));
      companyService.getAllCompanies(new AsyncCallback<ArrayList<CompanyDTO>>() {

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error fetching zone list :  " + caught.getMessage());
         }

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
         }
      });

      Date today = new Date();
      currentYear = new Integer(DateTimeFormat.getFormat("yyyy").format(today));

      selectionModel = new SingleSelectionModel<RoyaltyDTO>();
      royaltyCellTable.setTableLayoutFixed(true);
      royaltyCellTable.addColumn(royaltyApplicationColumn, "Royalty App. ");
      royaltyCellTable.setSelectionModel(selectionModel);
      selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
      royaltyCellTable.setPageSize(15);
      pager.setDisplay(royaltyCellTable);
      pager.setRangeLimited(true);

      provider.addDataDisplay(royaltyCellTable);
     
      companyListBox.addValueChangeHandler(new ValueChangeHandler<CompanyDTO>() {
         @Override
         public void onValueChange(ValueChangeEvent<CompanyDTO> event) {
            if (event.getValue().getId() < 0) {
             //  resetUpdateStatus();
            } else {
               currentCompany = event.getValue();
               getFXRateList();
            }

         }
      });
   }

   private void getFXRateList() {
       royaltyService.getAllRoyalty(currentCompany, new AsyncCallback<List<RoyaltyDTO>>() {
        
        @Override
        public void onSuccess(List<RoyaltyDTO> result) {
            if (currentPage == 999) {
                currentPage = ((result.size() - 1) < 0) ? 0 : (result
                      .size() - 1) / 10;
             }
             provider.getList().clear();
             provider.getList().addAll(result);
             royaltyList = result;
             royaltyCellTable.setRowCount(result.size());
             pager.setPage(currentPage);
            
        }
        
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Error fetching fxRate list :  "
                    + caught.getMessage());            
        }
    });
   }

   private Handler getSelectionChangeHandler() {
      return new SelectionChangeEvent.Handler() {

         public void onSelectionChange(SelectionChangeEvent event) {
            if ((editionInProgress && selectionModel.getSelectedObject() != selectedRoyalty)
                  || reverseSelection) {
               selectionModel.setSelected(selectedRoyalty, true);
               //TODO update widget
/*               fxRateRateTextBox.setText(oldFXRateRateTextBoxContent);
               currencyISOTextBox.setText(oldFXRateCurrencyTextBoxContent);
               monthPeriod.setItemSelected(oldFXRateMonthPeriodValue, true);
               yearPeriod.setItemSelected(oldFXRateYearPeriodValue, true);*/

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
               selectedRoyalty = selectionModel.getSelectedObject();
               if (selectedRoyalty != null) {
//TODO show rate and zone widget with values + show delete button to remove royalty from application

               }
            }
         }
      };

   };


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
