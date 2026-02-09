package org.alliancegenome.curation_api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import io.quarkus.logging.Log;
import lombok.extern.jbosslog.JBossLog;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

@JBossLog
public class FileTransferHelper {

	public String saveIncomingURLFile(String url) {

		File saveFilePath = generateFilePath();

		try {
			log.info("Downloading File: " + url);
			Response response = Jsoup.connect(url).followRedirects(true).ignoreContentType(true).execute();
			URL redirectUrl = response.url();
			log.info("Saving file to local filesystem: " + saveFilePath.getAbsolutePath());
			FileUtils.copyURLToFile(redirectUrl, saveFilePath);
			if (!saveFilePath.exists() || saveFilePath.length() == 0) {
				log.error("Downloading URL failed: " + redirectUrl);
				saveFilePath.delete();
				return null;
			}
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

		if (fullFilePath == null) {
			return null;
		}

		File inFilePath = new File(fullFilePath);

		if (!inFilePath.exists() || inFilePath.length() == 0) {
			log.error("Input file does not exist");
			return null;
		}

		try {
			GZIPInputStream gs = new GZIPInputStream(new FileInputStream(inFilePath));
			gs.close();
			log.info("Input stream is compressed not compressing");
			return new File(fullFilePath).getAbsolutePath();
		} catch (IOException e) {
			log.info("Input stream not in the GZIP format, GZIP it");

			File outFilePath = generateFilePath();

			if (!compressGzipFile(inFilePath, outFilePath)) {
				return null;
			}

			log.info(inFilePath + " gzipped to " + outFilePath);
			log.info("Deleting input file: " + inFilePath);
			inFilePath.delete();

			return outFilePath.getAbsolutePath();
		}

	}

	public File downloadFileFromS3(String bucket, String fullS3Path) {

		File localOutFile = generateFilePath();

		try {
			log.info("Download file From S3: " + "s3://" + bucket + "/" + fullS3Path + " -> " + localOutFile.getAbsolutePath());

			S3AsyncClient s3 = S3AsyncClient.crtBuilder().credentialsProvider(getCredentials()).region(Region.US_EAST_1).build();
			S3TransferManager tm = S3TransferManager.builder().s3Client(s3).build();

			DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
				.getObjectRequest(b -> b.bucket(bucket).key(fullS3Path))
				.addTransferListener(LoggingTransferListener.create())  // Add listener.
				.destination(localOutFile)
				.build();

			FileDownload downloadFile = tm.downloadFile(downloadFileRequest);

			downloadFile.completionFuture().join();
			log.info("S3 Download complete");
			tm.close();
			s3.close();

			return localOutFile;
		} catch (Exception e) {
			localOutFile.delete();
			e.printStackTrace();
			return null;
		}
	}

	public String uploadFileToS3(String bucket, String prefix, String path, File inFile) {
		try {
			String fullS3Path = prefix + "/" + path;
			log.info("Uploading file to S3: " + inFile.getAbsolutePath() + " -> s3://" + bucket + "/" + fullS3Path);

			S3AsyncClient s3 = S3AsyncClient.crtBuilder().credentialsProvider(getCredentials()).region(Region.US_EAST_1).build();
			S3TransferManager tm = S3TransferManager.builder().s3Client(s3).build();

			UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
				.putObjectRequest(b -> b.bucket(bucket).key(fullS3Path))
				.addTransferListener(LoggingTransferListener.create())  // Add listener.
				.source(inFile)
				.build();

			FileUpload uploadFile = tm.uploadFile(uploadFileRequest);

			uploadFile.completionFuture().join();
			log.info("S3 Upload complete");
			tm.close();
			s3.close();

			return fullS3Path;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private AwsCredentialsProvider getCredentials() {
		Optional<String> awsProfile = ConfigProvider.getConfig().getOptionalValue("bulk.data.loads.aws.profile", String.class);
		Optional<String> accessKey = ConfigProvider.getConfig().getOptionalValue("bulk.data.loads.s3AccessKey", String.class);
		Optional<String> secretKey = ConfigProvider.getConfig().getOptionalValue("bulk.data.loads.s3SecretKey", String.class);

		if (awsProfile.isPresent() && awsProfile.get() != null) {
			Log.info("ProfileCredentialsProvider: " + awsProfile.get());
			return ProfileCredentialsProvider.builder().profileName(awsProfile.get()).build();
		} else if (accessKey.isPresent() && accessKey.get() != null && secretKey.isPresent() && secretKey.get() != null) {
			Log.info("AWSStaticCredentialsProvider: " + accessKey.get());
			return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey.get(), secretKey.get()));
		} else {
			Log.info("InstanceProfileCredentialsProvider: ");
			return InstanceProfileCredentialsProvider.builder().asyncCredentialUpdateEnabled(false).build();
		}
	}

	private File generateFilePath() {
		return new File(generateUniqueFileName());
	}

	public String generateUniqueFileName() {
		Date d = new Date();
		UUID uuid = UUID.randomUUID();
		String outFileName = "tmp_file." + uuid + ".data_" + d.getTime();
		return outFileName;
	}

	private boolean compressGzipFile(File inFile, File gzipOutFile) {
		try (FileInputStream fis = new FileInputStream(inFile); GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(gzipOutFile))) {

			byte[] buffer = new byte[4096];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				gzipOS.write(buffer, 0, len);
			}
		} catch (Exception e) {
			log.info("Deleting old file: " + gzipOutFile);
			gzipOutFile.delete();
			e.printStackTrace();
			return false;
		}
		return true;
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

	public String getMD5SumOfFile(String fullFilePath) {
		try {
			InputStream is = new FileInputStream(new File(fullFilePath));
			String md5Sum = DigestUtils.md5Hex(is);
			is.close();
			return md5Sum;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
