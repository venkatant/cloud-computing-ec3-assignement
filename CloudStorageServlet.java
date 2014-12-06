//
//  BUSINESS LOGIC LAYER
//
package edu.bits.cloud.ec3;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class CloudStorageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private StorageService storage = new StorageService();
	private static final int BUFFER_SIZE = 1024 * 1024;
	private static final Logger log = Logger
			.getLogger(CloudStorageServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.info(this.getServletInfo() + " Servlets called....");
		resp.setContentType("text/plain");
		
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter;
		try {
			iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String fileName = item.getName();
				String mime = item.getContentType();

				storage.init(fileName, mime);
				InputStream is = item.openStream();

				byte[] b = new byte[BUFFER_SIZE];
				int readBytes = is.read(b, 0, BUFFER_SIZE);
				boolean isUploadFailed = false;
				try {
					while (readBytes != -1) {
						storage.storeFile(b, readBytes);
						readBytes = is.read(b, 0, readBytes);
					}
				} catch (IOException ie) {
					isUploadFailed = true;
				} finally {
					is.close();
					storage.destroy();
				}
				
				if(isUploadFailed) {
					resp.getWriter().println("File upload failed...");
				} else {
					resp.getWriter().println("File uploading done successfully");
				}
				
				log.info(this.getServletName() + " ended....");

			}
		} catch (FileUploadException e) {
			System.out.println("FileUploadException::" + e.getMessage());
			log.severe(this.getServletName() + ":FileUploadException::"
					+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(this.getServletName() + ":Exception::" + e.getMessage());
			System.out.println("Exception::" + e.getMessage());
			e.printStackTrace();
		}
	}

}
