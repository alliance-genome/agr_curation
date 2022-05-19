package org.alliancegenome.curation_api.jobs;

import java.io.*;
import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.model.entities.curationreports.*;
import org.alliancegenome.curation_api.services.curationreports.CurationReportHistoryService;
import org.alliancegenome.curation_api.util.FileTransferHelper;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;


@ApplicationScoped
public class ReportJobExecutor {

    @Inject CurationReportHistoryService curationReportHistoryService;
    
    @ConfigProperty(name = "reports.s3Bucket")
    String s3Bucket = null;
    
    @ConfigProperty(name = "reports.s3PathPrefix")
    String s3PathPrefix = null;
    
    @ConfigProperty(name = "reports.s3AccessKey")
    String s3AccessKey = null;
    
    @ConfigProperty(name = "reports.s3SecretKey")
    String s3SecretKey = null;
    
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String db_jdbc_url = null;
    
    @ConfigProperty(name = "quarkus.datasource.username")
    String db_username = null;
    
    @ConfigProperty(name = "quarkus.datasource.password")
    String db_password = null;

    protected FileTransferHelper fileHelper = new FileTransferHelper();
    
    public void process(CurationReport curationReport) throws Exception {
        Log.info("Processing Report: " + curationReport.getName());

        EngineConfig config = new EngineConfig( );
        
        Platform.startup( config );
        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject( IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );
        IReportEngine engine = factory.createReportEngine( config );

        Log.info("Reading Report File: " + curationReport.getBirtReportFilePath());
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("reports/" + curationReport.getBirtReportFilePath());
        
        IReportRunnable design = engine.openReportDesign(inputStream);

        IRunAndRenderTask task = engine.createRunAndRenderTask(design);

        task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, ReportJobExecutor.class.getClassLoader()); 

        //Set parameter values and validate
        task.setParameterValue("db_driver", "org.postgresql.Driver");
        task.setParameterValue("db_jdbc_url", db_jdbc_url);
        task.setParameterValue("db_username", db_username);
        task.setParameterValue("db_password", db_password);
        task.validateParameters();

        CurationReportHistory history = new CurationReportHistory();
        history.setCurationReport(curationReport);
        history.setCurationReportTimestamp(LocalDateTime.now());
    
        String fileExt = "xls";
        File outputFile = new File("reports/output/" + fileHelper.generateUniqueFileName() + "." + fileExt);
        
        Log.info("Generating Report File: " + outputFile.getAbsolutePath());
        EXCELRenderOption options1 = new EXCELRenderOption();
        options1.setOutputFileName(outputFile.getAbsolutePath());
        options1.setOutputFormat(fileExt);
        task.setRenderOption(options1);
        task.run();
        
        String md5Sum = fileHelper.getMD5SumOfFile(outputFile.getAbsolutePath());
        String generatedS3Path = generateS3MD5Path(md5Sum, fileExt);
        String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, generatedS3Path, outputFile);
        history.setXlsFilePath(s3Path);
        Log.info("Cleanup file: " + outputFile.getAbsolutePath());
        outputFile.delete();
        
        fileExt = "html";
        outputFile = new File("reports/output/" + fileHelper.generateUniqueFileName() + "." + fileExt);
        
        Log.info("Generating Report File: " + outputFile.getAbsolutePath());
        HTMLRenderOption options2 = new HTMLRenderOption();
        options2.setOutputFileName(outputFile.getAbsolutePath());
        options2.setOutputFormat(fileExt);
        task.setRenderOption(options2);
        task.run();
        
        md5Sum = fileHelper.getMD5SumOfFile(outputFile.getAbsolutePath());
        generatedS3Path = generateS3MD5Path(md5Sum, fileExt);
        s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, generatedS3Path, outputFile);
        history.setHtmlFilePath(s3Path);
        Log.info("Cleanup file: " + outputFile.getAbsolutePath());
        outputFile.delete();
        
        fileExt = "pdf";
        outputFile = new File("reports/output/" + fileHelper.generateUniqueFileName() + "." + fileExt);
        
        Log.info("Generating Report File: " + outputFile.getAbsolutePath());
        PDFRenderOption options3 = new PDFRenderOption();
        options3.setOutputFileName(outputFile.getAbsolutePath());
        options3.setOutputFormat(fileExt);
        task.setRenderOption(options3);
        task.run();
        
        md5Sum = fileHelper.getMD5SumOfFile(outputFile.getAbsolutePath());
        generatedS3Path = generateS3MD5Path(md5Sum, fileExt);
        s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, generatedS3Path, outputFile);
        history.setPdfFilePath(s3Path);
        Log.info("Cleanup file: " + outputFile.getAbsolutePath());
        outputFile.delete();
        
        curationReportHistoryService.create(history);
        
        task.close();

    }

    public String generateS3MD5Path(String md5Sum, String fileExt) {
        if(md5Sum != null && md5Sum.length() > 0) {
            return md5Sum.charAt(0) + "/" + md5Sum.charAt(1) + "/" + md5Sum.charAt(2) + "/" + md5Sum.charAt(3) + "/" + md5Sum + "." + fileExt;
        } else {
            return null;
        }
    }
    

}
