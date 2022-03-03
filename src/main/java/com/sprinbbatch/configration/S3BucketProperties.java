package com.sprinbbatch.configration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3BucketProperties {

	@Value("${cloud.aws.region}")
	private String s3Region;
	@Value("${app.awsServices.bucketName}")
	private String s3BucketName;
	private Boolean isDevLocalEnv;

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	public String getS3Region() {
		return s3Region;
	}

	public void setS3Region(String s3Region) {
		this.s3Region = s3Region;
	}

	public String getS3BucketName() {
		return s3BucketName;
	}

	public void setS3BucketName(String s3BucketName) {
		this.s3BucketName = s3BucketName;
	}

	public Boolean getIsDevLocalEnv() {
		return isDevLocalEnv;
	}

	public void setIsDevLocalEnv(Boolean isDevLocalEnv) {
		this.isDevLocalEnv = isDevLocalEnv;
	}

	public S3BucketProperties() {
		super();
		// TODO Auto-generated constructor stub
	}

	public S3BucketProperties(String s3Region, String s3BucketName) {
		super();
		this.s3Region = s3Region;
		this.s3BucketName = s3BucketName;
	}

	@Override
	public String toString() {
		return "S3BucketProperties [s3Region=" + s3Region + ", s3BucketName=" + s3BucketName + ", isDevLocalEnv="
				+ isDevLocalEnv + "]";
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
