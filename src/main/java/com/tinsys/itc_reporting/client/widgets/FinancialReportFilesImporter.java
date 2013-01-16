package com.tinsys.itc_reporting.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FinancialReportFilesImporter extends Composite implements WidgetSwitchManagement {

  private static Binder uiBinder = GWT.create(Binder.class);
  protected static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

  static class JsArray extends JavaScriptObject {
    protected JsArray() {
    }

    public final native int length() /*-{ return this.length; }-*/;

    public final native String get(int i) /*-{ return this[i].name;     }-*/;
  }

  @UiField
  FileUpload uploadField;

  @UiField
  FormPanel form;

  @UiField
  SubmitButton submitButton;

  @UiField
  TextArea uploadLog;

  @UiTemplate("FinancialReportFilesImporter.ui.xml")
  interface Binder extends UiBinder<Widget, FinancialReportFilesImporter> {
  }

  public FinancialReportFilesImporter() {
    initWidget(uiBinder.createAndBindUi(this));
    uploadField.getElement().setPropertyBoolean("multiple", true);
    form.setAction(UPLOAD_ACTION_URL);

    form.setEncoding(FormPanel.ENCODING_MULTIPART);
    form.setMethod(FormPanel.METHOD_POST);
    uploadField.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        JsArray fileNameArray = (JsArray) uploadField.getElement().getPropertyJSO("files");
        uploadLog.setText(" Files selected :\n");
        for (int i = 0; i < fileNameArray.length(); i++) {
          uploadLog.setText(uploadLog.getText() + fileNameArray.get(i) + "\n");
        }
      }
    });

    submitButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        form.submit();
      }
    });

    form.addSubmitHandler(new FormPanel.SubmitHandler() {
      public void onSubmit(SubmitEvent event) {
        if (uploadField.getFilename().length() == 0) {
          event.cancel();
        }
      }
    });

    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
      public void onSubmitComplete(SubmitCompleteEvent event) {
        if (event.getResults().contains("ERROR")) {

          String err = event.getResults();
          Window.alert(err);
        } else {

          uploadLog.setText(event.getResults());
        }
      }
    });

  }

  @Override
  public boolean isEditing() {
    return false;
  }

}
