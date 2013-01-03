package com.tinsys.itc_reporting.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.server.service.SaleService;

public class FileUploadServlet extends HttpServlet {

  /**
     * 
     */
  private static final Logger logger = Logger.getLogger(FileUploadServlet.class);
  private static final long serialVersionUID = 1L;
  private SaleService saleService;
  private List<String> errorList = new ArrayList<String>();
  private List<String> duplicateFileCheck = new ArrayList<String>();
  private List<String> processedFiles = new ArrayList<String>();
  private List<String> notProcessedFiles = new ArrayList<String>();

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
          parseFile(item);
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

  private void parseFile(FileItem item) throws IOException {
    List<String> tmpErrorList = new ArrayList<String>();

    if (item.isFormField()) {
      return;
    }
    logger.debug("Started Parsing File " + item.getName());

    int periodIndex = item.getName().indexOf("_");
    int zoneIndex = item.getName().indexOf("_", periodIndex + 1);
    if (periodIndex == -1 || zoneIndex == -1) {
      tmpErrorList.add("Unknow file name format for file " + item.getName());
      notProcessedFiles.add(item.getName());
      logger.error("Unknow file name format for file " + item.getName());
      notProcessedFiles.add(item.getName());
      errorList.addAll(tmpErrorList);
      return;
    }
    String code = item.getName().substring(zoneIndex + 1, zoneIndex + 3);
    String fileMonth = item.getName().substring(periodIndex + 1, periodIndex + 3);
    String fileYear = item.getName().substring(periodIndex + 3, periodIndex + 5);
    if ((!fileMonth.matches("[0-9]+") || Integer.parseInt(fileMonth) < 1 || Integer.parseInt(fileMonth) > 12) || !fileYear.matches("[0-9]+")) {
      tmpErrorList.add("Period can't be parsed in the file name :" + item.getName());
      notProcessedFiles.add(item.getName());
      logger.error("Period can't be parsed in the file name :" + item.getName());
      errorList.addAll(tmpErrorList);
      return;
    }
    String fileName = item.getName().substring(0, item.getName().indexOf('.'));
    FiscalPeriod period = new FiscalPeriod();
    period.setMonth(Integer.parseInt(fileMonth));
    period.setYear(Integer.parseInt("20" + fileYear));
    if (!duplicateFileCheck.contains(fileName)) {
      duplicateFileCheck.add(fileName);
      Zone zone = saleService.findZone(code);
      period = saleService.findOrCreatePeriod(period);
      if (zone == null) {
        tmpErrorList.add("No corresponding Zone found in database for :" + code + " . File " + item.getName() + " won't be processed");
        notProcessedFiles.add(item.getName());
        errorList.addAll(tmpErrorList);
        return;
      }
      InputStream fis;
      GZIPInputStream gfis;
      LabeledCSVParser lcsvp;
      if (item.getContentType().equalsIgnoreCase("application/x-gzip")) {
        gfis = new GZIPInputStream(item.getInputStream());
        lcsvp = new LabeledCSVParser(new CSVParser(gfis));
      } else {
        fis = item.getInputStream();
        lcsvp = new LabeledCSVParser(new CSVParser(fis));
      }
      fis = item.getInputStream();
      lcsvp.changeDelimiter('\t');
      while (lcsvp.getLine() != null && lcsvp.getValueByLabel("Quantity") != null) {
        Sales tmpSale = new Sales();
        String fileApp = lcsvp.getValueByLabel("Vendor Identifier");
        Application application = saleService.findApplication(fileApp);
        if (application == null) {
          tmpErrorList.add("No corresponding Application found in database for :" + fileApp + " . File " + item.getName() + " won't be processed");
          notProcessedFiles.add(item.getName());
          continue;
        }
        tmpSale.setPeriod(period);
        tmpSale.setZone(zone);
        tmpSale.setApplication(application);
        tmpSale.setCountryCode(lcsvp.getValueByLabel("Country Of Sale"));
        tmpSale.setPromoCode(lcsvp.getValueByLabel("Promo Code"));
        if (tmpSale.getPromoCode().length() == 0) {
          tmpSale.setPromoCode(null);
        }
        tmpSale.setIndividualPrice(new BigDecimal(lcsvp.getValueByLabel("Customer Price")));
        tmpSale.setIndividualProceeds(new BigDecimal(lcsvp.getValueByLabel("Partner Share")));
        tmpSale.setSoldUnits(Integer.parseInt(lcsvp.getValueByLabel("Quantity")));
        tmpSale.setTotalPrice(tmpSale.getIndividualPrice().multiply(new BigDecimal(tmpSale.getSoldUnits())));
        tmpSale.setTotalProceeds(tmpSale.getIndividualProceeds().multiply(new BigDecimal(tmpSale.getSoldUnits())));
        if (tmpErrorList.size() == 0) {
          logger.debug(" Adding sale : " + tmpSale.toString());
          saleService.summarizeSale(tmpSale);
        }
      }
      processedFiles.add(item.getName());
      errorList.addAll(tmpErrorList);
      logger.debug("Finished Parsing File " + item.getName());
    } else {
      tmpErrorList.add("There's already a file with name " + fileName + " in this batch");
      notProcessedFiles.add(item.getName());
      logger.debug("There's already a file with name " + fileName + " in this batch");
      errorList.addAll(tmpErrorList);
    }

  }
}
