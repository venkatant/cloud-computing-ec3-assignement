//
// DATA LAYER - INTERACTING WITH GOOGLE CLOUD
//
package edu.bits.cloud.ec3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.logging.Logger;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

@SuppressWarnings("deprecation")
public class StorageService {

	public static final String BUCKET_NAME = "ec3assignment";

	private FileWriteChannel writeChannel = null;

	FileService fileService = FileServiceFactory.getFileService();
	private OutputStream os = null;
	private static final Logger log = Logger.getLogger(StorageService.class
			.getName());

	public void init(String fileName, String mime) {
		System.out.println("Storage service:init() method:  file name:"
				+ fileName + " and mime:" + mime);
		log.info("Storage service:init() method:  file name:" + fileName
				+ " and mime:" + mime);

		GSFileOptionsBuilder builder = new GSFileOptionsBuilder()
				.setAcl("public_read").setBucket(BUCKET_NAME).setKey(fileName)
				.setMimeType(mime);

		AppEngineFile writableFile = null;
		try {
			writableFile = fileService.createNewGSFile(builder
					.build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean lock = true;
		try {
			writeChannel = fileService.openWriteChannel(writableFile, lock);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		os = Channels.newOutputStream(writeChannel);
		
		
	}

	public void storeFile(byte[] b, int readSize) throws IOException {
			os.write(b, 0, readSize);
			os.flush();
	}

	public void destroy() throws Exception {
		log.info("Storage service: destroy() method");
		os.close();
		writeChannel.closeFinally();
	}
}
