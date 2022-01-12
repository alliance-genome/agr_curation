package org.alliancegenome.curation_api.util;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.*;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.plugins.providers.multipart.*;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.transfer.*;

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
            gs.close();
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
    
    public File downloadFileFromS3(String AWSAccessKey, String AWSSecretKey, String bucket, String prefix, String path) {
        
        File localOutFile = generateFilePath();
        
        try {
            String fullS3Path = prefix + "/" + path;
            log.info("Download file From S3: " + "s3://" + bucket + "/" + fullS3Path + " -> " + localOutFile.getAbsolutePath());
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWSAccessKey, AWSSecretKey))).withRegion(Regions.US_EAST_1).build();
            TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
            final Download downloadFile = tm.download(bucket, fullS3Path, localOutFile);
            downloadFile.waitForCompletion();
            tm.shutdownNow();
            log.info("S3 Download complete");
            s3.shutdown();
            return localOutFile;
        } catch (Exception e) {
            localOutFile.delete();
            e.printStackTrace();
            return null;
        }
    }
    
    public String uploadFileToS3(String AWSAccessKey, String AWSSecretKey, String bucket, String prefix, String path, File inFile) {
        try {
            String fullS3Path = prefix + "/" + path;
            log.info("Uploading file to S3: " + inFile.getAbsolutePath() + " -> s3://" + bucket + "/" + fullS3Path);
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWSAccessKey, AWSSecretKey))).withRegion(Regions.US_EAST_1).build();
            TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
            final Upload uploadFile = tm.upload(bucket, fullS3Path, inFile);
            uploadFile.waitForCompletion();
            tm.shutdownNow();
            log.info("S3 Upload complete");
            s3.shutdown();
            return fullS3Path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private File generateFilePath() {
        Date d = new Date();
        UUID uuid = UUID.randomUUID();
        String outFileName = "tmp_file." + uuid + ".data_" + d.getTime();
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
