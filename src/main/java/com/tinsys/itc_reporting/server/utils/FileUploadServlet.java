package com.tinsys.itc_reporting.server.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.tinsys.itc_reporting.server.service.SaleService;

public class FileUploadServlet extends HttpServlet {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  private SaleService saleService;
  private List<String> errorList = new ArrayList<String>();
  private List<String> duplicateFileCheck = new ArrayList<String>();
  private List<String> processedFiles = new ArrayList<String>();
  private List<String> notProcessedFiles = new ArrayList<String>();
  private FinancialReportFileParser fileParser;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
    saleService = (SaleService) ctx.getBean("saleService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doGet(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    if (ServletFileUpload.isMultipartContent(req)) {
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      try {
        resp.setContentType("text/html");
        @SuppressWarnings("unchecked")
        List<FileItem> items = upload.parseRequest(req);
        saleService.reset();
        errorList.clear();
        duplicateFileCheck.clear();
        processedFiles.clear();
        notProcessedFiles.clear();
        for (FileItem item : items) {
          fileParser = new FinancialReportFileParserImpl(item);
          fileParser.setSaleService(saleService);
          if (fileParser.getErrorList().size() > 0) {
            this.errorList.addAll(fileParser.getErrorList());
            notProcessedFiles.add(item.getName());
          } else {
            if (!duplicateFileCheck.contains(fileParser.getFileName())) {
              duplicateFileCheck.add(fileParser.getFileName());
              if (!fileParser.parseContent() || fileParser.getErrorList().size( )> 0) {// file not processed
                notProcessedFiles.add(item.getName());
                this.errorList.addAll(fileParser.getErrorList());
              } else {
                processedFiles.add(item.getName());
              }
            } else { 
              this.errorList.add("There's already a file with name " + fileParser.getFileName() + " in this batch");
              notProcessedFiles.add(item.getName());
            }
          }
          
        }
        resp.getOutputStream().print(processLog());

        saleService.saveOrUpdate();

        resp.flushBuffer();
      } catch (Exception e) {
        resp.setContentType("text/plain");
        resp.getOutputStream().print("error parsing file : " + e.getMessage());
        resp.flushBuffer();
        e.printStackTrace();
      }

    } else {
      resp.setContentType("text/plain");
      resp.getWriter().write("ERROR : Request contents type is not supported by the servlet.");
      resp.flushBuffer();
    }
  }

  private String processLog() {
    processedFiles.removeAll(notProcessedFiles);
    String processLog = "";
    String ls = System.getProperty("line.separator");
    processLog += "Files processed :" + ls + ls;
    for (String file : processedFiles) {
      processLog += file + ls;
    }
    processLog += ls + "Files not processed :" + ls + ls;
    for (String file : notProcessedFiles) {
      processLog += file + ls;
    }
    processLog += ls + "Errors :" + ls + ls;
    for (String error : errorList) {
      processLog += error + ls;
    }
    return processLog;
  }
}
