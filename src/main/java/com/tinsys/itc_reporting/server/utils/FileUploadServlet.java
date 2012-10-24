package com.tinsys.itc_reporting.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.Ostermiller.util.BadDelimiterException;
import com.Ostermiller.util.BadQuoteException;
import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;




public class FileUploadServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private InputStream fis;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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

          //TODO zipped file management
          for (FileItem item : items) {
            // process only file upload - discard other form item types
            if (item.isFormField())
              continue;
            fis = item.getInputStream();
            LabeledCSVParser lcsvp = new LabeledCSVParser(new CSVParser(fis));
            lcsvp.changeDelimiter('\t');
            while(lcsvp.getLine() != null && lcsvp.getValueByLabel("Quantity")!=null){
/*                System.out.println(
                    "Start Date: " + lcsvp.getValueByLabel("Start Date")
                );
                System.out.println(
                    "End Date: " + lcsvp.getValueByLabel("End Date")
                );*/
            }       
              resp.flushBuffer();
            }

        } catch (Exception e) {
          resp.setContentType("text/plain");
          resp.getWriter().write("error parsing file");
          resp.flushBuffer();
          e.printStackTrace();

        }

      } else {
        resp.setContentType("text/plain");
        resp.getWriter().write("ERROR : Request contents type is not supported by the servlet.");
        resp.flushBuffer();
      }
    }
}
