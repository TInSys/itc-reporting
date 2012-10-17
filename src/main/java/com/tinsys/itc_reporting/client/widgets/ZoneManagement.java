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
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.client.service.ZoneServiceAsync;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class ZoneManagement extends Composite implements WidgetSwitchManagement {

    private static Binder uiBinder = GWT.create(Binder.class);
    private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
    private ZoneDTO selectedZone;
    private int currentPage = 0;
    private boolean editionInProgress;
    private String oldZoneCodeTextBoxContent;
    private String oldZoneNameTextBoxContent;
    private String oldZoneCurrencyISOTextBoxContent;
    private boolean reverseSelection = false;

    
    SimplePager.Resources pagerResources = GWT
            .create(SimplePager.Resources.class);
    @UiField
    SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER,
            pagerResources, false, 0, true);

    ListDataProvider<ZoneDTO> provider = new ListDataProvider<ZoneDTO>();
    final Column<ZoneDTO, String> zoneCodeColumn = new Column<ZoneDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(ZoneDTO zoneDTO) {
            return zoneDTO.getCode();
        }
    };
    final Column<ZoneDTO, String> zoneNameColumn = new Column<ZoneDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(ZoneDTO zoneDTO) {
            return zoneDTO.getName();
        }
    };
    final Column<ZoneDTO, String> zoneCurrencyISOColumn = new Column<ZoneDTO, String>(
            new ClickableTextCell()) {

        @Override
        public String getValue(ZoneDTO zoneDTO) {
            return zoneDTO.getCurrencyISO();
        }
    };    
    @UiField
    CellTable<ZoneDTO> zoneCellTable = new CellTable<ZoneDTO>();
    private SingleSelectionModel<ZoneDTO> selectionModel;

    @UiField
    TextBox zoneCodeTextBox = new TextBox();

    @UiField
    TextBox zoneNameTextBox = new TextBox();
    
    @UiField
    TextBox zoneCurrencyISOTextBox = new TextBox();
    
    @UiField
    Button saveZone = new Button();

    @UiField
    Button deleteZone = new Button();

    @UiField
    Button cancelUpdateZone = new Button();


    
    @UiTemplate("ZoneManagement.ui.xml")
    interface Binder extends UiBinder<Widget, ZoneManagement> {
    }
    
    public ZoneManagement() {
        initWidget(uiBinder.createAndBindUi(this));
        selectionModel = new SingleSelectionModel<ZoneDTO>();
        zoneCellTable.setTableLayoutFixed(true);
        zoneCellTable.addColumn(zoneCodeColumn,"Code ");
        zoneCellTable.addColumn(zoneNameColumn,"Name ");
        zoneCellTable.addColumn(zoneCurrencyISOColumn,"Currency ISO ");
        zoneCellTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
        zoneCellTable.setPageSize(15);
        pager.setDisplay(zoneCellTable);
        pager.setRangeLimited(true);

        provider.addDataDisplay(zoneCellTable);
        zoneCodeTextBox
                .addValueChangeHandler(new ValueChangeHandler<String>() {
                    public void onValueChange(ValueChangeEvent<String> arg0) {
                        editionInProgress = true;
                        cancelUpdateZone.setVisible(true);
                    }
                });
        zoneNameTextBox
                .addValueChangeHandler(new ValueChangeHandler<String>() {
                    public void onValueChange(ValueChangeEvent<String> arg0) {
                        editionInProgress = true;
                        cancelUpdateZone.setVisible(true);
                    }
                });
        zoneCurrencyISOTextBox
        .addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> arg0) {
                editionInProgress = true;
                cancelUpdateZone.setVisible(true);
            }
        });
        zoneCodeTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent arg0) {
                editionInProgress = true;
                cancelUpdateZone.setVisible(true);
                oldZoneCodeTextBoxContent = zoneCodeTextBox.getText();
            }
        });

        zoneNameTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent arg0) {
                editionInProgress = true;
                cancelUpdateZone.setVisible(true);
                oldZoneNameTextBoxContent = zoneNameTextBox.getText();
            }
        });

        zoneCurrencyISOTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent arg0) {
                editionInProgress = true;
                cancelUpdateZone.setVisible(true);
                oldZoneCurrencyISOTextBoxContent = zoneCurrencyISOTextBox.getText();
            }
        });
        getZoneList();
    }

    private void getZoneList() {
        zoneService.getAllZones(new AsyncCallback<ArrayList<ZoneDTO>>() {
            
            @Override
            public void onSuccess(ArrayList<ZoneDTO> result) {
                if (currentPage == 999) {
                    currentPage = ((result.size() - 1) < 0) ? 0
                            : (result.size() - 1) / 10;
                }
                provider.getList().clear();
                provider.getList().addAll(result);
                zoneCellTable.setRowCount(result.size());
                pager.setPage(currentPage);                
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error fetching zone list :  "
                        + caught.getMessage());                
            }
        });
    }

    private Handler getSelectionChangeHandler() {
        return new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                if ((editionInProgress && selectionModel.getSelectedObject() != selectedZone)
                        || reverseSelection) {
                    selectionModel.setSelected(selectedZone, true);
                    zoneCodeTextBox.setText(oldZoneCodeTextBoxContent);
                    zoneNameTextBox.setText(oldZoneNameTextBoxContent);
                    zoneCurrencyISOTextBox.setText(oldZoneCurrencyISOTextBoxContent);
                    if (!reverseSelection) {
                        showSaveAlert();
                        reverseSelection = true;
                    } else {
                        reverseSelection = false;
                    }
                } else {
                    reverseSelection = false;
                    selectedZone = selectionModel
                            .getSelectedObject();
                    if (selectedZone != null) {
                                zoneCodeTextBox.setText(selectedZone
                                        .getCode());

                                zoneNameTextBox.setText(selectedZone
                                        .getName());
                                zoneCurrencyISOTextBox.setText(selectedZone.getCurrencyISO());
                                cancelUpdateZone.setVisible(true);
                                deleteZone.setVisible(true);
                                saveZone.setText("Update");
                    }


                    }
                }
            };

        };
    
        @UiHandler("saveZone")
        void handleClickSave(ClickEvent e) {
            if (zoneCodeTextBox.getText().length() > 0
                    && zoneNameTextBox.getText().length() > 0 && zoneCurrencyISOTextBox.getText().length() > 0) {
                if (selectedZone == null) {
                    selectedZone = new ZoneDTO();
                    selectedZone.setCode(zoneCodeTextBox.getText());
                    selectedZone.setName(zoneNameTextBox.getText());
                    selectedZone.setCurrencyISO(zoneCurrencyISOTextBox.getText());
                    zoneService.createZone(selectedZone, new AsyncCallback<ZoneDTO>() {
                        
                        @Override
                        public void onSuccess(ZoneDTO result) {
                            currentPage = 999;
                            getZoneList();
                            zoneCodeTextBox.setText("");
                            zoneNameTextBox.setText("");
                            zoneCurrencyISOTextBox.setText("");
                            resetUpdateStatus();
                            
                        }
                        
                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert("Error saving new zone :"
                                    + caught.getMessage());                            
                        }
                    });
                    

                } else {
                    selectedZone.setCode(zoneCodeTextBox.getText());
                    selectedZone.setName(zoneNameTextBox.getText());
                    selectedZone.setCurrencyISO(zoneCurrencyISOTextBox.getText());
                    currentPage = pager.getPage();
                    zoneService.updateZone(selectedZone, new AsyncCallback<ZoneDTO>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert("Error updating zone :"
                                    + caught.getMessage());                                  
                        }

                        @Override
                        public void onSuccess(ZoneDTO result) {
                            getZoneList();
                            zoneCodeTextBox.setText("");
                            zoneNameTextBox.setText("");
                            zoneCurrencyISOTextBox.setText("");
                            resetUpdateStatus();                            
                        }
                    });
                }
            }
        }
    
        @UiHandler("cancelUpdateZone")
        void handleClickCancel(ClickEvent e) {
            resetUpdateStatus();
            currentPage = 999;
        }
        
        @UiHandler("deleteZone")
        void handleClickDelete(ClickEvent e) {
            deletePredefinedComment();
            currentPage = 999;
        }

        private void deletePredefinedComment() {
            zoneService.deleteZone(selectedZone, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Error deleting zone :"
                            + caught.getMessage()); 
                    
                }

                @Override
                public void onSuccess(Void result) {
                    currentPage = 999;
                    getZoneList();
                    resetUpdateStatus();
                }
            });
        }
        
    private void resetUpdateStatus() {
        cancelUpdateZone.setVisible(false);
        deleteZone.setVisible(false);
        zoneCodeTextBox.setValue("");
        zoneNameTextBox.setValue("");
        zoneCurrencyISOTextBox.setValue("");
        zoneCellTable.getSelectionModel().setSelected(null, true);
        selectedZone = null;
        saveZone.setText("Save Zone");
        editionInProgress = false;            
        }

    @Override
    public boolean isEditing() {
        return this.editionInProgress;
    }
    
    private void showSaveAlert() {
        final DialogBox simplePopup = new DialogBox(true);
        simplePopup.setWidth("500px");
        simplePopup.setText("");
        VerticalPanel dialogContent = new VerticalPanel();
        dialogContent
                .add(new HTML(
                        "Please save or cancel changes before changing selection"));
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
