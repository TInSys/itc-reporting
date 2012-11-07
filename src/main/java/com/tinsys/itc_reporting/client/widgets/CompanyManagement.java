package com.tinsys.itc_reporting.client.widgets;

import java.util.ArrayList;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.tinsys.itc_reporting.client.service.CompanyService;
import com.tinsys.itc_reporting.client.service.CompanyServiceAsync;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

public class CompanyManagement extends Composite implements WidgetSwitchManagement {

   private static Binder uiBinder = GWT.create(Binder.class);
   private CompanyServiceAsync companyService = GWT.create(CompanyService.class);
   private CompanyDTO selectedCompany;
   private int currentPage = 0;
   private boolean editionInProgress;
   private String oldCompanyNameTextBoxContent;
   private String oldCompanyCurrencyISOTextBoxContent;
   private boolean reverseSelection = false;

   SimplePager.Resources pagerResources = GWT
         .create(SimplePager.Resources.class);
   @UiField
   SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
         pagerResources, false, 0, true);

   ListDataProvider<CompanyDTO> provider = new ListDataProvider<CompanyDTO>();
   
   final Column<CompanyDTO, String> companyNameColumn = new Column<CompanyDTO, String>(
         new ClickableTextCell()) {

      @Override
      public String getValue(CompanyDTO companyDTO) {
         return companyDTO.getName();
      }
   };
   final Column<CompanyDTO, String> companyCurrencyISOColumn = new Column<CompanyDTO, String>(
         new ClickableTextCell()) {

      @Override
      public String getValue(CompanyDTO companyDTO) {
         return companyDTO.getCurrencyISO();
      }
   };
   @UiField
   CellTable<CompanyDTO> companyCellTable = new CellTable<CompanyDTO>();
   private SingleSelectionModel<CompanyDTO> selectionModel;

   @UiField
   TextBox companyNameTextBox = new TextBox();

   @UiField
   TextBox companyCurrencyISOTextBox = new TextBox();

   @UiField
   Button saveCompany = new Button();

   @UiField
   Button deleteCompany = new Button();

   @UiField
   Button cancelUpdateCompany = new Button();

   @UiTemplate("CompanyManagement.ui.xml")
   interface Binder extends UiBinder<Widget, CompanyManagement> {
   }

   public CompanyManagement() {
      initWidget(uiBinder.createAndBindUi(this));
      selectionModel = new SingleSelectionModel<CompanyDTO>();
      companyCellTable.setTableLayoutFixed(true);
      companyCellTable.addColumn(companyNameColumn, "Name ");
      companyCellTable.addColumn(companyCurrencyISOColumn, "Currency ISO ");
      companyCellTable.setSelectionModel(selectionModel);
      selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
      companyCellTable.setPageSize(15);
      pager.setDisplay(companyCellTable);
      pager.setRangeLimited(true);

      provider.addDataDisplay(companyCellTable);
      companyNameTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
         public void onValueChange(ValueChangeEvent<String> arg0) {
            editionInProgress = true;
            cancelUpdateCompany.setVisible(true);
         }
      });
      companyCurrencyISOTextBox
            .addValueChangeHandler(new ValueChangeHandler<String>() {
               public void onValueChange(ValueChangeEvent<String> arg0) {
                  editionInProgress = true;
                  cancelUpdateCompany.setVisible(true);
               }
            });

      companyNameTextBox.addKeyUpHandler(new KeyUpHandler() {
         public void onKeyUp(KeyUpEvent arg0) {
            editionInProgress = true;
            cancelUpdateCompany.setVisible(true);
            oldCompanyNameTextBoxContent = companyNameTextBox.getText();
         }
      });

      companyCurrencyISOTextBox.addKeyUpHandler(new KeyUpHandler() {
         public void onKeyUp(KeyUpEvent arg0) {
            editionInProgress = true;
            cancelUpdateCompany.setVisible(true);
            oldCompanyCurrencyISOTextBoxContent = companyCurrencyISOTextBox.getText();
         }
      });
      getCompanyList();
   }

   private void getCompanyList() {
      companyService.getAllCompanies(new AsyncCallback<ArrayList<CompanyDTO>>() {

         @Override
         public void onSuccess(ArrayList<CompanyDTO> result) {
            if (currentPage == 999) {
               currentPage = ((result.size() - 1) < 0) ? 0
                     : (result.size() - 1) / 10;
            }
            provider.getList().clear();
            provider.getList().addAll(result);
            companyCellTable.setRowCount(result.size());
            pager.setPage(currentPage);
         }

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error fetching company list :  " + caught.getMessage());
         }
      });
   }

   private Handler getSelectionChangeHandler() {
      return new SelectionChangeEvent.Handler() {

         public void onSelectionChange(SelectionChangeEvent event) {
            if ((editionInProgress && selectionModel.getSelectedObject() != selectedCompany)
                  || reverseSelection) {
               selectionModel.setSelected(selectedCompany, true);
               companyNameTextBox.setText(oldCompanyNameTextBoxContent);
               companyCurrencyISOTextBox.setText(oldCompanyCurrencyISOTextBoxContent);
               if (!reverseSelection) {
                  ArrayList<String> errors = new ArrayList<String>();
                  errors.add("Please save or cancel changes before changing selection");
                  showSaveAlert(errors);
                  reverseSelection = true;
               } else {
                  reverseSelection = false;
               }
            } else {
               reverseSelection = false;
               selectedCompany = selectionModel.getSelectedObject();
               if (selectedCompany != null) {
                  companyNameTextBox.setText(selectedCompany.getName());
                  companyCurrencyISOTextBox.setText(selectedCompany.getCurrencyISO());
                  cancelUpdateCompany.setVisible(true);
                  deleteCompany.setVisible(true);
                  saveCompany.setText("Update");
               }

            }
         }
      };

   };

   @UiHandler("saveCompany")
   void handleClickSave(ClickEvent e) {
      if (companyNameTextBox.getText().length() > 0
            && companyCurrencyISOTextBox.getText().length() > 0) {
         if (selectedCompany == null) {
            selectedCompany = new CompanyDTO();
            selectedCompany.setName(companyNameTextBox.getText());
            selectedCompany.setCurrencyISO(companyCurrencyISOTextBox.getText());
            companyService.createCompany(selectedCompany, new AsyncCallback<CompanyDTO>() {

               @Override
               public void onSuccess(CompanyDTO result) {
                  currentPage = 999;
                  getCompanyList();
                  companyNameTextBox.setText("");
                  companyCurrencyISOTextBox.setText("");
                  resetUpdateStatus();

               }

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error saving new company :" + caught.getMessage());
               }
            });
         } else {
            selectedCompany.setName(companyNameTextBox.getText());
            selectedCompany.setCurrencyISO(companyCurrencyISOTextBox.getText());
            currentPage = pager.getPage();
            companyService.updateCompany(selectedCompany, new AsyncCallback<CompanyDTO>() {

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error updating company :" + caught.getMessage());
               }

               @Override
               public void onSuccess(CompanyDTO result) {
                  getCompanyList();
                  companyNameTextBox.setText("");
                  companyCurrencyISOTextBox.setText("");
                  resetUpdateStatus();
               }
            });
         }
      } else {
         ArrayList<String> errors = new ArrayList<String>();
         if (companyNameTextBox.getText().length() == 0) {
            errors.add("Company name must not be empty");
         }
         if (companyCurrencyISOTextBox.getText().length() == 0) {
            errors.add("Company currency must not be empty");
         }
         showSaveAlert(errors);
      }
   }

   @UiHandler("cancelUpdateCompany")
   void handleClickCancel(ClickEvent e) {
      resetUpdateStatus();
      currentPage = 999;
   }

   @UiHandler("deleteCompany")
   void handleClickDelete(ClickEvent e) {
      deleteCompany();
      currentPage = 999;
   }

   private void deleteCompany() {
      companyService.deleteCompany(selectedCompany, new AsyncCallback<Void>() {

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error deleting company :" + caught.getMessage());

         }

         @Override
         public void onSuccess(Void result) {
            currentPage = 999;
            getCompanyList();
            resetUpdateStatus();
         }
      });
   }

   private void resetUpdateStatus() {
      cancelUpdateCompany.setVisible(false);
      deleteCompany.setVisible(false);
      companyNameTextBox.setValue("");
      companyCurrencyISOTextBox.setValue("");
      companyCellTable.getSelectionModel().setSelected(null, true);
      selectedCompany = null;
      saveCompany.setText("Save Company");
      editionInProgress = false;
   }

   @Override
   public boolean isEditing() {
      return this.editionInProgress;
   }

   private void showSaveAlert(ArrayList<String> errors) {
      final DialogBox simplePopup = new DialogBox(true);
      simplePopup.setWidth("500px");
      simplePopup.setText("");
      VerticalPanel dialogContent = new VerticalPanel();
      for (String error : errors) {
         dialogContent.add(new HTML(error));
      }
      simplePopup.center();
      simplePopup.show();
      simplePopup.setAutoHideEnabled(true);
      Button closeButton = new Button("Fermer", new ClickHandler() {

         public void onClick(ClickEvent arg0) {
            simplePopup.hide();

         }
      });
      dialogContent.add(closeButton);
      simplePopup.add(dialogContent);
   }
}
