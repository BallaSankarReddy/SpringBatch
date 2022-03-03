package com.sprinbbatch.common;

import java.io.File;

import org.springframework.batch.core.JobExecution;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;

public interface S3Bucket {

	public void put(JobExecution job, String filePath, String fileName, String fileFormat);

	public void put(JobExecution job, String filePath, String fileFormat);

	public void copy(JobExecution job, String key, String destFilePath, String destFileName);

	public void removeOrDelete(JobExecution job, String key);

	public S3Object read(JobExecution job, String filePath, String fileName, String fileFormate);

	public S3Object read(JobExecution job, String key);

	public ListObjectsV2Result list(JobExecution job, String filePath);
	
	public void uploadFileFromLocalSytem(JobExecution job, String fileName,String key);

}
