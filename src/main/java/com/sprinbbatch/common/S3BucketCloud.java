package com.sprinbbatch.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.sprinbbatch.SpringBatchApplication;
import com.sprinbbatch.configration.S3BucketProperties;

public class S3BucketCloud implements S3Bucket {
	
	
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(S3BucketCloud.class);

	private S3BucketProperties s3BucketProperties;

	
	@Autowired
	private AmazonS3Client amazonS3Client;
	
	public S3BucketCloud(S3BucketProperties s3BucketProperties) {
		this.s3BucketProperties = s3BucketProperties;
	}

	public S3BucketCloud() {
		super();
	}



	@Override
	public void put(JobExecution job, String filePath, String fileName, String fileFormat) {
		LOGGER.info("S3Region::{}",s3BucketProperties.getS3Region());
		LOGGER.info("S3BucketName::{}",s3BucketProperties.getS3BucketName());

		String basePath = System.getProperty("user.home");

		File file = new File(basePath + "/" + fileName);

		if (file.exists()) {

			AmazonS3 build = s3ClientBuilder();

			PutObjectRequest request = new PutObjectRequest(s3BucketProperties.getS3BucketName(), filePath + fileName,
					file);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(fileFormat);
			request.setMetadata(metadata);
			PutObjectResult result = build.putObject(request);

			if (null != result) {
				LOGGER.info("done ....." + job.getStatus());
			}

		}
	}

	private AmazonS3 s3ClientBuilder() {
		
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(s3BucketProperties.getAccessKey(), s3BucketProperties.getSecretKey());
		
		AmazonS3Client client = new AmazonS3Client(basicAWSCredentials);
		client.withRegion(Region.getRegion(Regions.fromName(s3BucketProperties.getS3Region())));
		return client;
	}

	@Override
	public void put(JobExecution job, String fileName, String fileFormat) {

	//	String basePath = System.getProperty("user.home");

		AmazonS3 build = s3ClientBuilder();

		File file = new File(fileName);
		if (file.exists()) {

			PutObjectRequest request = new PutObjectRequest(s3BucketProperties.getS3BucketName(),  fileFormat,
					file);

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(fileFormat);

			PutObjectResult result = build.putObject(request);
			if (null != result) {
				LOGGER.info("done ....." + job.getStatus());
			}

		}

	}

	@Override
	public void copy(JobExecution job, String key, String destFilePath, String destFileName) {

		
		LOGGER.info("S3Region::{}",s3BucketProperties.getS3Region());
		LOGGER.info("S3BucketName::{}",s3BucketProperties.getS3BucketName());
		LOGGER.info("S3Key::{}",key); // here key nothing but a from folder location path.
		LOGGER.info("S3DestFilePath::{}",destFilePath); // here destFilePath is  nothing but a where we going to upload file folder location path.
		LOGGER.info("S3DestFileName::{}",destFileName);// here destFileName is  nothing but a  uploading file name.

		try {

			AmazonS3 build = s3ClientBuilder();
			// CopyObjectRequest
			CopyObjectRequest request = new CopyObjectRequest(s3BucketProperties.getS3BucketName(), key,
					s3BucketProperties.getS3BucketName(), destFilePath + destFileName);
			CopyObjectResult result = build.copyObject(request);
			if (null != result) {
				LOGGER.info("done ....." + job.getStatus());
			}
		}catch (AmazonClientException e) {
			 LOGGER.error("Amazon Client Exception....{}",e.getMessage());
		}
		
		catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("File copy processing error..{}",e.getMessage());
		}

		

		


	}

	@Override
	public void removeOrDelete(JobExecution job, String key) {

		AmazonS3 build = s3ClientBuilder();

		build.deleteObject(new DeleteObjectRequest(s3BucketProperties.getS3BucketName(), key));
	}

	@Override
	public S3Object read(JobExecution job, String filePath, String fileName, String fileFormate) {

		AmazonS3 build = s3ClientBuilder();

		S3Object object = build
				.getObject(new GetObjectRequest(s3BucketProperties.getS3BucketName(), filePath + fileName));
		return object;

	}

	@Override
	public S3Object read(JobExecution job, String key) {

		AmazonS3 build = s3ClientBuilder();

		S3Object object = build.getObject(new GetObjectRequest(s3BucketProperties.getS3BucketName(), key));
		return object;
	}

	@Override
	public ListObjectsV2Result list(JobExecution job, String filePath) {

		AmazonS3 build = s3ClientBuilder();
		
		
		  ListObjectsV2Result result = build.listObjectsV2(new ListObjectsV2Request().withBucketName(s3BucketProperties.getS3BucketName()).withPrefix(filePath));
		//ListObjectsV2Result result = build.listObjectsV2(s3BucketProperties.getS3BucketName(), filePath);

		return result;

	}

	@Override
	public void uploadFileFromLocalSytem(JobExecution job, String fileName, String key) {
		LOGGER.info("S3Region::{}",s3BucketProperties.getS3Region());
		LOGGER.info("S3BucketName::{}",s3BucketProperties.getS3BucketName());

		//String basePath = System.getProperty("user.home");
		AmazonS3 build = s3ClientBuilder();
		
		
		File file = new File(fileName);
		
		
	    try  {
	    	@SuppressWarnings("resource")
			BufferedReader br        = new BufferedReader(new FileReader(file));
	    	
	    	String st;
	        // Condition holds true till
	        // there is character in a string
	        while ((st = br.readLine()) != null) {
	 
	           
	            @SuppressWarnings("resource")
				FileOutputStream iofs = new FileOutputStream(file);
	            iofs.write(st.getBytes());
	    }
	    	
	    	build.putObject(s3BucketProperties.getS3BucketName(), key +  file.getName(), file);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
	}
}