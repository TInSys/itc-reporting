package com.tinsys.itc_reporting.client.widgets;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;

public class FinancialReportFilesImporter extends Composite implements WidgetSwitchManagement{

    private static Binder uiBinder = GWT.create(Binder.class);
    protected static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

    @UiField
    FileUpload uploadField;
    
    @UiField
    FormPanel form;
    
    @UiField
    SubmitButton submitButton;
    
    @UiTemplate("FinancialReportFilesImporter.ui.xml")
    interface Binder extends UiBinder<Widget, FinancialReportFilesImporter> {
    }
    
    public FinancialReportFilesImporter() {
        initWidget(uiBinder.createAndBindUi(this));
        uploadField.getElement().setPropertyBoolean("multiple", true);
        form.setAction(UPLOAD_ACTION_URL);

        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        submitButton.addClickHandler(new ClickHandler() {    
            @Override
            public void onClick(ClickEvent event) {
                form.submit();
                String element = uploadField.getElement().getAttribute("files");                
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
                
                
              }
            }
          });

  }
    
   
    @Override
    public boolean isEditing() {
        // TODO Auto-generated method stub
        return false;
    }

}
