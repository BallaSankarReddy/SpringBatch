package com.sprinbbatch.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sprinbbatch.configration.S3BucketProperties;

@Component
public class S3BucketFactoryBuilder {
	
	@Autowired
	private S3BucketProperties s3BucketProperties;

	
	public S3Bucket getBucket() {
		if(s3BucketProperties!=null) {
			return new S3BucketCloud(s3BucketProperties);
			
		}
		return null;
	}
}
