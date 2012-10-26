package com.tinsys.itc_reporting.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
    private static final long serialVersionUID = 1L;
    private SaleService saleService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(config.getServletContext());
        saleService = (SaleService) ctx.getBean("saleService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // process only multipart requests
        if (ServletFileUpload.isMultipartContent(req)) {

            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            try {
                resp.setContentType("text/html");
                @SuppressWarnings("unchecked")
                List<FileItem> items = upload.parseRequest(req);

                resp.getOutputStream().print(
                        "Files added :" + System.getProperty("line.separator")
                                + System.getProperty("line.separator"));
                for (FileItem item : items) {
                    int periodIndex = item.getName().indexOf("_");
                    int zoneIndex = item.getName()
                            .indexOf("_", periodIndex + 1);
                    String code = item.getName().substring(zoneIndex + 1,
                            zoneIndex + 3);
                    String fileMonth = item.getName().substring(
                            periodIndex + 1, periodIndex + 3);
                    String fileYear = item.getName().substring(periodIndex + 3,
                            periodIndex + 5);
                    FiscalPeriod period = new FiscalPeriod();
                    period.setMonth(Integer.parseInt(fileMonth));
                    period.setYear(Integer.parseInt(fileYear));

                    Zone zone = saleService.findZone(code);
                    period = saleService.findPeriod(period);
                    if (item.isFormField())
                        continue;
                    InputStream fis;
                    GZIPInputStream gfis;
                    LabeledCSVParser lcsvp;
                    if (item.getContentType().equalsIgnoreCase(
                            "application/x-gzip")) {
                        gfis = new GZIPInputStream(item.getInputStream());
                        lcsvp = new LabeledCSVParser(new CSVParser(gfis));
                    } else {
                        fis = item.getInputStream();
                        lcsvp = new LabeledCSVParser(new CSVParser(fis));
                    }
                    fis = item.getInputStream();
                    lcsvp.changeDelimiter('\t');
                    while (lcsvp.getLine() != null
                            && lcsvp.getValueByLabel("Quantity") != null) {
                        Sales tmpSale = new Sales();
                        Application application = saleService
                                .findApplication(lcsvp
                                        .getValueByLabel("Vendor Identifier"));
                        tmpSale.setPeriod(period);
                        tmpSale.setZone(zone);
                        tmpSale.setApplication(application);
                        tmpSale.setCountryCode(lcsvp
                                .getValueByLabel("Country Of Sale"));
                        tmpSale.setIndividualPrice(new BigDecimal(lcsvp
                                .getValueByLabel("Customer Price")));
                        tmpSale.setSoldUnits(Integer.parseInt(lcsvp
                                .getValueByLabel("Quantity")));
                        saleService.summarizeSale(tmpSale);
                    }
                    resp.getOutputStream().print(
                            item.getName()
                                    + System.getProperty("line.separator"));
                }
                saleService.saveOrUpdate();

                resp.flushBuffer();
            } catch (Exception e) {
                resp.setContentType("text/plain");
                resp.getOutputStream().print(
                        "error parsing file : " + e.getMessage());
                resp.flushBuffer();
                e.printStackTrace();

            }

        } else {
            resp.setContentType("text/plain");
            resp.getWriter()
                    .write("ERROR : Request contents type is not supported by the servlet.");
            resp.flushBuffer();
        }
    }
}
