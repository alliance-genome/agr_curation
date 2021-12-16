package org.alliancegenome.curation_api.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.plugins.providers.multipart.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class FileTransferHelper {

    public String saveIncomingURLFile(String url) {

        File saveFilePath = generateFilePath();
        
        try {
            log.info("Downloading File: " + url);
            log.info("Saving file to local filesystem: " + saveFilePath.getAbsolutePath());
            FileUtils.copyURLToFile(new URL(url), saveFilePath);
            
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("Deleting old file: " + saveFilePath);
            saveFilePath.delete();
            return null;
        }
        
        return saveFilePath.getAbsolutePath();

    }
    
    public String saveIncomingFile(MultipartFormDataInput input, String formField) {
        Map<String, List<InputPart>> form = input.getFormDataMap();

        InputPart inputPart = form.get(formField).get(0);

        File saveFilePath = generateFilePath();
        
        try {
            InputStream is = inputPart.getBody(InputStream.class, null);

            log.info("Saving file to local filesystem: " + saveFilePath.getAbsolutePath());
            FileUtils.copyInputStreamToFile(is, saveFilePath);
            log.info("Save file to local filesystem complete");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("Deleting old file: " + saveFilePath);
            saveFilePath.delete();
            return null;
        }
        
        return saveFilePath.getAbsolutePath();

    }
    
    public String compressInputFile(String fullFilePath) {

        try {
            GZIPInputStream gs = new GZIPInputStream(new FileInputStream(new File(fullFilePath)));
            log.info("Input stream is compressed not compressing");
            return new File(fullFilePath).getAbsolutePath();
        } catch (IOException e) {
            log.info("Input stream not in the GZIP format, GZIP it");

            File outFilePath = generateFilePath();
            
            File inFilePath = new File(fullFilePath);
            
            if( !compressGzipFile(inFilePath, outFilePath) ) {
                return null;
            }
            
            log.info(inFilePath + " gzipped to " + outFilePath);
            log.info("Deleting input file: " + inFilePath);
            inFilePath.delete();

            return outFilePath.getAbsolutePath();
        }
        
        
    }
    
    public String getMD5SumOfGzipFile(String fullFilePath) {
        try {
            InputStream is = new GZIPInputStream(new FileInputStream(new File(fullFilePath)));
            String md5Sum = DigestUtils.md5Hex(is);
            is.close();
            return md5Sum;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    private File generateFilePath() {
        Date d = new Date();
        String outFileName = "tmp.data_" + d.getTime();
        return new File(outFileName);
    }

    private boolean compressGzipFile(File inFile, File gzipOutFile) {
        try ( FileInputStream fis = new FileInputStream(inFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(gzipOutFile)) ) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
        } catch(Exception e) {
            log.info("Deleting old file: " + gzipOutFile);
            gzipOutFile.delete();
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
