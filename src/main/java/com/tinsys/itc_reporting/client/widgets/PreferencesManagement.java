package com.tinsys.itc_reporting.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tinsys.itc_reporting.client.service.PreferencesService;
import com.tinsys.itc_reporting.client.service.PreferencesServiceAsync;
import com.tinsys.itc_reporting.shared.dto.PreferencesDTO;

public class PreferencesManagement extends Composite implements WidgetSwitchManagement {

  private static Binder uiBinder = GWT.create(Binder.class);
  private PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);
  private boolean editionInProgress;
  private String oldReferenceCurrencyTextBoxContent;
  private PreferencesDTO preferences;

  public PreferencesDTO getPreferences() {
    return preferences;
  }

  public void setPreferences(PreferencesDTO preferences) {
    this.preferences = preferences;
  }

  @UiField
  TextBox referenceCurrencyTextBox = new TextBox();

  @UiField
  Button savePreferences = new Button();

  @UiField
  Button cancelUpdatePreferences = new Button();

  @UiTemplate("PreferencesManagement.ui.xml")
  interface Binder extends UiBinder<Widget, PreferencesManagement> {
  }

  public PreferencesManagement(PreferencesDTO thePreferences) {
    initWidget(uiBinder.createAndBindUi(this));
    preferences = thePreferences;
    referenceCurrencyTextBox.setText(thePreferences.getReferenceCurrency());
    oldReferenceCurrencyTextBoxContent = referenceCurrencyTextBox.getText();

    referenceCurrencyTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> arg0) {
        editionInProgress = true;
        cancelUpdatePreferences.setVisible(true);
      }
    });

    referenceCurrencyTextBox.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent arg0) {
        editionInProgress = true;
        cancelUpdatePreferences.setVisible(true);
      }
    });
  }

  @UiHandler("savePreferences")
  void handleClickSave(ClickEvent e) {
    if (referenceCurrencyTextBox.getText().length() > 0) {
      {
        preferences.setReferenceCurrency(referenceCurrencyTextBox.getText());

        preferencesService.updatePreference(preferences, new AsyncCallback<PreferencesDTO>() {

          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Error updating application :" + caught.getMessage());
          }

          @Override
          public void onSuccess(PreferencesDTO result) {
            preferences = result;
            oldReferenceCurrencyTextBoxContent = result.getReferenceCurrency();
            resetUpdateStatus();
          }
        });
      }
    }
  }

  @UiHandler("cancelUpdatePreferences")
  void handleClickCancel(ClickEvent e) {
    resetUpdateStatus();
  }

  private void resetUpdateStatus() {
    cancelUpdatePreferences.setVisible(false);
    referenceCurrencyTextBox.setText(oldReferenceCurrencyTextBoxContent);
    editionInProgress = false;
  }

  @Override
  public boolean isEditing() {
    return this.editionInProgress;
  }

}
