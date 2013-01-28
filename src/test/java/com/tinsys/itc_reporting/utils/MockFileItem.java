package com.tinsys.itc_reporting.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.fileupload.FileItem;

public class MockFileItem implements FileItem {

  private URL mockUploadedFile;
  private String fileName;
  
  public MockFileItem(URL aFile, String fileName) {
    this.mockUploadedFile = aFile;
    this.fileName = fileName;
  }
  
  @Override
  public InputStream getInputStream() throws IOException {
    return new FileInputStream(this.mockUploadedFile.getFile());
  }

  @Override
  public String getContentType() {
    String tmpContentType = null;
    try {
      URLConnection uc;
      uc = this.mockUploadedFile.openConnection();
      tmpContentType =  uc.getContentType();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return tmpContentType;
  }

  @Override
  public String getName() {
    return this.fileName;
  }

  @Override
  public boolean isInMemory() {
    return false;
  }

  @Override
  public long getSize() {
    return 0;
  }

  @Override
  public byte[] get() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getString(String encoding) throws UnsupportedEncodingException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getString() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void write(File file) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void delete() {
    // TODO Auto-generated method stub

  }

  @Override
  public String getFieldName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFieldName(String name) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isFormField() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setFormField(boolean state) {
    // TODO Auto-generated method stub

  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

}
