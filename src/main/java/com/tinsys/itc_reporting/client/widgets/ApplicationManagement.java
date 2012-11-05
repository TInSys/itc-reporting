package com.tinsys.itc_reporting.client.widgets;

import java.util.ArrayList;
import java.util.List;

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
import com.tinsys.itc_reporting.client.service.ApplicationService;
import com.tinsys.itc_reporting.client.service.ApplicationServiceAsync;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

public class ApplicationManagement extends Composite implements WidgetSwitchManagement {

    private static Binder uiBinder = GWT.create(Binder.class);
    private ApplicationServiceAsync applicationService = GWT.create(ApplicationService.class);
    private ApplicationDTO selectedApplication;
    private int currentPage = 0;
    private boolean editionInProgress;
    private String oldApplicationVendorIDTextBoxContent;
    private String oldApplicationNameTextBoxContent;
    private boolean reverseSelection = false;

    
    SimplePager.Resources pagerResources = GWT
            .create(SimplePager.Resources.class);
    @UiField
    SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
            pagerResources, false, 0, true);

    ListDataProvider<ApplicationDTO> provider = new ListDataProvider<ApplicationDTO>();
    final Column<ApplicationDTO, String> applicationVendorIDColumn = new Column<ApplicationDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(ApplicationDTO applicationDTO) {
            return applicationDTO.getVendorID();
        }
    };
    final Column<ApplicationDTO, String> applicationNameColumn = new Column<ApplicationDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(ApplicationDTO applicationDTO) {
            return applicationDTO.getName();
        }
    };
   
    @UiField
    CellTable<ApplicationDTO> applicationCellTable = new CellTable<ApplicationDTO>();
    private SingleSelectionModel<ApplicationDTO> selectionModel;

    @UiField
    TextBox applicationVendorIDTextBox = new TextBox();

    @UiField
    TextBox applicationNameTextBox = new TextBox();
    
    @UiField
    Button saveApplication = new Button();

    @UiField
    Button deleteApplication = new Button();

    @UiField
    Button cancelUpdateApplication = new Button();


    
    @UiTemplate("ApplicationManagement.ui.xml")
    interface Binder extends UiBinder<Widget, ApplicationManagement> {
    }
    
    public ApplicationManagement() {
        initWidget(uiBinder.createAndBindUi(this));
        selectionModel = new SingleSelectionModel<ApplicationDTO>();
        applicationCellTable.setTableLayoutFixed(true);
        applicationCellTable.addColumn(applicationVendorIDColumn,"Vendor ID ");
        applicationCellTable.addColumn(applicationNameColumn,"Name ");
        applicationCellTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
        applicationCellTable.setPageSize(15);
        pager.setDisplay(applicationCellTable);
        pager.setRangeLimited(true);

        provider.addDataDisplay(applicationCellTable);
        applicationVendorIDTextBox
                .addValueChangeHandler(new ValueChangeHandler<String>() {
                    public void onValueChange(ValueChangeEvent<String> arg0) {
                        editionInProgress = true;
                        cancelUpdateApplication.setVisible(true);
                    }
                });
        applicationNameTextBox
                .addValueChangeHandler(new ValueChangeHandler<String>() {
                    public void onValueChange(ValueChangeEvent<String> arg0) {
                        editionInProgress = true;
                        cancelUpdateApplication.setVisible(true);
                    }
                });

        applicationVendorIDTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent arg0) {
                editionInProgress = true;
                cancelUpdateApplication.setVisible(true);
                oldApplicationVendorIDTextBoxContent = applicationVendorIDTextBox.getText();
            }
        });

        applicationNameTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent arg0) {
                editionInProgress = true;
                cancelUpdateApplication.setVisible(true);
                oldApplicationNameTextBoxContent = applicationNameTextBox.getText();
            }
        });

        getApplicationList();
    }

    private void getApplicationList() {
        applicationService.getAllApplications(new AsyncCallback<ArrayList<ApplicationDTO>>() {
            
            @Override
            public void onSuccess(ArrayList<ApplicationDTO> result) {
                if (currentPage == 999) {
                    currentPage = ((result.size() - 1) < 0) ? 0
                            : (result.size() - 1) / 10;
                }
                provider.getList().clear();
                provider.getList().addAll(result);
                applicationCellTable.setRowCount(result.size());
                pager.setPage(currentPage);                
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error fetching application list :  "
                        + caught.getMessage());                
            }
        });
    }

    private Handler getSelectionChangeHandler() {
        return new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                if ((editionInProgress && selectionModel.getSelectedObject() != selectedApplication)
                        || reverseSelection) {
                    selectionModel.setSelected(selectedApplication, true);
                    applicationVendorIDTextBox.setText(oldApplicationVendorIDTextBoxContent);
                    applicationNameTextBox.setText(oldApplicationNameTextBoxContent);
                    if (!reverseSelection) {
                       List<String> errors = new ArrayList<String>();
                       errors.add("Please save or cancel changes before changing selection");
                       showSaveAlert(errors);
                       reverseSelection = true;
                    } else {
                        reverseSelection = false;
                    }
                } else {
                    reverseSelection = false;
                    selectedApplication = selectionModel
                            .getSelectedObject();
                    if (selectedApplication != null) {
                        applicationVendorIDTextBox.setText(selectedApplication
                                        .getVendorID());

                                applicationNameTextBox.setText(selectedApplication
                                        .getName());
                                cancelUpdateApplication.setVisible(true);
                                deleteApplication.setVisible(true);
                                saveApplication.setText("Update");
                    }


                    }
                }
            };

        };
    
        @UiHandler("saveApplication")
        void handleClickSave(ClickEvent e) {
            if (applicationVendorIDTextBox.getText().length() > 0
                    && applicationNameTextBox.getText().length() > 0 ) {
                if (selectedApplication == null) {
                    selectedApplication = new ApplicationDTO();
                    selectedApplication.setVendorID(applicationVendorIDTextBox.getText());
                    selectedApplication.setName(applicationNameTextBox.getText());
                    applicationService.createApplication(selectedApplication, new AsyncCallback<ApplicationDTO>() {
                        
                        @Override
                        public void onSuccess(ApplicationDTO result) {
                            currentPage = 999;
                            getApplicationList();
                            applicationVendorIDTextBox.setText("");
                            applicationNameTextBox.setText("");
                            resetUpdateStatus();
                            
                        }
                        
                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert("Error saving new application :"
                                    + caught.getMessage());                            
                        }
                    });

                } else {
                    selectedApplication.setVendorID(applicationVendorIDTextBox.getText());
                    selectedApplication.setName(applicationNameTextBox.getText());
                    currentPage = pager.getPage();
                    applicationService.updateApplication(selectedApplication, new AsyncCallback<ApplicationDTO>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert("Error updating application :"
                                    + caught.getMessage());                                  
                        }

                        @Override
                        public void onSuccess(ApplicationDTO result) {
                            getApplicationList();
                            applicationVendorIDTextBox.setText("");
                            applicationNameTextBox.setText("");
                            resetUpdateStatus();                            
                        }
                    });
                }
            } else {
               List<String> errors = new ArrayList<String>();
               if (applicationVendorIDTextBox.getText().length() == 0){
                  errors.add("Vendor Id should not be empty ");
               }
               if (applicationNameTextBox.getText().length() == 0 ){
                  errors.add("Name should not be empty ");                  
               }
               showSaveAlert(errors);
            }
        }
    
        @UiHandler("cancelUpdateApplication")
        void handleClickCancel(ClickEvent e) {
            resetUpdateStatus();
            currentPage = 999;
        }
        
        @UiHandler("deleteApplication")
        void handleClickDelete(ClickEvent e) {
            deleteApplication();
            currentPage = 999;
        }

        private void deleteApplication() {
            applicationService.deleteApplication(selectedApplication, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Error deleting application :"
                            + caught.getMessage()); 
                }

                @Override
                public void onSuccess(Void result) {
                    currentPage = 999;
                    getApplicationList();
                    resetUpdateStatus();
                }
            });
        }
        
    private void resetUpdateStatus() {
        cancelUpdateApplication.setVisible(false);
        deleteApplication.setVisible(false);
        applicationVendorIDTextBox.setValue("");
        applicationNameTextBox.setValue("");
        applicationCellTable.getSelectionModel().setSelected(null, true);
        selectedApplication = null;
        saveApplication.setText("Save Application");
        editionInProgress = false;            
        }

    @Override
    public boolean isEditing() {
        return this.editionInProgress;
    }
    
    private void showSaveAlert(List<String> content) {
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
