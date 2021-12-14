package org.alliancegenome.curation_api.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.plugins.providers.multipart.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class FileUploadHelper {

    private MultipartFormDataInput input;
    private String formField = "";
    
    public FileUploadHelper(MultipartFormDataInput input, String formField) {
        this.input = input;
        this.formField = formField;
    }

    public String getOutputFilePath() {

        Map<String, List<InputPart>> form = input.getFormDataMap();

        String outFileName = "";

        InputPart inputPart = form.get(formField).get(0);

        Date d = new Date();
        outFileName = "tmp.data_" + d.getTime();
        File saveFilePath = new File(outFileName);

        try {
            InputStream is = inputPart.getBody(InputStream.class, null);

            log.info("Saving file to local filesystem: " + saveFilePath.getAbsolutePath());
            FileUtils.copyInputStreamToFile(is, saveFilePath);
            log.info("Save file to local filesystem complete");

            try {
                GZIPInputStream gs = new GZIPInputStream(new FileInputStream(saveFilePath));
            } catch (IOException e) {
                log.info("Input stream not in the GZIP format, GZIP it");

                String gzFileName = outFileName + ".gz";
                if( !compressGzipFile(saveFilePath, gzFileName) ) {
                    log.info("Deleting old file: " + saveFilePath);
                    saveFilePath.delete();
                    return null;
                }
                log.info("gzipped to " + gzFileName);

                // delete original uncompressed file
                log.info("Deleting old file: " + saveFilePath);
                saveFilePath.delete();
                outFileName = gzFileName;
                GZIPInputStream gs = new GZIPInputStream(new FileInputStream(gzFileName));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("Deleting old file: " + saveFilePath);
            saveFilePath.delete();
        }

        
        return new File(outFileName).getAbsolutePath();

    }
    
    private boolean compressGzipFile(File inFile, String gzipFile) {
        try ( FileInputStream fis = new FileInputStream(inFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(gzipFile)) ) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
        } catch(Exception e) {
            log.info("Deleting old file: " + gzipFile);
            new File(gzipFile).delete();
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
