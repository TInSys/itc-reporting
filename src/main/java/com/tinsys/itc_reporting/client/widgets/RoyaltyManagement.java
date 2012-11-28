package com.tinsys.itc_reporting.client.widgets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
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
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.client.service.ZoneServiceAsync;
import com.tinsys.itc_reporting.client.widgets.utils.ShuttleBox;
import com.tinsys.itc_reporting.client.widgets.utils.SortableListBox;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class RoyaltyManagement extends Composite implements
      WidgetSwitchManagement {

   private static Binder uiBinder = GWT.create(Binder.class);
   private static final String CALCULATION_ON_SALES = "S";
   private static final String CALCULATION_ON_PROCEEDS = "P";
   private RoyaltyServiceAsync royaltyService = GWT
         .create(RoyaltyService.class);
   private CompanyServiceAsync companyService = GWT
         .create(CompanyService.class);
   private ApplicationServiceAsync applicationService = GWT
         .create(ApplicationService.class);
   private ZoneServiceAsync zoneService = GWT.create(ZoneService.class);
   private RoyaltyDTO selectedRoyalty;
   private int currentPage = 0;
   private boolean editionInProgress;
   private boolean reverseSelection = false;
   private CompanyDTO currentCompany;
   private List<RoyaltyDTO> royaltyList;
   private List<ZoneDTO> allAvailableZones;
   private List<ZoneDTO> availableZones;
   private ArrayList<ApplicationDTO> allAvailableApplications;
   private List<ApplicationDTO> availableApplications;
   private CompanyDTO dummyCompany;
   private ApplicationDTO dummyApplication;
   private String oldShareRateTextBoxContent;
   private String oldShareRateCalculationField;
   private List<ZoneDTO> oldSelectedZones;
   private List<ZoneDTO> oldAvailableZones;
   private int oldApplicationDTO;
   SimplePager.Resources pagerResources = GWT
         .create(SimplePager.Resources.class);

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

   @UiField
   Button addRoyaltyButton;

   @UiField
   ShuttleBox shuttleBox;

   @UiField
   VerticalPanel royaltyDetailsPanel;

   @UiField
   HorizontalPanel applicationListBoxPanel;

   @UiField
   ListBox applicationListBox;

   @UiField
   TextBox shareRateTextBox;

   @UiField
   RadioButton shareOnSalesRadioButton;

   @UiField
   RadioButton shareOnProceedsRadioButton;

   @UiField
   Button cancelUpdateRoyalty;

   @UiField
   Button saveRoyalty;

   @UiField
   Button removeRoyalty;

   @UiTemplate("RoyaltyManagement.ui.xml")
   interface Binder extends UiBinder<Widget, RoyaltyManagement> {
   }

   public RoyaltyManagement() {
      initWidget(uiBinder.createAndBindUi(this));
      companyService
            .getAllCompanies(new AsyncCallback<ArrayList<CompanyDTO>>() {

               @Override
               public void onFailure(Throwable caught) {
                  Window.alert("Error fetching Company list :  "
                        + caught.getMessage());
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
                  zoneService
                        .getAllZones(new AsyncCallback<ArrayList<ZoneDTO>>() {

                           @Override
                           public void onFailure(Throwable caught) {
                              Window.alert("Error fetching Zone list :  "
                                    + caught.getMessage());
                           }

                           @SuppressWarnings("unchecked")
                           @Override
                           public void onSuccess(ArrayList<ZoneDTO> result) {
                              availableZones = result;
                              allAvailableZones = (List<ZoneDTO>) result
                                    .clone();
                              applicationService
                                    .getAllApplications(new AsyncCallback<ArrayList<ApplicationDTO>>() {

                                       @Override
                                       public void onFailure(Throwable caught) {
                                          Window.alert("Error fetching Application list :  "
                                                + caught.getMessage());

                                       }

                                       @Override
                                       public void onSuccess(
                                             ArrayList<ApplicationDTO> result) {
                                          availableApplications = result;
                                          allAvailableApplications = (ArrayList<ApplicationDTO>) result
                                                .clone();

                                       }
                                    });
                           }
                        });
               }
            });
      shuttleBox.addChangeHandler(new ChangeHandler() {
         @Override
         public void onChange(ChangeEvent event) {
            oldAvailableZones = getAvailableZones();
            oldSelectedZones = getSelectedZones();
            editionInProgress = true;
            cancelUpdateRoyalty.setVisible(true);
         }
      });
      shareRateTextBox.addChangeHandler(new ChangeHandler() {

         @Override
         public void onChange(ChangeEvent event) {
            oldShareRateTextBoxContent = shareRateTextBox.getText();
            editionInProgress = true;
            cancelUpdateRoyalty.setVisible(true);
         }
      });
      shareOnSalesRadioButton
            .addValueChangeHandler(new ValueChangeHandler<Boolean>() {

               @Override
               public void onValueChange(ValueChangeEvent<Boolean> event) {
                  if (oldShareRateCalculationField == null) {
                     oldShareRateCalculationField = CALCULATION_ON_PROCEEDS;
                  }
                  editionInProgress = true;
                  cancelUpdateRoyalty.setVisible(true);
               }
            });
      shareOnProceedsRadioButton
            .addValueChangeHandler(new ValueChangeHandler<Boolean>() {

               @Override
               public void onValueChange(ValueChangeEvent<Boolean> event) {
                  if (oldShareRateCalculationField == null) {
                     oldShareRateCalculationField = CALCULATION_ON_SALES;
                  }
                  editionInProgress = true;
                  cancelUpdateRoyalty.setVisible(true);
               }
            });
      selectionModel = new SingleSelectionModel<RoyaltyDTO>();
      royaltyCellTable.setEmptyTableWidget(new HTML(
            "There is no data to display"));
      royaltyCellTable.setTableLayoutFixed(true);
      royaltyCellTable.addColumn(royaltyApplicationColumn,
            "Royalty for Application ");
      royaltyCellTable.setSelectionModel(selectionModel);
      selectionModel.addSelectionChangeHandler(getSelectionChangeHandler());
      royaltyCellTable.setPageSize(15);
      pager.setDisplay(royaltyCellTable);
      pager.setRangeLimited(true);

      provider.addDataDisplay(royaltyCellTable);

      companyListBox
            .addValueChangeHandler(new ValueChangeHandler<CompanyDTO>() {
               @Override
               public void onValueChange(ValueChangeEvent<CompanyDTO> event) {
                  resetUpdateStatus();
                  oldApplicationDTO = 0;
                  oldAvailableZones = new ArrayList<ZoneDTO>();
                  oldSelectedZones = new ArrayList<ZoneDTO>();
                  oldShareRateTextBoxContent = "0";
                  oldShareRateCalculationField = CALCULATION_ON_SALES;
                  currentCompany = event.getValue();
                  getRoyaltyList();

               }
            });
      applicationListBox.addChangeHandler(new ChangeHandler() {

         @Override
         public void onChange(ChangeEvent event) {
            oldApplicationDTO = ((ListBox) event.getSource())
                  .getSelectedIndex();
            editionInProgress = true;
         }
      });

   }

   protected void resetUpdateStatus() {
      royaltyCellTable.getSelectionModel().setSelected(null, true);
      shareRateTextBox.setText("0");
      shareOnSalesRadioButton.setValue(true);
      applicationListBox.clear();
      availableApplications = allAvailableApplications;
      selectedRoyalty = null;
      royaltyDetailsPanel.setVisible(false);
      cancelUpdateRoyalty.setVisible(false);
      removeRoyalty.setVisible(true);
      editionInProgress = false;
   }

   private void getRoyaltyList() {
      royaltyService.getAllRoyalty(currentCompany,
            new AsyncCallback<List<RoyaltyDTO>>() {

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
               shareRateTextBox.setText(oldShareRateTextBoxContent);
               shareOnSalesRadioButton.setValue(oldShareRateCalculationField
                     .equals(CALCULATION_ON_SALES));
               shareOnProceedsRadioButton.setValue(oldShareRateCalculationField
                     .equals(CALCULATION_ON_PROCEEDS));
               shuttleBox.getAvailableItemsListbox().clear();
               for (ZoneDTO zoneDTO : oldAvailableZones) {
                  shuttleBox.getAvailableItemsListbox().addItem(
                        zoneDTO.getName(), String.valueOf(zoneDTO.getId()));
               }
               shuttleBox.getSelectedItemsListbox().clear();
               for (ZoneDTO zoneDTO : oldSelectedZones) {
                  shuttleBox.getSelectedItemsListbox().addItem(
                        zoneDTO.getName(), String.valueOf(zoneDTO.getId()));
               }
               applicationListBox.setSelectedIndex(oldApplicationDTO);
               shuttleBox.refreshLayout();
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
                  selectionModel.setSelected(selectedRoyalty, true);
                  SortableListBox availableZonesListbox = new SortableListBox(
                        true);
                  SortableListBox selectedZonesListbox = new SortableListBox(
                        true);
                  availableZones.clear();
                  availableZones.addAll(allAvailableZones);
                  availableZones.removeAll(selectedRoyalty.getZones());
                  for (ZoneDTO zoneDTO : availableZones) {
                     availableZonesListbox.addItem(zoneDTO.getName(),
                           String.valueOf(zoneDTO.getId()));
                  }
                  shuttleBox.setAvailableItemsListbox(availableZonesListbox);
                  for (ZoneDTO zoneDTO : selectedRoyalty.getZones()) {
                     selectedZonesListbox.addItem(zoneDTO.getName(),
                           String.valueOf(zoneDTO.getId()));
                  }
                  shuttleBox.setSelectedItemsListbox(selectedZonesListbox);
                  shuttleBox.refreshLayout();
                  oldAvailableZones = getAvailableZones();
                  oldSelectedZones = getSelectedZones();
                  shareRateTextBox.setText(selectedRoyalty.getShareRate()
                        .toPlainString());
                  shareOnSalesRadioButton.setValue(selectedRoyalty
                        .getShareRateCalculationField().equals(
                              CALCULATION_ON_SALES));
                  shareOnProceedsRadioButton.setValue(selectedRoyalty
                        .getShareRateCalculationField().equals(
                              CALCULATION_ON_PROCEEDS));

                  applicationListBoxPanel.setVisible(false);
                  royaltyDetailsPanel.setVisible(true);

               }
            }
         }
      };

   };

   @UiHandler("cancelUpdateRoyalty")
   void handleCancelUpdate(ClickEvent e) {
      resetUpdateStatus();
   }

   @UiHandler("saveRoyalty")
   void handleSaveRoyalty(ClickEvent e) {
      if (royaltyIsValid()) {
         if (selectedRoyalty == null) {
            selectedRoyalty = new RoyaltyDTO();
            selectedRoyalty.setCompany(currentCompany);
            selectedRoyalty.setZones(new ArrayList<ZoneDTO>());
            for (ApplicationDTO applicationDTO : availableApplications) {
               if (applicationDTO.getId() == Integer
                     .parseInt(applicationListBox.getValue(applicationListBox
                           .getSelectedIndex()))) {
                  selectedRoyalty.setApplication(applicationDTO);
                  break;
               }
            }
         }
         selectedRoyalty
               .setShareRate(new BigDecimal(shareRateTextBox.getText()));
         selectedRoyalty.setShareRateCalculationField((shareOnSalesRadioButton
               .getValue()) ? CALCULATION_ON_SALES : CALCULATION_ON_PROCEEDS);
         List<ZoneDTO> tmpZones = getSelectedZones();
         selectedRoyalty.setZones(tmpZones);
         if (selectedRoyalty.getId() != null) {
            royaltyService.updateRoyalty(selectedRoyalty,
                  new AsyncCallback<RoyaltyDTO>() {

                     @Override
                     public void onFailure(Throwable caught) {
                        Window.alert("Error saving Royalty :  "
                              + caught.getMessage());
                     }

                     @Override
                     public void onSuccess(RoyaltyDTO result) {
                        resetUpdateStatus();

                     }
                  });
         } else {
            royaltyService.createRoyalty(selectedRoyalty,
                  new AsyncCallback<RoyaltyDTO>() {

                     @Override
                     public void onFailure(Throwable caught) {
                        Window.alert("Error saving Royalty :  "
                              + caught.getMessage());
                     }

                     @Override
                     public void onSuccess(RoyaltyDTO result) {
                        resetUpdateStatus();
                        getRoyaltyList();
                     }
                  });
         }
      }
   }

   @SuppressWarnings("unchecked")
   @UiHandler("addRoyaltyButton")
   void handleAddRoyalty(ClickEvent e) {
      if (editionInProgress) {
         List<String> errors = new ArrayList<String>();
         errors.add("Please save or cancel current changes before adding a Royalty");
         showAlert(errors);
      } else {
         if (currentCompany != null && currentCompany != dummyCompany) {
            resetUpdateStatus();
            oldApplicationDTO = 0;
            oldShareRateTextBoxContent = null;
            oldShareRateCalculationField = null;
            selectedRoyalty = null;
            availableApplications = (List<ApplicationDTO>) allAvailableApplications
                  .clone();
            if (royaltyList != null) {
               for (RoyaltyDTO royaltyDTO : royaltyList) {
                  availableApplications.remove(royaltyDTO.getApplication());
               }
            }
            dummyApplication = new ApplicationDTO();
            dummyApplication.setId(-1L);
            dummyApplication.setName("Please select an Application");
            applicationListBox.addItem(dummyApplication.getName(),
                  String.valueOf(dummyApplication.getId()));
            for (ApplicationDTO applicationDTO : availableApplications) {
               applicationListBox.addItem(applicationDTO.getName(),
                     String.valueOf(applicationDTO.getId()));
            }
            shuttleBox.reset();
            availableZones.clear();
            availableZones.addAll(allAvailableZones);
            for (ZoneDTO zoneDTO : availableZones) {
               shuttleBox.getAvailableItemsListbox().addItem(zoneDTO.getName(),
                     String.valueOf(zoneDTO.getId()));
            }
            shuttleBox.refreshLayout();
            oldAvailableZones = getAvailableZones();
            oldSelectedZones = getSelectedZones();
            applicationListBoxPanel.setVisible(true);
            royaltyDetailsPanel.setVisible(true);
            removeRoyalty.setVisible(false);
            cancelUpdateRoyalty.setVisible(true);
            editionInProgress = true;
         }
      }
   }

   private List<ZoneDTO> getAvailableZones() {
      ArrayList<ZoneDTO> tmpZone = new ArrayList<ZoneDTO>();
      for (int i = 0; i < shuttleBox.getAvailableItemsListbox().getItemCount(); i++) {
         for (ZoneDTO zoneDTO : allAvailableZones) {
            if (zoneDTO.getId() == Integer.parseInt(shuttleBox
                  .getAvailableItemsListbox().getValue(i))) {
               tmpZone.add(zoneDTO);
               break;
            }
         }
      }
      return tmpZone;
   }

   private List<ZoneDTO> getSelectedZones() {
      ArrayList<ZoneDTO> tmpZone = new ArrayList<ZoneDTO>();
      for (int i = 0; i < shuttleBox.getSelectedItemsListbox().getItemCount(); i++) {
         for (ZoneDTO zoneDTO : allAvailableZones) {
            if (zoneDTO.getId() == Integer.parseInt(shuttleBox
                  .getSelectedItemsListbox().getValue(i))) {
               tmpZone.add(zoneDTO);
               break;
            }
         }
      }
      return tmpZone;
   }

   private boolean royaltyIsValid() {
      List<String> errors = new ArrayList<String>();
      boolean valid = true;
      if (currentCompany == dummyCompany || currentCompany == null) {
         errors.add("There's no company selected !");
         valid = false;
      }
      if ((selectedRoyalty == null && applicationListBox.getSelectedIndex() <= 0)
            || (selectedRoyalty != null && selectedRoyalty.getApplication() == null)) {
         errors.add("Please select an application");
         valid = false;
      }
      if (!shareRateTextBox.getText().matches("\\-?[0-9]*\\.?[0-9]*")
            || shareRateTextBox.getText().length() == 0) {
         errors.add("Share Rate should not be empty and be numeric");
         valid = false;
      }
      if (shuttleBox.getSelectedItemsListbox().getItemCount() == 0) {
         errors.add("You should add at least one zone");
         valid = false;
      }

      showAlert(errors);
      return valid;
   }

   @UiHandler("removeRoyalty")
   void handleRemoveRoyalty(ClickEvent e) {
      royaltyService.deleteRoyalty(selectedRoyalty, new AsyncCallback<Void>() {

         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Error deleting Royalty :  " + caught.getMessage());

         }

         @Override
         public void onSuccess(Void result) {
            resetUpdateStatus();
            getRoyaltyList();
         }
      });
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
